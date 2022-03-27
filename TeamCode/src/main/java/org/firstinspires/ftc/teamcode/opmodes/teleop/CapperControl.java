package org.firstinspires.ftc.teamcode.opmodes.teleop;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Capper;
import org.firstinspires.ftc.teamcode.hardware.Duck;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;

public class EndgameControl extends ControlModule {
    ControllerMap.AxisEntry left_trigger;
    ControllerMap.AxisEntry right_trigger;

    ControllerMap.AxisEntry right_stick_x;
    ControllerMap.AxisEntry right_stick_y;
    ControllerMap.AxisEntry left_stick_y;

    ControllerMap.ButtonEntry right_bumper;

    Duck duck;
    Capper capper;

    private boolean endgame = false;

    public EndgameControl(String name) {
        super(name);
    }

    @Override
    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager) {
        duck = robot.duck;
        capper = robot.capper;

        left_trigger = controllerMap.getAxisMap("duck:left", "gamepad2", "left_trigger");
        right_trigger = controllerMap.getAxisMap("duck:right", "gamepad2", "right_trigger");

        right_stick_x = controllerMap.getAxisMap("tape:swivel", "gamepad1", "right_stick_x");
        right_stick_y = controllerMap.getAxisMap("tape:tilt", "gamepad1", "right_stick_y");
        left_stick_y = controllerMap.getAxisMap("tape:extend", "gamepad1", "left_stick_y");

        right_bumper = controllerMap.getButtonMap("endgame", "gamepad1", "right_bumper");

        capper.init();
    }

    @Override
    public void update(Telemetry telemetry) {
        if (right_bumper.edge() == -1) endgame = !endgame;

        if (left_trigger.get() > right_trigger.get()) {
            duck.spin(left_trigger.get());
        } else {
            duck.spin(-right_trigger.get());
        }

        if (endgame) {
            capper.extend(left_stick_y.get());
            capper.adjust(right_stick_y.get(), right_stick_x.get());
            telemetry.addData("Tape Extension", capper.extension);
        }
    }
}
