package org.firstinspires.ftc.teamcode.opmodes.teleop;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Lift;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;

public class LiftTest extends ControlModule{
    private Robot robot;
    private Lift lift;

    private ControllerMap.AxisEntry lift_left_y;
    private ControllerMap.AxisEntry lift_left_x;

    public LiftTest(String name) {
        super(name);
    }

    @Override
    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager) {

    }


    @Override
    public void update(Telemetry telemetry) throws InterruptedException {

    }
}
