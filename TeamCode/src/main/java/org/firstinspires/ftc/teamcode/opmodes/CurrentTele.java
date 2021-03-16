package org.firstinspires.ftc.teamcode.opmodes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.hardware.events.AutoShooterEvent;
import org.firstinspires.ftc.teamcode.hardware.events.CameraEvent;
import org.firstinspires.ftc.teamcode.hardware.events.NavMoveEvent;
import org.firstinspires.ftc.teamcode.hardware.events.AutoPowershotEvent;
import org.firstinspires.ftc.teamcode.hardware.autoshoot.Tracker;
import org.firstinspires.ftc.teamcode.hardware.events.RingEvent;
import org.firstinspires.ftc.teamcode.hardware.navigation.Navigator;
import org.firstinspires.ftc.teamcode.hardware.navigation.Odometry;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.util.Logger;
import org.firstinspires.ftc.teamcode.util.Persistent;
import org.firstinspires.ftc.teamcode.util.Scheduler;
import org.firstinspires.ftc.teamcode.util.Time;
import org.firstinspires.ftc.teamcode.util.event.ButtonEvent;
import org.firstinspires.ftc.teamcode.util.event.EventBus;
import org.firstinspires.ftc.teamcode.util.event.EventBus.Subscriber;
import org.firstinspires.ftc.teamcode.util.event.EventFlow;
import org.firstinspires.ftc.teamcode.util.event.TimerEvent;
import org.firstinspires.ftc.teamcode.vision.RingDetector;
import org.opencv.core.Mat;

@TeleOp(name="!!THE TeleOp!!")
public class CurrentTele extends LoggingOpMode {
    private Robot robot;
    private Navigator navigator;
    private Odometry odometry;
    private Tracker tracker;
    private ControllerMap controllerMap;
    private Logger logger;

    private RingDetector detector;
    private Mat detectorFrame;

    private ControllerMap.AxisEntry   ax_drive_l;
    private ControllerMap.AxisEntry   ax_drive_r;
    private ControllerMap.AxisEntry   ax_intake;
    private ControllerMap.AxisEntry   ax_intake_out;
    private ControllerMap.AxisEntry   ax_turret;
    private ControllerMap.AxisEntry   ax_shooter;
    private ControllerMap.AxisEntry   ax_track;
    private ControllerMap.ButtonEntry btn_shooter;
    private ControllerMap.ButtonEntry btn_pusher;
    private ControllerMap.ButtonEntry btn_wobble_up;
    private ControllerMap.ButtonEntry btn_wobble_down;
    private ControllerMap.ButtonEntry btn_wobble_open;
    private ControllerMap.ButtonEntry btn_wobble_close;
    private ControllerMap.ButtonEntry btn_slow;
    private ControllerMap.ButtonEntry btn_slow2;
    private ControllerMap.ButtonEntry btn_wobble_int;
    private ControllerMap.ButtonEntry btn_turret_home;
    private ControllerMap.ButtonEntry btn_shooter_preset;
    private ControllerMap.ButtonEntry btn_aim;
    private ControllerMap.ButtonEntry btn_powershot;
    private ControllerMap.ButtonEntry btn_shooter_move;

    private double driveSpeed;
    private double slowSpeed;
    private double lastUpdate;

    private boolean tracking = true;
    private boolean lift_up = false;
    private boolean shooter_on = false;
    private int slow = 0;
    
    private EventBus evBus;
    private Scheduler scheduler; // just in case
    private EventFlow ringFlow;
    private EventFlow powershotFlow;
    private EventFlow shooterFlow;
    private EventFlow pusherFlow;

    private int shooterPowerIdx;

    private static final int TRIGGER_PUSHER_FLOW = 0;

    // TODO Find serial
    private static final String WEBCAM_SERIAL = "1234567890";

    private double ring_count;
    
    private double[] speeds;
    private double[] powershot_angles;
    private double[] powershot_powers;

    private double past_x = 48;
    private double past_y = -48;
    private double shooter_power = 0;

    private boolean autoPowershotRunning = false;
    private boolean autoShooterMove = false;
    private int ringCount = 0;

