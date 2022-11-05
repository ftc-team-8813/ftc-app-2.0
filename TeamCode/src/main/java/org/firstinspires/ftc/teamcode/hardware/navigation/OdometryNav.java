package org.firstinspires.ftc.teamcode.hardware.navigation;

import com.arcrobotics.ftclib.geometry.Pose2d;
import com.arcrobotics.ftclib.geometry.Rotation2d;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.arcrobotics.ftclib.hardware.motors.MotorEx;
import com.arcrobotics.ftclib.hardware.motors.Motor.Encoder;
import com.arcrobotics.ftclib.kinematics.HolonomicOdometry;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.DcMotorEx;

public class OdometryNav {

    private final MotorEx front_left;
    private final MotorEx front_right;
    private final MotorEx back_left;
    private final MotorEx back_right;

    private HolonomicOdometry odometry;

    private Encoder left_odometer;
    private Encoder right_odometer;
    private Encoder center_odometer;

    private final double TRACKWIDTH = 10.07570866;
    private final double CENTER_WHEEL_OFFSET = 5.027;
    private final double WHEEL_DIAMETER = 1.37795;
    private final double TICKS_PER_REV = 8192;
    private final double DISTANCE_PER_PULSE = Math.PI * WHEEL_DIAMETER / TICKS_PER_REV;

    public OdometryNav(MotorEx front_left, MotorEx front_right, MotorEx back_left, MotorEx back_right) {
        this.front_left = front_left;
        this.front_right = front_right;
        this.back_left = back_left;
        this.back_right = back_right;

        left_odometer = front_left.encoder.setDistancePerPulse(DISTANCE_PER_PULSE);
        right_odometer = back_right.encoder.setDistancePerPulse(DISTANCE_PER_PULSE);
        center_odometer = back_left.encoder.setDistancePerPulse(DISTANCE_PER_PULSE);

//        right_odometer.setDirection(Motor.Direction.REVERSE);

        odometry = new HolonomicOdometry(
                left_odometer::getDistance,
                right_odometer::getDistance,
                center_odometer::getDistance,
                TRACKWIDTH, CENTER_WHEEL_OFFSET
        );

        left_odometer.reset();
        right_odometer.reset();
        center_odometer.reset();

        Pose2d start_pose = new Pose2d(0,0,new Rotation2d(Math.toRadians(45.0)));
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

}
