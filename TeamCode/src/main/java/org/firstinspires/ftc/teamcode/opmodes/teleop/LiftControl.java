package org.firstinspires.ftc.teamcode.opmodes.teleop;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Lift;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.util.Status;

public class LiftControl extends ControlModule {
    private Lift lift;

    ControllerMap.AxisEntry left_stick_x;
    ControllerMap.AxisEntry right_stick_y;

    private int id;
    private double preset_rotate;
    private double preset_extend;

    public LiftControl(String name) {
        super(name);
    }

    @Override
    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager) {
        this.lift = robot.lift;

        left_stick_x = controllerMap.getAxisMap("lift:rotate", "gamepad2", "left_stick_x");
        right_stick_y = controllerMap.getAxisMap("lift:raise", "gamepad2", "right_stick_y");
    }

    @Override
    public void init_loop(Telemetry telemetry) {
        super.init_loop(telemetry);
        lift.resetLift();
    }

    @Override
    public void update(Telemetry telemetry) {
        lift.raise(lift.lift_target + (right_stick_y.get() * 250));
//        lift.rotate(lift.pivot_target + (left_stick_x.get() * 100));

//        switch (id){
//            case 0:
//                lift.raise(Status.PITSTOP, true);
//                if (lift.liftReached()) id += 1;
//                break;
//            case 1:
//                lift.rotate(preset_rotate, true);
//                if (lift.pivotReached()) id += 1;
//                break;
//            case 2:
//                lift.raise(preset_extend, true);
//                if (lift.pivotReached()) id += 1;
//                break;
//        }

        telemetry.addData("Lift Current: ", lift.getLiftPosition());
        telemetry.addData("Lift Target: ", lift.lift_target);
        telemetry.addData("Pivot Current: ", lift.getPivotPosition());
        telemetry.addData("Pivot Target: ", lift.pivot_target);
        telemetry.addData("Lift Limit Pressed: ", lift.lift_limit.getState());
        lift.update();
    }
}
