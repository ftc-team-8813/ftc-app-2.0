package org.firstinspires.ftc.teamcode.opmodes;

import com.google.gson.JsonObject;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.hardware.events.LiftEvent;
import org.firstinspires.ftc.teamcode.hardware.events.TurretEvent;
import org.firstinspires.ftc.teamcode.hardware.tracking.Tracker;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.util.Scheduler;
import org.firstinspires.ftc.teamcode.util.Time;
import org.firstinspires.ftc.teamcode.util.event.EventBus;
import org.firstinspires.ftc.teamcode.util.event.EventBus.Subscriber;
import org.firstinspires.ftc.teamcode.util.event.EventFlow;
import org.firstinspires.ftc.teamcode.util.event.TimerEvent;
import org.firstinspires.ftc.teamcode.util.event.TriggerEvent;

@TeleOp(name="!!THE TeleOp!!")
public class CurrentTele extends LoggingOpMode {
    private Robot robot;
    private Tracker tracker;
    private ControllerMap controllerMap;
    
    private ControllerMap.AxisEntry   ax_drive_l;
    private ControllerMap.AxisEntry   ax_drive_r;
    private ControllerMap.AxisEntry   ax_intake;
    private ControllerMap.AxisEntry   ax_intake_out;
    private ControllerMap.AxisEntry   ax_turret;
    private ControllerMap.AxisEntry   ax_turret_reverse;
    private ControllerMap.ButtonEntry btn_lift;
    private ControllerMap.ButtonEntry btn_shooter;
    private ControllerMap.ButtonEntry btn_pusher;
    private ControllerMap.ButtonEntry btn_wobble_up;
    private ControllerMap.ButtonEntry btn_wobble_down;
    private ControllerMap.ButtonEntry btn_wobble_open;
    private ControllerMap.ButtonEntry btn_wobble_close;
    private ControllerMap.ButtonEntry btn_slow;
    private ControllerMap.ButtonEntry btn_wobble_int;
    private ControllerMap.ButtonEntry btn_turret_home;
    private ControllerMap.ButtonEntry btn_shooter_preset;
    private ControllerMap.ButtonEntry btn_aim;
    
    private double driveSpeed;
    private double slowSpeed;
    private double lastUpdate;
    
    private boolean lift_up = false;
    private boolean shooter_on = false;
    private boolean slow = false;
    
    private EventBus evBus;
    private Scheduler scheduler; // just in case
    private EventFlow liftFlow;
    
    private int shooterPowerIdx;
    
    private static final int TRIGGER_LIFT_FLOW = 0;
    
    @Override
    public void init()
    {
        robot = new Robot(hardwareMap);
        tracker = new Tracker(robot.turret, robot.drivetrain, 1, 0);
        evBus = new EventBus();
        scheduler = new Scheduler(evBus);
        
        liftFlow = new EventFlow(evBus);
        Scheduler.Timer liftTimer = scheduler.addPendingTrigger(0.2, "Lift Timer");
        
        liftFlow.start(new Subscriber<>(TriggerEvent.class, (ev, bus, sub) -> {
                    robot.turret.home(0);
                }, "Home Turret", TRIGGER_LIFT_FLOW))
                .then(new Subscriber<>(TurretEvent.class, (ev, bus, sub) -> {
                    robot.lift.up();
                }, "Lift Up", TurretEvent.TURRET_MOVED))
                .then(new Subscriber<>(LiftEvent.class, (ev, bus, sub) -> {
                    liftTimer.reset();
                }, "Lift Wait", LiftEvent.LIFT_MOVED))
                .then(new Subscriber<>(TimerEvent.class, (ev, bus, sub) -> {
                    robot.lift.down();
                }, "Lift Down", liftTimer.eventChannel))
                .then(new Subscriber<>(LiftEvent.class, (ev, bus, sub) -> {},
                   "Lift Finished", LiftEvent.LIFT_MOVED)); // implicitly jump to beginning
        
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
        controllerMap.setAxisMap  ("drive_l",   "gamepad1", "left_stick_y" );
        controllerMap.setAxisMap  ("drive_r",   "gamepad1", "right_stick_y");
        controllerMap.setAxisMap  ("intake",    "gamepad1", "right_trigger");
        controllerMap.setAxisMap  ("intake_out","gamepad1", "left_trigger");
        controllerMap.setAxisMap  ("turret",    "gamepad2", "left_stick_x" );
        controllerMap.setAxisMap  ("turr_reverse","gamepad2", "left_trigger");
        controllerMap.setButtonMap("lift",      "gamepad1", "right_bumper" );
        controllerMap.setButtonMap("shooter",   "gamepad2", "y");
        controllerMap.setButtonMap("pusher",    "gamepad2", "x");
        controllerMap.setButtonMap("wobble_up", "gamepad2", "dpad_up");
        controllerMap.setButtonMap("wobble_dn", "gamepad2", "dpad_down");
        controllerMap.setButtonMap("wobble_o",  "gamepad2", "dpad_left");
        controllerMap.setButtonMap("wobble_c",  "gamepad2", "dpad_right");
        controllerMap.setButtonMap("slow",      "gamepad1", "left_bumper");
        controllerMap.setButtonMap("wobble_i",  "gamepad2", "left_bumper");
        controllerMap.setButtonMap("turr_home", "gamepad2", "a");
        controllerMap.setButtonMap("shoot_pre", "gamepad2", "right_bumper");
        controllerMap.setButtonMap("aim",       "gamepad2", "b");
        
        ax_drive_l      = controllerMap.axes.get("drive_l");
        ax_drive_r      = controllerMap.axes.get("drive_r");
        ax_intake       = controllerMap.axes.get("intake");
        ax_intake_out   = controllerMap.axes.get("intake_out");
        ax_turret       = controllerMap.axes.get("turret");
        ax_turret_reverse  = controllerMap.axes.get("turr_reverse");
        btn_lift        = controllerMap.buttons.get("lift");
        btn_shooter     = controllerMap.buttons.get("shooter");
        btn_pusher      = controllerMap.buttons.get("pusher");
        btn_wobble_up   = controllerMap.buttons.get("wobble_up");
        btn_wobble_down = controllerMap.buttons.get("wobble_dn");
        btn_wobble_open = controllerMap.buttons.get("wobble_o");
        btn_wobble_close= controllerMap.buttons.get("wobble_c");
        btn_slow        = controllerMap.buttons.get("slow");
        btn_wobble_int  = controllerMap.buttons.get("wobble_i");
        btn_turret_home = controllerMap.buttons.get("turr_home");
        btn_shooter_preset = controllerMap.buttons.get("shoot_pre");
        btn_aim = controllerMap.buttons.get("aim");
    
        JsonObject config = robot.config.getAsJsonObject("teleop");
        driveSpeed = config.get("drive_speed").getAsDouble();
        slowSpeed  = config.get("slow_speed").getAsDouble();
        robot.lift.down();
        
        robot.imu.initialize(evBus, scheduler);
    }
    
