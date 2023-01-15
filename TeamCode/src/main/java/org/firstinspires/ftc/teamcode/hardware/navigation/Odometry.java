package org.firstinspires.ftc.teamcode.hardware.navigation;

import com.arcrobotics.ftclib.geometry.Pose2d;
import com.arcrobotics.ftclib.geometry.Rotation2d;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.arcrobotics.ftclib.hardware.motors.MotorEx;
import com.arcrobotics.ftclib.hardware.motors.Motor.Encoder;
import com.arcrobotics.ftclib.kinematics.HolonomicOdometry;
import com.qualcomm.robotcore.hardware.Servo;

import java.io.Serializable;

public class Odometry {

    private final MotorEx front_left;
    private final MotorEx front_right;
    private final MotorEx back_left;
    private final MotorEx back_right;
    private final Servo center_odometry;
    private final Servo left_odometry;
    private final Servo right_odometry;

    private final HolonomicOdometry odometry;

    private final Encoder left_odometer;
    private final Encoder right_odometer;
    private final Encoder center_odometer;

    private final double TRACKWIDTH = 9.12;
    private final double CENTER_WHEEL_OFFSET = -6.089;
    private final double WHEEL_DIAMETER = 1.37795;
    private final double TICKS_PER_REV = 8192;
    private final double DISTANCE_PER_PULSE = Math.PI * WHEEL_DIAMETER / TICKS_PER_REV;

    public Odometry(MotorEx front_left, MotorEx front_right, MotorEx back_left, MotorEx back_right, Servo center_odometry,Servo left_odometry,Servo right_odometry) {
        this.front_left = front_left;
        this.front_right = front_right;
        this.back_left = back_left;
        this.back_right = back_right;
        this.center_odometry = center_odometry;
        this.left_odometry = left_odometry;
        this.right_odometry = right_odometry;

        left_odometer = back_left.encoder.setDistancePerPulse(DISTANCE_PER_PULSE);
        right_odometer = front_left.encoder.setDistancePerPulse(DISTANCE_PER_PULSE);
        center_odometer = back_right.encoder.setDistancePerPulse(DISTANCE_PER_PULSE);

        left_odometer.setDirection(MotorEx.Direction.REVERSE);

        odometry = new HolonomicOdometry(
                left_odometer::getDistance,
                right_odometer::getDistance,
                center_odometer::getDistance,
                TRACKWIDTH, CENTER_WHEEL_OFFSET
        );

        resetEncoders();

        Pose2d start_pose = new Pose2d(0,0, new Rotation2d(Math.toRadians(0)));
        odometry.updatePose(start_pose);
    }

    public void updatePose() {
        odometry.updatePose();
    }

    public void updatePose(Pose2d pose) {
        odometry.updatePose(pose);
    }

    public Pose2d getPose() {
        return odometry.getPose();
    }

    public void resetEncoders() {
        left_odometer.reset();
        right_odometer.reset();
        center_odometer.reset();
    }

    public void Up() {
        center_odometry.setPosition(0);
        left_odometry.setPosition(0.137);
        right_odometry.setPosition(1);
    }

    public void Down() {
        center_odometry.setPosition(0.34);
        left_odometry.setPosition(0.566);
        right_odometry.setPosition(0.63);
    }

}
