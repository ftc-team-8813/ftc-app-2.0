package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.hardware.bosch.BNO055IMU;

import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.teamcode.hardware.events.IMUEvent;
import org.firstinspires.ftc.teamcode.util.Logger;
import org.firstinspires.ftc.teamcode.util.Scheduler;
import org.firstinspires.ftc.teamcode.util.Storage;
import org.firstinspires.ftc.teamcode.util.event.EventBus;
import org.firstinspires.ftc.teamcode.util.event.TimerEvent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class IMU
{

    //Modes
    public static final int PRE_INIT = 0;
    public static final int CALIBRATING = 1;
    public static final int INITIALIZED = 2;
    public static final int STARTED = 3;
    public static final int CLOSED = -2;
    public static final int ERROR = -1;
    private static final String[] statuses = {
            "pre-init", "calibrating", "initialized", "started", "error", "closed"
    };

    //The IMU
    private BNO055IMU imu;
    //Its parameters
    private BNO055IMU.Parameters params;
    //The logger
    private Logger log;
    private Worker worker;
    private Scheduler.Timer workerInterval;
    private EventBus.Subscriber<TimerEvent> workerSub;
    private EventBus evBus;


    public IMU(BNO055IMU imu)
    {
        this.imu = imu;
        log = new Logger("IMU Wrapper");
        worker = new Worker(); // Just create the worker object here to avoid NPE's when checking state
    }

    public BNO055IMU getInternalImu()
    {
        return imu;
    }

    /**
     * Worker Thread -- bad
     * Initializes the IMU asynchronously and then continuously polls the sensor, counting revolutions.
     */
    private class Worker implements EventBus.SubCallback<TimerEvent>
    {
        private Logger log;
        private File calibrationFile = Storage.getFile("imu_calibration.json");

        private final String[] internalStatus =
                {"Idle", "Error", "Initializing peripherals", "Initializing system", "Self-test",
                        "Running w/fusion", "Running"};

        private final String[] errors =
                {"[none]", "Peripheral error", "System error", "Self-test", "Register map value out of range",
                        "Register map address out of range", "Register map write error", "Low-power mode not available",
                        "Accel power mode not available", "Fusion config error", "Sensor config error"};

        private int prevStatus, prevError;

        private boolean inRadians;

        private float lastAngle;
        private float angleOffset;
        private int revolutions;

        private long lastLog;
        private int updateCount;
        private boolean autoCalibrating;

        private int state = PRE_INIT;
        private String detailStatus = "";

        private float heading, roll, pitch;

        private Scheduler scheduler;

        protected Worker()
        {
            log = new Logger("IMU Worker");
        }

        private void update()
        {
            BNO055IMU.SystemStatus status = imu.getSystemStatus();
            if (prevStatus != (int) status.bVal)
            {
                prevStatus = (int) status.bVal;
                detailStatus = internalStatus[prevStatus];

                if (prevStatus == 1) // status == ERROR
                {
                    BNO055IMU.SystemError error = imu.getSystemError();
                    if (prevError != (int) error.bVal)
                    {
                        prevError = (int) error.bVal;
                        detailStatus = internalStatus[prevStatus] + ": " + errors[prevError];
                    }
                }
                log.d("Status: %s", detailStatus);
            }

            Orientation o = imu.getAngularOrientation();
            float h = o.firstAngle;
            float r = o.secondAngle;
            float p = o.thirdAngle;
            if (inRadians)
            {
                h = (float) Math.toDegrees(h);
                r = (float) Math.toDegrees(roll);
                p = (float) Math.toDegrees(pitch);
            }
            roll = r;
            pitch = p;
            float delta = h - lastAngle;
            if (delta < -300)
            {
                //Looped past 180 to -179
                revolutions++;
            }
            else if (delta > 300)
            {
                //Looped past -179 to 180
                revolutions--;
            }
            lastAngle = h;
            heading = h + 360 * revolutions - angleOffset;
        }

        @Override
        public void run(TimerEvent ev, EventBus bus, EventBus.Subscriber<TimerEvent> sub)
        {
            if (state > CLOSED)
            {
                switch (state)
                {
                    case PRE_INIT:
                    {
                        // OK to initialize; user starts this thread with initialize()
                        log.d("====Initializing IMU====");
                        log.d("Reading calibration file...");
                        detailStatus = "Reading calibration";
                        try (Scanner scan = new Scanner(calibrationFile))
                        {
                            if (calibrationFile.exists())
                            {
                                String data = scan.useDelimiter("\\Z").next();
                                params.calibrationData = BNO055IMU.CalibrationData.deserialize(data);
                            }
                            else
                            {
                                log.d("File does not exist!");
                                autoCalibrating = true;
                            }
                        }
                        catch (IOException e)
                        {
                            log.w("Unable to read calibration file");
                            log.w(e);
                            autoCalibrating = true;
                        }

                        log.d("Initializing IMU [WILL BLOCK EVENT LOOP]");
                        detailStatus = "Initializing";
                        imu.initialize(params);
                        setState(CALIBRATING);
                        break;
                    }
                    case CALIBRATING:
                    {
                        int progress = (imu.getCalibrationStatus().calibrationStatus >> 4) & 3;
                        detailStatus = "Calibrating--Progress: " + progress;
                        if (progress == 3)
                        {
                            autoCalibrating = false;
                            try (FileWriter writer = new FileWriter(calibrationFile))
                            {
                                String data = imu.readCalibrationData().serialize();
                                writer.write(data);
                            }
                            catch (IOException e)
                            {
                                log.e("Unable to write calibration file");
                                log.e(e);
                            }

                            detailStatus = "Initialized";
                            setState(INITIALIZED);

                            Scheduler.Timer resetTimer = scheduler.addFutureTrigger(0.5, "Reset Delay");
                            evBus.subscribe(TimerEvent.class, (ev2, bus2, sub2) -> {
                                resetHeading();
                                setState(STARTED);
                            }, "Reset Heading", resetTimer.eventChannel);
                        }
                        break;
                    }
                    case INITIALIZED:
                    case STARTED:
                    {
                        update();
                        break;
                    }
                }
            }
            else
            {
                bus.unsubscribe(sub);
            }
        }

        public double getHeading()
        {
            return heading;
        }

        public double getRoll()
        {
            return roll;
        }

        public double getPitch()
        {
            return pitch;
        }

        public void setState(int state)
        {
            evBus.pushEvent(new IMUEvent(this.state, state));
            this.state = state;
        }

        public int getState()
        {
            return state;
        }

        public String getDetailStatus()
        {
            return detailStatus;
        }

        public void resetHeading()
        {
            angleOffset = lastAngle;
            revolutions = 0;
        }
    }

    public void initialize(EventBus evBus, Scheduler scheduler)
    {
        if (worker.getState() > PRE_INIT) // is it already initialized/initializing?
        {
            log.w("Already initialized!");
            return;
        }
        //Set up
        params = new BNO055IMU.Parameters();
        params.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        params.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        params.mode = BNO055IMU.SensorMode.IMU;

        this.evBus = evBus;
        worker.scheduler = scheduler;
        workerInterval = scheduler.addRepeatingTrigger(0.02, "IMU Worker Timer");
        workerSub = evBus.subscribe(TimerEvent.class, worker, "IMU Worker", workerInterval.eventChannel);
    }

    public int getStatus()
    {
        return worker.getState();
    }

    public String getStatusString()
    {
        int status = getStatus();
        if (status == ERROR) return statuses[4];
        if (status == CLOSED) return statuses[5];
        return statuses[status];
    }

    public String getDetailStatus()
    {
        return worker.getDetailStatus();
    }

    public double getHeading()
    {
        return worker.getHeading();
    }

    public double getRoll()
    {
        return worker.getRoll();
    }

    public double getPitch()
    {
        return worker.getPitch();
    }

    public void resetHeading()
    {
        worker.resetHeading();
    }

    public void stop()
    {
        if (worker.getState() > CLOSED)
        {
            worker.setState(CLOSED);
        }
    }
}
