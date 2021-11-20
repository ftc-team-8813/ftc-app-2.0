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

    private boolean pods_up = true;


    public OdometryControl(String name) {
        super(name);
    }


    @Override
    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager) {
        this.odometry = robot.odometry;

        btn_release_odo_a = controllerMap.getButtonMap("odo:x", "gamepad1","x");
        btn_release_odo_b = controllerMap.getButtonMap("odo:y", "gamepad1","y");
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

        double[] odo_data = odometry.getOdoData();
        telemetry.addData("Y Coord: ", odo_data[0]);
        telemetry.addData("X Coord: ", odo_data[1]);
        telemetry.addData("Heading: ", odo_data[2]);

        double[] curr_poses = odometry.getCurrentPositions();
        telemetry.addData("Left Enc: ", curr_poses[0]);
        telemetry.addData("Right Enc: ", curr_poses[1]);
        telemetry.addData("Front Enc: ", curr_poses[2]);
    }
}
