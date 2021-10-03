package org.firstinspires.ftc.teamcode.opmodes.teleop;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Drivetrain;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.hardware.navigation.Odometry;
import org.firstinspires.ftc.teamcode.input.ControllerMap;

public class OdometryControl extends ControlModule{
    private Odometry odometry;
    private ControllerMap.ButtonEntry btn_release_odo_a;


    public OdometryControl(String name) {
        super(name);
    }


    @Override
    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager) {
        this.odometry = robot.odometry;
        btn_release_odo_a = controllerMap.getButtonMap("odo:a", "gamepad1","a");
    }

    @Override
    public void update(Telemetry telemetry) {
        if (btn_release_odo_a.get()){
            odometry.release();
        }
        odometry.update();

        double[] curr_poses = odometry.getCurrentPositions();
        telemetry.addData("Left Enc: ", curr_poses[0]);
        telemetry.addData("Right Enc: ", curr_poses[1]);
        telemetry.addData("Front Enc: ", curr_poses[2]);
    }
}