    @Override
    public void init()
    {
        robot = new Robot(hardwareMap);
        odometry = robot.drivetrain.getOdometry();
        logger = new Logger("CurrentTele");
        // TODO load configuration for tracker
        tracker = new Tracker(robot.turret, robot.drivetrain);
        evBus = new EventBus();
        scheduler = new Scheduler(evBus);
        navigator = new Navigator(robot.drivetrain, robot.drivetrain.getOdometry(), evBus);
        navigator.setForwardSpeed(0.8);
        navigator.setTurnSpeed(0.6);

        /* Webcam webcam = Webcam.forSerial(WEBCAM_SERIAL);
        if (webcam == null) throw new IllegalArgumentException("Could not find a webcam with serial number " + WEBCAM_SERIAL);
        SimpleFrameHandler frameHandler = new SimpleFrameHandler();
        webcam.open(ImageFormat.YUY2, 800, 448, 30, frameHandler);
        detectorFrame = new Mat(800, 448, CV_8UC4);
        detector = new RingDetector(800, 448);
        */

        ringFlow = new EventFlow(evBus);
        powershotFlow = new EventFlow(evBus);
        shooterFlow = new EventFlow(evBus);
        pusherFlow = new EventFlow(evBus);
        Scheduler.Timer powershotTimer = scheduler.addPendingTrigger(0.4, "powershotTimer");
        Scheduler.Timer shooterTimer = scheduler.addPendingTrigger(2, "shooterTimer");

        JsonObject config = robot.config.getAsJsonObject("teleop");
        JsonArray powershotAngle = config.getAsJsonArray("powershot_angles");
        JsonArray powershotPowers = config.getAsJsonArray("powershot_powers");
        powershot_angles = new double[powershotAngle.size()];
        for (int i = 0; i < powershotAngle.size(); i++)
        {
            powershot_angles[i] = powershotAngle.get(i).getAsDouble();
        }
        powershot_powers = new double[powershotPowers.size()];
        for (int i = 0; i < powershotPowers.size(); i++)
        {
            powershot_powers[i] = powershotPowers.get(i).getAsDouble();
        }

        /*
            Turns on Intake
            Waits for Ring 1
            Schooches forward and back
            Waits for Ring 2
            Schooches forward and back
            Waits for Ring 3
            Schooches forward and back
            Turn on Shooter
            Turn off Intake
            Enable Tracking (Heading Only)
            Rapid Shoot (Time?)

            Diable Tracking
            Turn Shooter to Slow
            Home Turret

            Repeat
         */
        ringFlow.start(new Subscriber<>(RingEvent.class, (ev, bus, sub) -> {
                    robot.intake.intake();
                    if (ring_count == 3){
                        evBus.pushEvent(new RingEvent(RingEvent.BUCKET_FULL));
                    }
                }, "Start Ring Flow", RingEvent.TRIGGER_AUTO_RING))
                .then(new Subscriber<>(CameraEvent.class, (ev, bus, sub) -> {
                    navigator.goTo(odometry.getX() + 10, odometry.getY());
                }, "Scooch Forward for Ring", CameraEvent.FRAME_CAUGHT))
                .then(new Subscriber<>(NavMoveEvent.class, (ev, bus, sub) -> {
                    navigator.goTo(odometry.getX() - 10, odometry.getY());
                }, "Scooch Backward for Ring", NavMoveEvent.MOVE_COMPLETE))
                .then(new Subscriber<>(CameraEvent.class, (ev, bus, sub) -> {
                    ring_count++;
                    ringFlow.jump(0);
                }, "Repeat Scooching for More Rings", CameraEvent.FRAME_CAUGHT))
                .then(new Subscriber<>(RingEvent.class, (ev, bus, sub) -> {
                    robot.turret.shooter.start();
                    tracking = true;
                }, "Start Shooter", RingEvent.BUCKET_FULL));

        powershotFlow.start(new Subscriber<>(AutoPowershotEvent.class, (ev, bus, sub) -> {
                    autoPowershotRunning = true;
                    ringCount = 0;
                    robot.turret.unpush();
                    robot.turret.rotate(powershot_angles[ringCount], false);
                    robot.turret.shooter.start(powershot_powers[ringCount]);
                    shooterTimer.reset();
                }, "Start Up Shooter", AutoPowershotEvent.TRIGGER_AUTO_POWERSHOT))
                .then(new Subscriber<>(TimerEvent.class, (ev, bus, sub) -> {
                    powershotTimer.reset();
                }, "Turn Powershot", shooterTimer.eventChannel))
                .then(new Subscriber<>(TimerEvent.class, (ev, bus, sub) -> {
                    robot.turret.push();
                    powershotTimer.reset();
                }, "Shoot Powershot", powershotTimer.eventChannel))
                .then(new Subscriber<>(TimerEvent.class, (ev, bus, sub) -> {
                    robot.turret.unpush();
                    powershotTimer.reset();
                }, "Unpush", powershotTimer.eventChannel))
                .then(new Subscriber<>(TimerEvent.class, (ev, bus, sub) -> {
                    ringCount += 1;
                    if (ringCount < 3)
                    {
                        robot.turret.shooter.start(powershot_powers[ringCount]);
                        robot.turret.rotate(powershot_angles[ringCount], false);
                        powershotTimer.reset();
                        powershotFlow.jump(2);
                    }
                    else
                    {
                        robot.turret.unpush();
                        robot.turret.shooter.stop();
                        autoPowershotRunning = false;
                    }
                }, "Turn Powershot 2", powershotTimer.eventChannel));

        shooterFlow.start(new Subscriber<>(AutoShooterEvent.class, (ev, bus, sub) -> {
                        autoShooterMove = true;
                        navigator.goTo(0, 0);
                    }, "Moving to Shoot", AutoShooterEvent.TRIGGER_AUTO_SHOOTER))
                    .then(new Subscriber<>(NavMoveEvent.class, (ev, bus, sub) -> {
                        navigator.turn(180);
                    }, "Adjusting Heading", NavMoveEvent.MOVE_COMPLETE))
                    .then(new Subscriber<>(NavMoveEvent.class, (ev, bus, sub) -> {
                        autoShooterMove = false;
                    }, "Finished Moving for Shooting", NavMoveEvent.TURN_COMPLETE));

        Scheduler.Timer pushDelay = scheduler.addPendingTrigger(0.1, "Push delay");
        Scheduler.Timer unpushDelay = scheduler.addPendingTrigger(0.1, "Unpush delay");

        pusherFlow.start(new Subscriber<>(ButtonEvent.class, (ev, bus, sub) -> {
                    robot.turret.push();
                    pushDelay.reset();
                }, "Button Trigger", TRIGGER_PUSHER_FLOW))
                .then(new Subscriber<>(TimerEvent.class, (ev, bus, sub) -> {
                    robot.turret.unpush();
                    unpushDelay.reset();
                }, "Unpush", pushDelay.eventChannel))
                .then(new Subscriber<>(TimerEvent.class, (ev, bus, sub) -> {
                    robot.turret.push();
                    pushDelay.reset();
                    pusherFlow.jump(1);
                }, "Push", unpushDelay.eventChannel));

        robot.lift.connectEventBus(evBus);
        robot.turret.connectEventBus(evBus);
        
        controllerMap = new ControllerMap(gamepad1, gamepad2);
        /*
         Hardware required:
         -- drivetrain (4 motors, tank, 2 axes)
         -- intake + ramp (2 motors, 1 button)
         -- turret (1 motor + 1 potentiometer/encoder, 1 axis + closed loop control)
         -- lift (2 servos, 1 button + toggle)
         -- shooter (1 motor + speed control, 1 button + toggle; telemetry for speed output)
         -- pusher (1 servo, 1 button)
         */
        controllerMap.setAxisMap  ("drive_l",     "gamepad1", "left_stick_y" );
        controllerMap.setAxisMap  ("drive_r",     "gamepad1", "right_stick_y");
        controllerMap.setAxisMap  ("intake",      "gamepad1", "right_trigger");
        controllerMap.setAxisMap  ("intake_out",  "gamepad1", "left_trigger");
        controllerMap.setAxisMap  ("turret",      "gamepad2", "left_stick_x" );
        controllerMap.setAxisMap  ("shooter_adj", "gamepad2", "left_stick_y");
        controllerMap.setAxisMap  ("tracking",    "gamepad2","right_trigger");
        controllerMap.setButtonMap("slow2",       "gamepad1", "right_bumper" );
        controllerMap.setButtonMap("shooter",     "gamepad2", "y");
        controllerMap.setButtonMap("pusher",      "gamepad2", "x");
        controllerMap.setButtonMap("wobble_up",   "gamepad2", "dpad_up");
        controllerMap.setButtonMap("wobble_dn",   "gamepad2", "dpad_down");
        controllerMap.setButtonMap("wobble_o",    "gamepad2", "dpad_left");
        controllerMap.setButtonMap("wobble_c",    "gamepad2", "dpad_right");
        controllerMap.setButtonMap("slow",        "gamepad1", "left_bumper");
        controllerMap.setButtonMap("wobble_i",    "gamepad2", "left_bumper");
        controllerMap.setButtonMap("turr_home",   "gamepad2", "a");
        controllerMap.setButtonMap("shoot_pre",   "gamepad2", "right_bumper");
        controllerMap.setButtonMap("aim",         "gamepad2", "b");
        controllerMap.setButtonMap("powershot",   "gamepad1", "dpad_down");
        controllerMap.setButtonMap("shooter_pos", "gamepad2", "left_stick_button");

        ax_drive_l      = controllerMap.axes.get("drive_l");
        ax_drive_r      = controllerMap.axes.get("drive_r");
        ax_intake       = controllerMap.axes.get("intake");
        ax_intake_out   = controllerMap.axes.get("intake_out");
        ax_turret       = controllerMap.axes.get("turret");
        ax_shooter      = controllerMap.axes.get("shooter_adj");
        ax_track        = controllerMap.axes.get("tracking");
        btn_shooter     = controllerMap.buttons.get("shooter");
        btn_pusher      = controllerMap.buttons.get("pusher");
        btn_wobble_up   = controllerMap.buttons.get("wobble_up");
        btn_wobble_down = controllerMap.buttons.get("wobble_dn");
        btn_wobble_open = controllerMap.buttons.get("wobble_o");
        btn_wobble_close= controllerMap.buttons.get("wobble_c");
        btn_slow        = controllerMap.buttons.get("slow");
        btn_slow2       = controllerMap.buttons.get("slow2");
        btn_wobble_int  = controllerMap.buttons.get("wobble_i");
        btn_turret_home = controllerMap.buttons.get("turr_home");
        btn_shooter_preset = controllerMap.buttons.get("shoot_pre");
        btn_powershot = controllerMap.buttons.get("powershot");
        btn_aim = controllerMap.buttons.get("aim");
        btn_shooter_move = controllerMap.buttons.get("shooter_pos");

        JsonArray driveSpeeds = config.getAsJsonArray("drive_speeds");
        speeds = new double[driveSpeeds.size()];
        for (int i = 0; i < driveSpeeds.size(); i++)
        {
            speeds[i] = driveSpeeds.get(i).getAsDouble();
        }

        robot.wobble.up();
        
        robot.imu.initialize(evBus, scheduler);

        if (Persistent.get("odo_x") != null && Persistent.get("odo_y") != null){
            past_x = (double) Persistent.get("odo_x");
            past_y = (double) Persistent.get("odo_y");
        }
        logger.i("Start X: %.1f", past_x);
        logger.i("Start Y: %.1f", past_y);
        robot.drivetrain.getOdometry().setPosition(past_x, past_y);

        JsonObject trackerConf = robot.config.getAsJsonObject("tracker");
        double target_x = trackerConf.get("target_x").getAsDouble();
        double target_y = trackerConf.get("target_y").getAsDouble();
        tracker.setTarget(target_x, target_y);

        if (Persistent.get("turret_zero_found") == null)
            robot.turret.startZeroFind();
    }

