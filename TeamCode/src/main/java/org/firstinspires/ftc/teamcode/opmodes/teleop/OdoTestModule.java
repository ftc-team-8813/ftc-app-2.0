package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.arcrobotics.ftclib.geometry.Pose2d;
import com.arcrobotics.ftclib.geometry.Rotation2d;
import com.arcrobotics.ftclib.kinematics.HolonomicOdometry;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.DistanceSensors;
import org.firstinspires.ftc.teamcode.hardware.Drivetrain;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;

public class OdoTestModule extends ControlModule{

    public HolonomicOdometry odometry;
    private Pose2d currentPose;
    private Drivetrain drivetrain;
    private DistanceSensors sensors;
    private ControllerMap.AxisEntry ax_drive_left_x;
    private ControllerMap.AxisEntry ax_drive_left_y;
    private ControllerMap.AxisEntry ax_drive_right_x;
    private int pocket = 0;

    public OdoTestModule(String name) {
        super(name);
    }

    @Override
    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager) {
        odometry = robot.odo;

        odometry.updatePose(new Pose2d(0, 0, new Rotation2d(0)));

        currentPose = odometry.getPose();

        this.drivetrain = robot.drivetrain;

        ax_drive_left_x = controllerMap.getAxisMap("drive:left_x", "gamepad1", "left_stick_x");
        ax_drive_left_y = controllerMap.getAxisMap("drive:right_y", "gamepad1", "left_stick_y");
        ax_drive_right_x = controllerMap.getAxisMap("drive:right_x", "gamepad1", "right_stick_x");
    }

    @Override
    public void init_loop(Telemetry telemetry) {
        super.init_loop(telemetry);
        if(sensors.getRightDistance() > 400 && sensors.getRightDistance() < 700){
            pocket = 1;
        }

        odometry.updatePose(new Pose2d(0, 0, new Rotation2d(0)));

        telemetry.addData("Odo Pose", currentPose);
        telemetry.addData("Pocket", pocket);
    }

    @Override
    public void update(Telemetry telemetry) {
        odometry.updatePose();
        currentPose = odometry.getPose();
        drivetrain.moveRobotCentric(ax_drive_left_x.get(), -ax_drive_left_y.get(), ax_drive_right_x.get());

        telemetry.addData("Odometry Pose", currentPose);
    }
}
