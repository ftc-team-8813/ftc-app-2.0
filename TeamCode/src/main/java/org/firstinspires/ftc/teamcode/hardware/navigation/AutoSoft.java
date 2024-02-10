package org.firstinspires.ftc.teamcode.hardware.navigation;

import com.arcrobotics.ftclib.controller.PIDController;
import com.arcrobotics.ftclib.drivebase.MecanumDrive;
import com.arcrobotics.ftclib.geometry.Pose2d;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.arcrobotics.ftclib.hardware.motors.MotorEx;
import com.arcrobotics.ftclib.kinematics.HolonomicOdometry;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

public class AutoSoft {
    public MotorEx front_left;
    public MotorEx front_right;
    public MotorEx back_left;
    public MotorEx back_right;
    private final double TRACKWIDTH = 9.167;
    private final double CENTER_WHEEL_OFFSET = -6.024;
    private final double WHEEL_DIAMETER = 1.37795;
    private final double TICKS_PER_REV = 8192;
    private final double DISTANCE_PER_PULSE = Math.PI * WHEEL_DIAMETER / TICKS_PER_REV;
    private Motor.Encoder left_odometer;
    private Motor.Encoder right_odometer;
    private Motor.Encoder center_odometer;
    private MecanumDrive drive;
    private HolonomicOdometry odometry = new HolonomicOdometry(
        left_odometer::getDistance,
        right_odometer::getDistance,
        center_odometer::getDistance,
        TRACKWIDTH, CENTER_WHEEL_OFFSET
    );

    public AutoSoft(MotorEx front_left, MotorEx front_right, MotorEx back_left, MotorEx back_right){
        this.front_left = front_left;
        this.front_right = front_right;
        this.back_left = back_left;
        this.back_right = back_right;

        left_odometer = back_left.encoder.setDistancePerPulse(DISTANCE_PER_PULSE); //check if this still the same
        right_odometer = front_left.encoder.setDistancePerPulse(DISTANCE_PER_PULSE); //same with this
        center_odometer = back_right.encoder.setDistancePerPulse(DISTANCE_PER_PULSE); //same with this

        right_odometer.setDirection(MotorEx.Direction.REVERSE);
    }

    public void autoMove(Pose2d targetPose, Pose2d currentPose, Telemetry telemetry) {
         PIDController xCont = new PIDController(0,0,0);
         PIDController yCont = new PIDController(0,0,0);
         PIDController headingCont = new PIDController(0,0,0);

        double xPower = xCont.calculate(currentPose.getX(), targetPose.getX());
        double yPower = yCont.calculate(currentPose.getY(), targetPose.getY());
        double headingPower = headingCont.calculate(currentPose.getHeading(), targetPose.getHeading());

        drive.driveRobotCentric(xPower, -yPower, headingPower);

        telemetry.addData("x Power", xPower);
        telemetry.addData("y Power", yPower);
        telemetry.addData("heading Power", headingPower);
    }

    public Pose2d getPose(){
        return odometry.getPose();
    }

    public void updatePose(Pose2d pose2d){
        odometry.updatePose(pose2d);
    }


}
