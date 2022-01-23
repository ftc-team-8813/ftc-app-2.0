package org.firstinspires.ftc.teamcode.opmodes.teleop;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Duck;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;

public class DuckControl extends ControlModule{
    private Duck duck;
    private ControllerMap.AxisEntry ax_right_stick_y;

    public DuckControl(String name){
        super(name);
    }

    @Override
    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager) {
        this.duck = robot.duck;
        ax_right_stick_y = controllerMap.getAxisMap("duck:spin", "gamepad2", "right_stick_y");
    }

    @Override
    public void update(Telemetry telemetry) {
        duck.spin(-ax_right_stick_y.get()*1.5);
    }
}
