package org.firstinspires.ftc.teamcode.opmodes.teleop;

import static java.lang.Math.round;

import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Lift;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.util.Status;

public class LiftControl extends ControlModule{
    private Lift lift;
    private ControllerMap.AxisEntry ax_left_stick_y;
    private ControllerMap.ButtonEntry btn_y;
    private ControllerMap.ButtonEntry btn_x;
    private ControllerMap.ButtonEntry btn_dpad_down;
    private ElapsedTime timer;


    public LiftControl(String name){super(name);}

    @Override
    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager) {
        this.lift = robot.lift;
        timer = new ElapsedTime();

        ax_left_stick_y = controllerMap.getAxisMap("lift:adjust", "gamepad2", "left_stick_y");
        btn_y = controllerMap.getButtonMap("lift:extend_high", "gamepad2", "y");
        btn_x = controllerMap.getButtonMap("lift:extend_neutral", "gamepad2", "x");
        btn_dpad_down = controllerMap.getButtonMap("lift:reset", "gamepad2", "dpad_down");
    }

    @Override
    public void update(Telemetry telemetry) {
        double delta_extension = -ax_left_stick_y.get() * Status.SENSITIVITY;

        lift.extend(lift.getTargetLiftPos() + delta_extension, false);

        if (lift.getPower() > 0.1 && lift.getCurrentLiftPos() > Status.ROTATABLE_THRESHOLD){
            lift.rotate(Status.EXTENSIONS.get("out"));
        } else if (lift.getPower() < 0.1 && lift.getCurrentLiftPos() < Status.ROTATABLE_THRESHOLD){
            lift.rotate(Status.EXTENSIONS.get("in"));
        }

        if (btn_y.get()){
            lift.extend(Status.STAGES.get("high"), false);
        } else if (btn_x.get()){
            lift.extend(Status.STAGES.get("neutral"), false);
        } else if (btn_dpad_down.get()){
            lift.extend(0, false);
        }

        lift.updateLift();
        telemetry.addData("Lift Real Pos: ", lift.getCurrentLiftPos());
        telemetry.addData("Lift Target Pos: ", lift.getTargetLiftPos());
    }
}
