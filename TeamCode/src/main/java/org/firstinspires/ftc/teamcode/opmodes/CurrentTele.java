package org.firstinspires.ftc.teamcode.opmodes;

import com.google.gson.JsonObject;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.util.Configuration;
import org.firstinspires.ftc.teamcode.util.Scheduler;
import org.firstinspires.ftc.teamcode.util.Storage;
import org.firstinspires.ftc.teamcode.util.Time;
import org.firstinspires.ftc.teamcode.util.event.EventBus;
import org.firstinspires.ftc.teamcode.util.event.EventFlow;
import org.firstinspires.ftc.teamcode.util.event.TimerEvent;
import org.firstinspires.ftc.teamcode.util.event.TriggerEvent;

@TeleOp(name="!!THE TeleOp!!")
public class CurrentTele extends OpMode {
    private Robot robot;
    private ControllerMap controllerMap;
    
    private ControllerMap.AxisEntry   ax_drive_l;
    private ControllerMap.AxisEntry   ax_drive_r;
    private ControllerMap.ButtonEntry btn_intake;
    private ControllerMap.AxisEntry   ax_turret;
    private ControllerMap.ButtonEntry btn_lift;
    private ControllerMap.ButtonEntry btn_shooter;
    private ControllerMap.ButtonEntry btn_pusher;
    
    private double driveSpeed;
    private double lastUpdate;
    
    private boolean lift_up = false;
    private boolean shooter_on = false;
    
    @Override
    public void init()
    {
        robot = new Robot(hardwareMap);
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
        controllerMap.setAxisMap  ("drive_l", "gamepad1", "left_stick_y" );
        controllerMap.setAxisMap  ("drive_r", "gamepad1", "right_stick_y");
        controllerMap.setButtonMap("intake",  "gamepad1", "right_trigger");
        controllerMap.setAxisMap  ("turret",  "gamepad2", "left_stick_x" );
        controllerMap.setButtonMap("lift",    "gamepad1", "right_bumper" );
        controllerMap.setButtonMap("shooter", "gamepad2", "y");
        controllerMap.setButtonMap("pusher",  "gamepad2", "b");
        
        ax_drive_l = controllerMap.axes.get("drive_l");
        ax_drive_r = controllerMap.axes.get("drive_r");
        btn_intake = controllerMap.buttons.get("intake");
        ax_turret  = controllerMap.axes.get("turret");
        btn_lift   = controllerMap.buttons.get("lift");
        btn_shooter= controllerMap.buttons.get("shooter");
        btn_pusher = controllerMap.buttons.get("pusher");
    
        JsonObject config = Configuration.readJson(Storage.getFile("teleop.json"));
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
        robot.drivetrain.telemove(ax_drive_l.get() * driveSpeed, ax_drive_r.get() * driveSpeed);
        
        if (btn_intake.get()) robot.intake.intake();
        else                  robot.intake.stop();
        
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
    }
}
