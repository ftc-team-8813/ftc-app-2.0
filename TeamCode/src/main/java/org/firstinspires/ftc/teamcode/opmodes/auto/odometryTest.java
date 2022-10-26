package org.firstinspires.ftc.teamcode.opmodes.auto;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.arcrobotics.ftclib.command.OdometrySubsystem;
import com.arcrobotics.ftclib.geometry.Pose2d;
import com.arcrobotics.ftclib.geometry.Rotation2d;
import com.arcrobotics.ftclib.kinematics.HolonomicOdometry;
import com.arcrobotics.ftclib.kinematics.Odometry;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.opmodes.LoggingOpMode;

@TeleOp(name= "Odometry Test")
public class odometryTest extends LoggingOpMode {

    private Robot robot;
    private int i = 0;

    @Override
    public void init() {
        super.init();
        robot = Robot.initialize(hardwareMap);
        robot.drivetrain.makeOdometry(telemetry);
        robot.drivetrain.setOriginalPoseForOdometry();
    }

    @Override
    public void loop() {
        telemetry.addData("Pose", robot.drivetrain.getOdometry());
        robot.drivetrain.updateOdometry(telemetry);
    }
}
