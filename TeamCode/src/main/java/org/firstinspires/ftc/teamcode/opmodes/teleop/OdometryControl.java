package org.firstinspires.ftc.teamcode.opmodes.teleop;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Drivetrain;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.hardware.navigation.Odometry;
import org.firstinspires.ftc.teamcode.input.ControllerMap;

public class OdometryControl extends ControlModule{
    private Odometry odometry;
    private ControllerMap.ButtonEntry btn_release_odo_a;
    private ControllerMap.ButtonEntry btn_release_odo_b;
    private ControllerMap.ButtonEntry btn_increase_left;
    private ControllerMap.ButtonEntry btn_decrease_left;
    private ControllerMap.ButtonEntry btn_increase_right;
    private ControllerMap.ButtonEntry btn_decrease_right;

    private boolean pods_up;


    public OdometryControl(String name) {
        super(name);
    }


    @Override
    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager) {
        this.odometry = robot.odometry;
        btn_release_odo_a = controllerMap.getButtonMap("odo:a", "gamepad1","a");
        btn_release_odo_b = controllerMap.getButtonMap("odo:b", "gamepad1","b");
        btn_increase_left = controllerMap.getButtonMap("odo:dpad_right", "gamepad1", "dpad_right");
        btn_decrease_left = controllerMap.getButtonMap("odo:dpad_left", "gamepad1", "dpad_left");
        btn_increase_right = controllerMap.getButtonMap("odo:dpad_up", "gamepad1", "dpad_up");
        btn_decrease_right = controllerMap.getButtonMap("odo:dpad_down", "gamepad1", "dpad_down");
    }

    @Override
    public void update(Telemetry telemetry) {
        if (btn_release_odo_a.get()) {
            pods_up = false;
        } else if (btn_release_odo_b.get()){
            pods_up = true;
        }
        if (pods_up){
            odometry.podsUp();
        } else {
            odometry.podsDown();
        }

        if (btn_increase_left.get()){
            odometry.deltaPositionLeft(0.01);
        } else if (btn_decrease_left.get()){
            odometry.deltaPositionLeft(-0.01);
        }
        if (btn_increase_right.get()){
            odometry.deltaPositionRight(0.01);
        } else if (btn_decrease_right.get()){
            odometry.deltaPositionRight(-0.01);
        }
        odometry.update();

        double[] curr_poses = odometry.getCurrentPositions();
        telemetry.addData("Left Enc: ", curr_poses[0]);
        telemetry.addData("Right Enc: ", curr_poses[1]);
        telemetry.addData("Front Enc: ", curr_poses[2]);

        double[] servo_poses = odometry.getServoDropPositions();
        telemetry.addData("Left Odo Drop", servo_poses[0]);
        telemetry.addData("Right Odo Drop", servo_poses[1]);
    }
}
