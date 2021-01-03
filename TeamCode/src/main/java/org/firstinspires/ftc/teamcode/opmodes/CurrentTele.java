package org.firstinspires.ftc.teamcode.opmodes;

import com.google.gson.JsonObject;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.hardware.Turret;
import org.firstinspires.ftc.teamcode.hardware.events.LiftEvent;
import org.firstinspires.ftc.teamcode.hardware.events.TurretEvent;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.util.Configuration;
import org.firstinspires.ftc.teamcode.util.Scheduler;
import org.firstinspires.ftc.teamcode.util.Storage;
import org.firstinspires.ftc.teamcode.util.Time;
import org.firstinspires.ftc.teamcode.util.event.EventBus;
import org.firstinspires.ftc.teamcode.util.event.EventBus.Subscriber;
import org.firstinspires.ftc.teamcode.util.event.EventFlow;
import org.firstinspires.ftc.teamcode.util.event.TimerEvent;
import org.firstinspires.ftc.teamcode.util.event.TriggerEvent;

@TeleOp(name="!!THE TeleOp!!")
public class CurrentTele extends LoggingOpMode {
    private Robot robot;
    private ControllerMap controllerMap;
    
    private ControllerMap.AxisEntry   ax_drive_l;
    private ControllerMap.AxisEntry   ax_drive_r;
    private ControllerMap.ButtonEntry btn_intake;
    private ControllerMap.ButtonEntry btn_intake_out;
    private ControllerMap.AxisEntry   ax_turret;
    private ControllerMap.ButtonEntry btn_lift;
    private ControllerMap.ButtonEntry btn_shooter;
    private ControllerMap.ButtonEntry btn_pusher;
    private ControllerMap.ButtonEntry btn_wobble_up;
    private ControllerMap.ButtonEntry btn_wobble_down;
    private ControllerMap.ButtonEntry btn_wobble_open;
    private ControllerMap.ButtonEntry btn_wobble_close;
    
    private double driveSpeed;
    private double lastUpdate;
    
    private boolean lift_up = false;
    private boolean shooter_on = false;
    
    private EventBus evBus;
    private Scheduler scheduler; // just in case
    private EventFlow liftFlow;
    
    private static final int TRIGGER_LIFT_FLOW = 0;
    
    @Override
    public void init()
    {
        robot = new Robot(hardwareMap);
        evBus = new EventBus();
        scheduler = new Scheduler(evBus);
        
        liftFlow = new EventFlow(evBus);
        Scheduler.Timer liftTimer = scheduler.addPendingTrigger(0.2, "Lift Timer");
        
        liftFlow.start(new Subscriber<>(TriggerEvent.class, (ev, bus, sub) -> {
                    robot.turret.home();
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
        controllerMap.setButtonMap("intake",    "gamepad1", "right_trigger");
        controllerMap.setButtonMap("intake_out","gamepad1", "left_trigger");
        controllerMap.setAxisMap  ("turret",    "gamepad2", "left_stick_x" );
        controllerMap.setButtonMap("lift",      "gamepad1", "right_bumper" );
        controllerMap.setButtonMap("shooter",   "gamepad2", "y");
        controllerMap.setButtonMap("pusher",    "gamepad2", "x");
        controllerMap.setButtonMap("wobble_up", "gamepad2", "dpad_up");
        controllerMap.setButtonMap("wobble_dn", "gamepad2", "dpad_down");
        controllerMap.setButtonMap("wobble_o",  "gamepad2", "dpad_left");
        controllerMap.setButtonMap("wobble_c",  "gamepad2", "dpad_right");
        
        ax_drive_l      = controllerMap.axes.get("drive_l");
        ax_drive_r      = controllerMap.axes.get("drive_r");
        btn_intake      = controllerMap.buttons.get("intake");
        btn_intake_out  = controllerMap.buttons.get("intake_out");
        ax_turret       = controllerMap.axes.get("turret");
        btn_lift        = controllerMap.buttons.get("lift");
        btn_shooter     = controllerMap.buttons.get("shooter");
        btn_pusher      = controllerMap.buttons.get("pusher");
        btn_wobble_up   = controllerMap.buttons.get("wobble_up");
        btn_wobble_down = controllerMap.buttons.get("wobble_dn");
        btn_wobble_open = controllerMap.buttons.get("wobble_o");
        btn_wobble_close= controllerMap.buttons.get("wobble_c");
    
        JsonObject config = robot.config.getAsJsonObject("teleop");
        driveSpeed = config.get("drive_speed").getAsDouble();
        
        robot.lift.down();
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
        robot.drivetrain.telemove(ax_drive_r.get() * driveSpeed, ax_drive_l.get() * driveSpeed);
        
        if (btn_intake.get())          robot.intake.intake();
        else if (btn_intake_out.get()) robot.intake.outtake();
        else                           robot.intake.stop();
        
        double turret_adj = ax_turret.get() * dt * 0.5;
        robot.turret.rotate(robot.turret.getTarget() + turret_adj);
        
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
        
        if (btn_pusher.get()) robot.turret.push();
        else                  robot.turret.unpush();
        
        if (btn_wobble_up.get()) robot.wobble.up();
        if (btn_wobble_down.get()) robot.wobble.down();
        if (btn_wobble_open.get()) robot.wobble.open();
        if (btn_wobble_close.get()) robot.wobble.close();
        
        robot.lift.update(telemetry);
        robot.turret.update(telemetry);
        scheduler.loop();
        evBus.update();
        // telemetry.addData("Turret power", "%.3f", robot.turret.turret.getPower());
    }
}
