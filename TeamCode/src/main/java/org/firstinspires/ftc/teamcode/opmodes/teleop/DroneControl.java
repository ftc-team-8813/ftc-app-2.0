package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.DroneLauncher;
import org.firstinspires.ftc.teamcode.hardware.Lift;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;

public class DroneControl extends ControlModule{

    public final double DroneSpinPos = 0;

    public DroneLauncher droneLauncher;

    public ControllerMap.ButtonEntry launchButton;
//    public ElapsedTime timer;

    public DroneControl(String name) {
        super(name);
    }

    @Override
    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager) {
        droneLauncher = robot.droneLauncher;

        launchButton = controllerMap.getButtonMap("droneLauncher", "gamepad1", "dpad_up");
        droneLauncher.setLaunchPos(1);

//        timer = new ElapsedTime();
    }

    @Override
    public void update(Telemetry telemetry) {

        if(launchButton.edge() == -1){
            droneLauncher.setLaunchPos(0);
        }

        telemetry.addData("Drone Position: ", droneLauncher.getLaunchPos());
        telemetry.addData("Button", launchButton);
    }
}
