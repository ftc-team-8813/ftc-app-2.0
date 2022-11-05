package org.firstinspires.ftc.teamcode.opmodes.auto;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.arcrobotics.ftclib.command.OdometrySubsystem;
import com.arcrobotics.ftclib.geometry.Pose2d;
import com.arcrobotics.ftclib.geometry.Rotation2d;
import com.arcrobotics.ftclib.kinematics.HolonomicOdometry;
import com.arcrobotics.ftclib.kinematics.Odometry;
import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DistanceSensor;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.opmodes.LoggingOpMode;

@TeleOp(name= "Odometry Test")
public class odometryTest extends LoggingOpMode {

    private Robot robot;
    private TelemetryPacket packet = new TelemetryPacket();

    @Override
    public void init() {
        super.init();
        robot = Robot.initialize(hardwareMap);
        robot.drivetrain.makeOdometry(telemetry);
        robot.drivetrain.setOriginalPoseForOdometry();

        packet.put("x", 3.7);
        packet.put("status", "alive");

        packet.fieldOverlay()
            .setFill("blue")
            .fillRect(-20, -20, 40, 40);

        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
    }

    @Override
    public void loop() {
        telemetry.addData("X", robot.drivetrain.getOdometry().getX());
        telemetry.addData("Y", robot.drivetrain.getOdometry().getY());
        telemetry.addData("Heading", robot.drivetrain.getOdometry().getHeading());
        robot.drivetrain.updateOdometry(telemetry);
        telemetry.update();
    }
}
