package org.firstinspires.ftc.teamcode.opmodes.teleop;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Hoist;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.opmodes.LoggingOpMode;

public class HoistControl extends ControlModule {

    private Hoist hoist;
    private ControllerMap.ButtonEntry y_button;
    private ControllerMap.ButtonEntry hookDown;



    public HoistControl(String name) {
        super(name);
    }

    @Override
    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager) {
        hoist = robot.hoist;
        y_button = controllerMap.getButtonMap("hang", "gamepad2", "y");
        hookDown = controllerMap.getButtonMap("hookDown", "gamepad2", "a");

    }

    @Override
    public void update(Telemetry telemetry) {
        if(y_button.edge() == -1){
            hoist.setHoist(1);
        }

    }
}
