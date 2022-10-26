package org.firstinspires.ftc.teamcode.opmodes.teleop;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Lift;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.util.Logger;

public class LiftControl extends ControlModule {

    private Lift lift;
    private Logger log = new Logger("Lift Control");

    double x = 0;
    double y = 0;
    //Arm1 = 488.89580 mm
    //Arm2 = 424.15230 mm


    public LiftControl(String name) {
        super(name);
    }

    @Override
    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager) {
        this.lift = robot.lift;
        x = 0;
        y = 0;

    }

    @Override
    public void update(Telemetry telemetry) {

    }
}