    @Override
    public void init_loop()
    {
        robot.turret.updateInit(telemetry);
    }
    
    @Override
    public void start()
    {
        lastUpdate = Time.now();
        Persistent.clear();
    }
    
    private void stopAutoPowershot()
    {
        powershotFlow.forceJump(0);
        robot.turret.unpush();
        robot.turret.shooter.stop();
        autoPowershotRunning = false;
    }

    @Override
    public void loop()
    {
        double dt = Time.since(lastUpdate);
        lastUpdate = Time.now();
        double speed = speeds[slow];
        // TODO unswap control axes
        if (!autoShooterMove) {
            robot.drivetrain.telemove(ax_drive_r.get() * speed,
                                     ax_drive_l.get() * speed);
        } else if (ax_drive_r.get() > 0.1){
            autoShooterMove = false;
        }

        // ONLY FOR TESTING PURPOSES
        //shooter_power = Range.clip(shooter_power + -ax_shooter.get() * 0.005, 0, 1);
        //robot.turret.shooter.start(shooter_power);

        robot.intake.run(ax_intake.get() - ax_intake_out.get());

        //if (btn_aim.get()){
        //    tracker.updateVars();
        //

        if (autoPowershotRunning)
        {
            if (ax_turret.get() > 0.1) stopAutoPowershot();
        }
        else
        {
            double turret_adj = -ax_turret.get() * 0.003;
            robot.turret.rotate(robot.turret.getTarget() + turret_adj);
        }

        if (btn_shooter.edge() > 0)
        {
            if (autoPowershotRunning) stopAutoPowershot();
            shooter_on = !shooter_on;
            if (shooter_on) robot.turret.shooter.start();
            else            robot.turret.shooter.stop();
        }
        
        if (btn_shooter_preset.edge() > 0)
        {
            if (autoPowershotRunning) stopAutoPowershot();
            shooterPowerIdx += 1;
            robot.turret.shooter.setPreset(shooterPowerIdx);
            robot.controlHub.setLEDColor(robot.turret.shooter.getPresetColor());
        }
        
        if (btn_slow.edge() > 0)
        {
            if (slow == 0) slow = 1;
            else slow = 0;
        }
        if (btn_slow2.edge() > 0)
        {
            if (slow == 0) slow = 2;
            else slow = 0;
        }
        
        if (autoPowershotRunning)
        {
            if (btn_pusher.get()) stopAutoPowershot();
        }
        else
        {
            int pushEdge = btn_pusher.edge();
            if (pushEdge > 0) evBus.pushEvent(new ButtonEvent(TRIGGER_PUSHER_FLOW));
            else if (pushEdge < 0)
            {
                robot.turret.unpush();
                pusherFlow.forceJump(0);
            }
        }
        
        if (btn_turret_home.edge() > 0 && !tracking)
        {
            if (autoPowershotRunning) stopAutoPowershot();
            robot.turret.home();
        }

        tracking = ax_track.get() > 0.5;

        if (btn_shooter_move.edge() > 0){
            evBus.pushEvent(new AutoShooterEvent(AutoShooterEvent.TRIGGER_AUTO_SHOOTER));
        }

        if (btn_wobble_up.get()) robot.wobble.up();
        if (btn_wobble_down.get()) robot.wobble.down();
        if (btn_wobble_open.get()) robot.wobble.open();
        if (btn_wobble_close.get()) robot.wobble.close();
        if (btn_wobble_int.get()) robot.wobble.middle();

        if (btn_powershot.edge() > 0){
            evBus.pushEvent(new AutoPowershotEvent(AutoPowershotEvent.TRIGGER_AUTO_POWERSHOT));
        }
        
        robot.lift.update(telemetry);
        robot.turret.update(telemetry);
        if (autoShooterMove){
            navigator.update(telemetry);
        }
        if (tracking){
            double position = tracker.update(telemetry);
            telemetry.addData("Tracker Target Position: ", position);
        }
        robot.drivetrain.getOdometry().updateDeltas();
        telemetry.addData("Shooter Velocity", "%.3f",
                ((DcMotorEx)robot.turret.shooter.motor).getVelocity());
        telemetry.addData("Shooter power", "%.3f", robot.turret.shooter.motor.getPower());
        telemetry.addData("Shooter speed preset", robot.turret.shooter.getCurrPreset());
        telemetry.addData("Turret target heading", "%.3f", tracker.getTargetHeading());
        telemetry.addData("Odometry position", "%.3f,%.3f", robot.drivetrain.getOdometry().x, robot.drivetrain.getOdometry().y);
        telemetry.addData("Turret Current Position", robot.turret.turretFb.getCurrentPosition());
        telemetry.addData("IMU Heading: ", robot.drivetrain.getOdometry().getIMU().getHeading());
        telemetry.addData("Testing: ", tracking);
        scheduler.loop();
        evBus.update();
        // telemetry.addData("Turret power", "%.3f", robot.turret.turret.getPower());
    }
}
