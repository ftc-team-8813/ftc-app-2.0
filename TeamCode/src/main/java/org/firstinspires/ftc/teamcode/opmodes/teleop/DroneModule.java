package org.firstinspires.ftc.teamcode.opmodes.teleop;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.DroneLauncher;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;

public class DroneModule extends ControlModule{

    public final double DroneSpinPos = 0.9;

    public DroneLauncher droneLauncher;

    public ControllerMap.ButtonEntry launchButton;

    public DroneModule(String name) {
        super(name);
    }

    @Override
    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager) {
        droneLauncher = robot.droneLauncher;
        launchButton = controllerMap.getButtonMap("DroneLauncher", "gamepad1", "y");
    }

    @Override
    public void update(Telemetry telemetry) {
        droneLauncher.setLaunchPos(DroneSpinPos);
        if(launchButton.edge() == -1){
            droneLauncher.setLaunchPos(DroneSpinPos);
        }

        telemetry.addData("Drone Position: ", droneLauncher.getLaunchPos());
    }
}
