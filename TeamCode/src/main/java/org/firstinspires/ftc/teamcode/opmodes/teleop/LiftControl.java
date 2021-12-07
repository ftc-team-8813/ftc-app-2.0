package org.firstinspires.ftc.teamcode.opmodes.teleop;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Lift;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.util.Status;

public class LiftControl extends ControlModule{
    private Lift lift;
    private ControllerMap.AxisEntry ax_left_stick_y;
    private ControllerMap.ButtonEntry btn_right_bumper;


    public LiftControl(String name){super(name);}

    @Override
    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager) {
        this.lift = robot.lift;

        ax_left_stick_y = controllerMap.getAxisMap("lift:extend", "gamepad2", "left_stick_y");
        btn_right_bumper = controllerMap.getButtonMap("lift:deposit", "gamepad2", "right_bumper");
    }

    @Override
    public void update(Telemetry telemetry) {
        double delta_extension = ax_left_stick_y.get() * Status.SENSITIVITY;
        boolean out = lift.getCurrentLiftPos() > Status.ROTATABLE_THRESHOLD;

        lift.extend(lift.getTargetLiftPos() + delta_extension, false);

        if (out){
            lift.rotate(Status.EXTENSIONS.get("out"));
        } else {
            lift.rotate(Status.EXTENSIONS.get("in"));
        }

        if (btn_right_bumper.get() && out){
            lift.deposit(Status.DEPOSITS.get("out"));
        } else {
            lift.deposit(Status.DEPOSITS.get("in"));
        }

        lift.updateLift();
        telemetry.addData("Lift Real Pos: ", lift.getCurrentLiftPos());
        telemetry.addData("Lift Target Pos: ", lift.getTargetLiftPos());
        telemetry.addData("Arm Pos :", lift.getCurrentArmPos());
    }
}
