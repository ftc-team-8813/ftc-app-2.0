package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.CalibratedAnalogInput;
import org.firstinspires.ftc.teamcode.hardware.Drivetrain;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.hardware.navigation.Odometry;
import org.firstinspires.ftc.teamcode.input.ControllerMap;

public class DriveControl extends ControlModule{
    private Drivetrain drivetrain;
    private Odometry odometry;
    private ControllerMap.AxisEntry ax_drive_right_x;
    private ControllerMap.AxisEntry ax_drive_right_y;
    private ControllerMap.AxisEntry ax_drive_left_x;

    public DriveControl(String name) {
        super(name);
    }

    @Override
    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager) {
        this.drivetrain = robot.drivetrain;
        this.odometry = robot.odometry;
        ax_drive_right_x = controllerMap.getAxisMap("drive:right_x", "gamepad1", "right_stick_x");
        ax_drive_right_y = controllerMap.getAxisMap("drive:right_y", "gamepad1", "right_stick_y");
        ax_drive_left_x = controllerMap.getAxisMap("drive:left_x", "gamepad1", "left_stick_x");
    }

    @Override
    public void update(Telemetry telemetry) {
        drivetrain.telemove(-ax_drive_right_y.get(), ax_drive_right_x.get(), ax_drive_left_x.get());
        odometry.update();

        telemetry.addData("Forward Dist: ", odometry.forward);
        telemetry.addData("Side Dist: ", odometry.side);
        telemetry.addData("Turn Dist: ", odometry.heading);

        double[] curr_poses = odometry.getCurrentPositions();
        telemetry.addData("Left Enc: ", curr_poses[0]);
        telemetry.addData("Right Enc: ", curr_poses[1]);
        telemetry.addData("Front Enc: ", curr_poses[2]);

        telemetry.update();
    }
}
