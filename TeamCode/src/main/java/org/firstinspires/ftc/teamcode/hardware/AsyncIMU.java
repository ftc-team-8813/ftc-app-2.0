package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.hardware.bosch.BNO055IMU;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.teamcode.util.Logger;
import org.firstinspires.ftc.teamcode.util.Storage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * Wrapper class for the BNO055 IMU to automatically set up and calibrate the IMU. <br>
 * NOTE: The gyroscope heading value is positive in the COUNTERCLOCKWISE direction. <br>
 * [Old code from Relic Recovery]
 */
// TODO: Fix any possible state handling issues with the CLOSED and ERROR states
public class AsyncIMU
{
    
    //Modes
    public static final int PRE_INIT = 0;
    public static final int INITIALIZED = 1;
    public static final int STARTED = 2;
    public static final int CLOSED = -2;
    public static final int ERROR = -1;
    
    //The IMU
    private BNO055IMU imu;
    //Its parameters
    private BNO055IMU.Parameters params;
    //The logger
    private Logger log;
    private Worker worker;
    private Thread workerThread;

    
    public AsyncIMU(BNO055IMU imu)
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
    private class Worker implements Runnable
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
        
        private volatile boolean inRadians;
    
        private float lastAngle;
        private float angleOffset;
        private int revolutions;
        
        private long lastLog;
        private int updateCount;
        private boolean autoCalibrating;
        
        private volatile int status = PRE_INIT;
        private volatile String detailStatus = "";
        
        private volatile float heading, roll, pitch;
        
        private boolean immediateStart = false;
        
        public void setImmediateStart(boolean immediateStart)
        {
            this.immediateStart = immediateStart;
        }
    
        private void update()
        {
            BNO055IMU.SystemStatus status = imu.getSystemStatus();
            if (prevStatus != (int)status.bVal)
            {
                prevStatus = (int)status.bVal;
                detailStatus = internalStatus[prevStatus];
                
                if (prevStatus == 1) // status == ERROR
                {
                    BNO055IMU.SystemError error = imu.getSystemError();
                    if (prevError != (int)error.bVal)
                    {
                        prevError = (int)error.bVal;
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
            } else if (delta > 300)
            {
                //Looped past -179 to 180
                revolutions--;
            }
            lastAngle = h;
            heading = h + 360 * revolutions - angleOffset;
        }
        
        @Override
        public void run()
        {
            log = new Logger("IMU Worker Thread");
            while (status > CLOSED)
            {
                switch (status)
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
                        
                        log.d("Initializing IMU");
                        detailStatus = "Initializing";
                        imu.initialize(params);
                        
                        while (autoCalibrating)
                        {
                            log.d("Running auto-calibration");
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
                                break;
                            }
                            
                            try
                            {
                                Thread.sleep(100);
                            }
                            catch (InterruptedException e)
                            {
                                status = ERROR;
                                break;
                            }
                        }
                        
                        detailStatus = "Initialized";
                        if (immediateStart) status = STARTED;
                        else status = INITIALIZED;
                        break;
                    }
                    case STARTED:
                    {
                        update();
                        /*
                        updateCount++;
                        if (System.currentTimeMillis() - lastLog > 1000)
                        {
                            log.d("Average update rate: %d fps", updateCount);
                            updateCount = 0;
                            lastLog = System.currentTimeMillis();
                        }
                         */
                        break;
                    }
                }
                try
                {
                    Thread.sleep(100);
                }
                catch (InterruptedException e)
                {
                    log.i("Interrupted");
                    status = CLOSED;
                    break;
                }
            }
        }
        
        public synchronized double getHeading()
        {
            return heading;
        }
        
        public synchronized double getRoll()
        {
            return roll;
        }
        
        public synchronized double getPitch()
        {
            return pitch;
        }
        
        public synchronized void setStatus(int status)
        {
            this.status = status;
        }
        
        public synchronized int getStatus()
        {
            return status;
        }
        
        public synchronized String getDetailStatus()
        {
            return detailStatus;
        }
        
        public synchronized void resetHeading()
        {
            angleOffset = lastAngle;
            revolutions = 0;
        }
    }
    
    public void initialize()
    {
        if (worker.getStatus() >= INITIALIZED)
        {
            log.w("Already initialized!");
            return;
        }
        //Set up
        params = new BNO055IMU.Parameters();
        params.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        params.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        params.mode = BNO055IMU.SensorMode.IMU;
    
        workerThread = new Thread(worker, "IMU Worker Thread");
        workerThread.setDaemon(true);
        workerThread.start(); // Initialize automatically
    }
    
    public void waitForInit(Telemetry telemetry) throws InterruptedException
    {
        Telemetry.Item item = null;
        while (worker.getStatus() < INITIALIZED)
        {
            if (telemetry != null)
            {
                item = telemetry.addData("IMU Initialization status", worker.getDetailStatus());
                telemetry.update();
            }
            Thread.sleep(10);
        }
        if (item != null)
        {
            telemetry.addData("IMU initialization status", worker.getDetailStatus());
            telemetry.update();
        }
    }
    
    public void setImmediateStart(boolean immediateStart)
    {
        if (worker.getStatus() < INITIALIZED) worker.setImmediateStart(immediateStart);
    }
    
    public void start()
    {
        start(false);
    }
    
    public void start(boolean inRadians)
    {
        worker.inRadians = inRadians;
        if (worker.getStatus() < INITIALIZED)
        {
            log.f("start() called before initialization complete!");
            throw new IllegalStateException("start() called before initialization complete!");
        }
        if (worker.getStatus() == STARTED)
        {
            log.d("Trying to start IMU even though it is already running");
            return;
        }
        worker.setStatus(STARTED);
    }

    public int getStatus()
    {
        return worker.getStatus();
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
        if (worker.getStatus() > CLOSED)
        {
            if (worker.getStatus() > PRE_INIT) workerThread.interrupt();
            worker.setStatus(CLOSED);
        }
    }
}
