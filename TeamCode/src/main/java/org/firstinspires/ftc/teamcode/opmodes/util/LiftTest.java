package org.firstinspires.ftc.teamcode.opmodes.util;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;

@TeleOp(name="Lift Test")
public class LiftTest extends OpMode
{
    
    private Robot robot;
    private ControllerMap controllerMap;

    private ControllerMap.ButtonEntry btn_enable;
    private ControllerMap.ButtonEntry btn_home;
    private ControllerMap.AxisEntry ax_lift;
    private ControllerMap.AxisEntry ax_grab;
    
    @Override
    public void init()
    {
        robot = new Robot(hardwareMap);
        controllerMap = new ControllerMap(gamepad1, gamepad2);
        
        controllerMap.setButtonMap("enable", "gamepad1", "right_trigger");
        controllerMap.setButtonMap("home", "gamepad1", "y");
        controllerMap.setAxisMap("lift", "gamepad1", "left_stick_y");
        controllerMap.setAxisMap("grab", "gamepad1", "right_stick_y");
        
        btn_enable = controllerMap.buttons.get("enable");
        btn_home = controllerMap.buttons.get("home");
        ax_lift = controllerMap.axes.get("lift");
        ax_grab = controllerMap.axes.get("grab");
    }
    
    @Override
    public void loop()
    {
        robot.lift.hold = btn_enable.get();
        double lift_tgt_new = robot.lift.liftTarget - 0.005 * ax_lift.get();
        double grab_tgt_new = robot.lift.grabTarget - 0.005 * ax_grab.get();
        
        if (lift_tgt_new < 0) robot.lift.liftTarget = 0;
        else if (lift_tgt_new > 0.17) robot.lift.liftTarget = 0.17;
        else robot.lift.liftTarget = lift_tgt_new;
        
        if (grab_tgt_new < 0) robot.lift.grabTarget = 0;
        else if (grab_tgt_new > 0.5) robot.lift.grabTarget = 0.5;
        else robot.lift.grabTarget = grab_tgt_new;
        
        if (btn_home.edge() > 0) robot.lift.home_stage = 1;
        
        telemetry.addData("Lift target", "%.3f", robot.lift.liftTarget);
        telemetry.addData("Grab target", "%.3f", robot.lift.grabTarget);
        telemetry.addData("Hold", robot.lift.hold);
        
        robot.lift.update(telemetry);
    }
}
