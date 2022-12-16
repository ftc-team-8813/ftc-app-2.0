package org.firstinspires.ftc.teamcode.hardware.navigation;

import com.arcrobotics.ftclib.geometry.Pose2d;
import com.arcrobotics.ftclib.geometry.Rotation2d;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.arcrobotics.ftclib.hardware.motors.MotorEx;
import com.arcrobotics.ftclib.hardware.motors.Motor.Encoder;
import com.arcrobotics.ftclib.kinematics.HolonomicOdometry;
import com.qualcomm.robotcore.hardware.Servo;

import java.io.Serializable;

public class OdometryNav {

    private final MotorEx front_left;
    private final MotorEx front_right;
    private final MotorEx back_left;
    private final MotorEx back_right;
    private final MotorEx lift_2;
    private final Servo center_odo;
    private final Servo left_odo;
    private final Servo right_odo;

    private HolonomicOdometry odometry;

    private final Encoder left_odometer;
    private final Encoder right_odometer;
    private final Encoder center_odometer;

    private final double TRACKWIDTH = 12.5925;
    private final double CENTER_WHEEL_OFFSET = 5.89567;
    private final double WHEEL_DIAMETER = 1.37795;
    private final double TICKS_PER_REV = 8192;
    private final double DISTANCE_PER_PULSE = Math.PI * WHEEL_DIAMETER / TICKS_PER_REV;

    public OdometryNav(MotorEx front_left, MotorEx front_right, MotorEx back_left, MotorEx back_right,MotorEx lift_2,Servo center_odo,Servo left_odo,Servo right_odo) {
        this.front_left = front_left;
        this.front_right = front_right;
        this.back_left = back_left;
        this.back_right = back_right;
        this.lift_2 = lift_2;
        this.center_odo = center_odo;
        this.left_odo = left_odo;
        this.right_odo = right_odo;



        left_odometer = back_left.encoder.setDistancePerPulse(DISTANCE_PER_PULSE);
        right_odometer = lift_2.encoder.setDistancePerPulse(DISTANCE_PER_PULSE);
        center_odometer = back_right.encoder.setDistancePerPulse(DISTANCE_PER_PULSE);

        left_odometer.setDirection(MotorEx.Direction.REVERSE);

        odometry = new HolonomicOdometry(
                left_odometer::getDistance,
                right_odometer::getDistance,
                center_odometer::getDistance,
                TRACKWIDTH, CENTER_WHEEL_OFFSET
        );

        left_odometer.reset();
        right_odometer.reset();
        center_odometer.reset();

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

    public void resetOdometry() {
        left_odometer.reset();
        right_odometer.reset();
        center_odometer.reset();
    }

}
