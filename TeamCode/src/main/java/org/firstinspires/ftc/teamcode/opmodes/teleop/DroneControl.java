//package org.firstinspires.ftc.teamcode.opmodes.teleop;
//
//import com.qualcomm.robotcore.util.ElapsedTime;
//
//import org.firstinspires.ftc.robotcore.external.Telemetry;
//import org.firstinspires.ftc.teamcode.hardware.DroneLauncher;
//import org.firstinspires.ftc.teamcode.hardware.Lift;
//import org.firstinspires.ftc.teamcode.hardware.Robot;
//import org.firstinspires.ftc.teamcode.input.ControllerMap;
//
//public class DroneControl extends ControlModule{
//
//    public DroneLauncher droneLauncher;
//
//    public ControllerMap.ButtonEntry launchButton;
//    public ControllerMap.ButtonEntry goUp;
//
//
//    public DroneControl(String name) {
//        super(name);
//    }
//
//    @Override
//    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager) {
//        droneLauncher = robot.droneLauncher;
//
//        launchButton = controllerMap.getButtonMap("droneLauncherShoot", "gamepad2", "dpad_down");
//        goUp = controllerMap.getButtonMap("droneLauncherUp", "gamepad2", "dpad_up");
//        droneLauncher.setLaunchPos(1);
//        droneLauncher.setLauncherHeight(0.6);
//    }
//
//    @Override
//    public void update(Telemetry telemetry) {
//        if(goUp.edge() == -1){
//            droneLauncher.setLauncherHeight(0.9);
//        }
//
//        if(launchButton.edge() == -1){
//            droneLauncher.setLaunchPos(0);
//        }
//
//        telemetry.addData("Drone Position: ", droneLauncher.getLaunchPos());
//        telemetry.addData("Button", launchButton);
//    }
//}
