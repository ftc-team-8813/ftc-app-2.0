package org.firstinspires.ftc.teamcode.opmodes.teleop;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Mode;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;

public class ModeControl extends ControlModule{

    private Mode mode;

    private ControllerMap.ButtonEntry y_button;
    private ControllerMap.ButtonEntry b_button;
    private ControllerMap.ButtonEntry a_button;

    public ModeControl(String name) {
        super(name);
    }

    @Override
    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager) {

        y_button = controllerMap.getButtonMap("intake:fast","gamepad2","y");
        b_button = controllerMap.getButtonMap("intake:circuit","gamepad2","b");
        a_button = controllerMap.getButtonMap("intake:ground","gamepad2","a");
    }

    @Override
    public void update(Telemetry telemetry) {

        if (y_button.edge() == -1) {
            mode.set("Fast");
        }

        if (b_button.edge() == -1) {
            mode.set("Circuit");
        }

        if (a_button.edge() == -1) {
            mode.set("Ground");
        }
    }
}