    @Override
    public void start()
    {
        lastUpdate = Time.now();
    }
    
    @Override
    public void loop()
    {
        double dt = Time.since(lastUpdate);
        lastUpdate = Time.now();
        // TODO -- HACK: axes swapped due to config problem
        double speed = slow ? slowSpeed : driveSpeed;
        robot.drivetrain.telemove(ax_drive_r.get() * speed,
                                 ax_drive_l.get() * speed);
        

        robot.intake.run(ax_intake.get() - ax_intake_out.get());

        //if (btn_aim.get()){
        //    tracker.updateVars();
        //}
        double turret_adj = -ax_turret.get() * 5;
        robot.turret.rotate(robot.turret.getTarget() + turret_adj);

        if (btn_aim.get()){
            tracker.updateVars();
        }
        
        if (btn_lift.edge() > 0)
        {
            lift_up = !lift_up;
            if (lift_up) robot.lift.up();
            else         robot.lift.down();
        }
        
        if (btn_shooter.edge() > 0)
        {
            shooter_on = !shooter_on;
            if (shooter_on) robot.turret.shooter.start();
            else            robot.turret.shooter.stop();
        }
        
        if (btn_shooter_preset.edge() > 0)
        {
            shooterPowerIdx += 1;
            robot.turret.shooter.setPreset(shooterPowerIdx);
            robot.controlHub.setLEDColor(robot.turret.shooter.getPresetColor());
        }
        
        if (btn_slow.edge() > 0)
        {
            slow = !slow;
        }
        
        if (btn_pusher.get()) robot.turret.push();
        else                  robot.turret.unpush();
        
        if (btn_turret_home.edge() > 0) robot.turret.home(0);
        if (ax_turret_reverse.get() > 0.5) robot.turret.home(180);
        
        if (btn_wobble_up.get()) robot.wobble.up();
        if (btn_wobble_down.get()) robot.wobble.down();
        if (btn_wobble_open.get()) robot.wobble.open();
        if (btn_wobble_close.get()) robot.wobble.close();
        if (btn_wobble_int.get()) robot.wobble.middle();
        
        robot.lift.update(telemetry);
        robot.turret.update(telemetry);
        robot.drivetrain.getOdometry().updateDeltas();
        telemetry.addData("Shooter Velocity", "%.3f",
                ((DcMotorEx)robot.turret.shooter.motor).getVelocity());
        telemetry.addData("Shooter speed preset", robot.turret.shooter.getCurrPreset());
        telemetry.addData("Turret target heading", "%.3f", tracker.getTargetHeading());
        telemetry.addData("Odometry position", "%.3f,%.3f", robot.drivetrain.getOdometry().x, robot.drivetrain.getOdometry().y);
        telemetry.addData("Turret Current Position", robot.turret.turretFb.getCurrentPosition());
        scheduler.loop();
        evBus.update();
        // telemetry.addData("Turret power", "%.3f", robot.turret.turret.getPower());
    }
}
