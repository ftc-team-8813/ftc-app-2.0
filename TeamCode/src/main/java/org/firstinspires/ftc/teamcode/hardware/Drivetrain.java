package org.firstinspires.ftc.teamcode.hardware;

import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.geometry.Pose2d;
import com.arcrobotics.ftclib.geometry.Rotation2d;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.navigation.MotionProfile;
import org.firstinspires.ftc.teamcode.hardware.navigation.Odometry;
import org.firstinspires.ftc.teamcode.hardware.navigation.PID;

@Config
public class Drivetrain {

    /*
    hits the pole at the beginning
    * Forward Oscillations
    * drives slow towards the conoe stack
    * doesn't get close enough for preload
    lift dooesn't go high enough ever
    wobbles side to side while going to the cone stack
    arm goes too low for first cone
    * */

    private final DcMotorEx front_left;
    private final DcMotorEx front_right;
    private final DcMotorEx back_left;
    private final DcMotorEx back_right;
    private final BNO055IMU imu;
    private boolean has_reached;

    public static double forward_kp = 0.06;
    public static double forward_ki = 0;
    public static double forward_kd = 0.0105;
    public static double forward_a = 0.8;
    public static double strafe_kp = 0.07;
    public static double strafe_ki = 0;
    public static double strafe_kd = 0.02;
    public static double strafe_a = 0.8;
    public static double turn_kp = 0.007;
    public static double turn_ki = 0.12;
    public static double turn_kd = 0.0028;
    public static double turn_a = 0.8;
    public static double turn_max_i_sum = 1;
    public static double turn_clip = 1;

    public static double cs_turn_kp = 0.0058;
    public static double cs_turn_ki = 0.09;
    public static double cs_strafe_kp = 0.063;

    private final PID forward_pid = new PID(forward_kp,forward_ki,forward_kd,0,0,forward_a);
    private final PID strafe_pid = new PID(strafe_kp,strafe_ki,strafe_kd,0,0,strafe_a);
    private final PID turn_pid = new PID(turn_kp,turn_ki,turn_kd,0,turn_max_i_sum,turn_a);

    public static double rise_slope = 0.1;
    public static double fall_slope = 0.00000000000000000000001;
    public static double minimum = 0;
    public static double maximum = 1;
    public static double feed_forward = 1;

    private final MotionProfile strafe_cs = new MotionProfile(rise_slope,fall_slope,minimum,maximum);

    private double forward = 0;
    private double strafe = 0;
    private double turn = 0;
//    private double turn_correct = 0;
    private double forward_error_band = 0;
    private double strafe_error_band = 0;
    private double turn_error_band = 0;

    private boolean move = true;

    private double y;
    private double x;
    private double rot;
    private double forward_power;
    private double strafe_power;
    private double turn_power;
    private double botHeading;
    private double rotX;
    private double rotY;
    private double denominator;
    private double forward_error;
    private double strafe_error;
    private double turn_error;

    private double heading_delta;
    private double heading_was;

    public Drivetrain(DcMotorEx front_left, DcMotorEx front_right, DcMotorEx back_left, DcMotorEx back_right, BNO055IMU imu) {
        this.front_left = front_left;
        this.front_right = front_right;
        this.back_left = back_left;
        this.back_right = back_right;
        this.imu = imu;

        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();
        parameters.gyroRange = BNO055IMU.GyroRange.DPS2000;
        imu.initialize(parameters);

        front_right.setDirection(DcMotorSimple.Direction.REVERSE);
        back_right.setDirection(DcMotorSimple.Direction.REVERSE);

        front_left.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        front_right.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        back_left.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        back_right.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public void resetEncoders() {
        front_right.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        back_right.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        front_left.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        back_left.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        front_right.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        back_right.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        front_left.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        back_left.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void move(double forward, double strafe, double turn, double turn_correct) {
        front_left.setPower((forward + strafe + (turn + turn_correct)));
        front_right.setPower((forward - strafe - (turn + turn_correct)));
        back_left.setPower((forward - strafe + (turn + turn_correct)));
        back_right.setPower((forward + strafe - (turn + turn_correct)));
    }

    public void move(double forward, double strafe, double turn, double turn_correct, double denominator) {
        front_left.setPower(((forward + strafe + (turn + turn_correct)) / denominator));
        front_right.setPower(((forward - strafe - (turn + turn_correct)) / denominator));
        back_left.setPower(((forward - strafe + (turn + turn_correct)) / denominator));
        back_right.setPower(((forward + strafe - (turn + turn_correct)) / denominator));
    }

    public void stop() {
        front_left.setPower(0);
        front_right.setPower(0);
        back_left.setPower(0);
        back_right.setPower(0);
        move = false;
    }

    public boolean hasReached() {
        return has_reached;
    }

    public void autoMove(double forward, double strafe, double turn, double forward_error_band, double strafe_error_band, double turn_error_band, Pose2d odo, Telemetry telemetry) {

        double heading = getHeading();

        has_reached = false;

        this.forward = forward;
        this.strafe = strafe;
        this.turn = turn;
        this.forward_error_band = forward_error_band;
        this.strafe_error_band = strafe_error_band;
        this.turn_error_band = turn_error_band;

        y = odo.getX();
        x = odo.getY();
        rot = 0.0;

        if(Math.signum(-heading) == -1) {
            rot = ((-heading) + 360);
        }
        else {
            rot = -heading;
        }

        rot %= 360;

        if (Math.abs(turn - rot) > Math.abs(turn - (rot-360))) {
            rot -= 360;
        }

        forward_error = Math.abs(forward - y);
        strafe_error = Math.abs(strafe - x);
        turn_error = Math.abs(turn - rot);

        if((forward_error <= forward_error_band) && (strafe_error <= strafe_error_band) && (turn_error <= turn_error_band)){
            has_reached = true;
        }

        telemetry.addData("F Error",forward_error);
        telemetry.addData("S Error",strafe_error);
        telemetry.addData("T Error",turn_error);

    }

    public void update(Pose2d odo, Telemetry telemetry, boolean motionProfile, int id, boolean rise, boolean fall) {
        double heading = getHeading();

        heading_delta = heading - heading_was;
        turn_error = Math.abs(turn - rot);

        if (turn_error != 0) {
            heading_delta = 0;
        }

        if (heading_delta > 300) {
            heading_delta -= 360;
        }
        if (heading_delta < -300) {
            heading_delta += 360;
        }

        y = odo.getX();
        x = odo.getY();
        rot = 0.0;

        if(Math.signum(-heading) == -1) {
            rot = ((-heading) + 360);
        }
        else {
            rot = -heading;
        }

        rot %= 360;

        if (Math.abs(turn - rot) > Math.abs(turn - (rot-360))) {
            rot -= 360;
        }

        if (motionProfile) {
            turn_pid.setKp(cs_turn_kp);
            turn_pid.setKi(cs_turn_ki);
            strafe_pid.setKp(cs_strafe_kp);
            feed_forward = 1;
        }
        else {
            turn_pid.setKp(turn_kp);
            turn_pid.setKi(turn_ki);
            strafe_pid.setKp(strafe_kp);
            feed_forward = 0;
        }

        forward_power = forward_pid.getOutPut(forward,y,0);
        strafe_power = strafe_pid.getOutPut(strafe,x,feed_forward);
        turn_power = Range.clip((turn_pid.getOutPut(turn, rot, 0)),-turn_clip,turn_clip);

        botHeading = -1* Math.toRadians(heading);

        rotX = /*0.4 **/ (strafe_power * Math.cos(botHeading) - forward_power * Math.sin(botHeading));
        rotY = /*0.4 **/ (strafe_power * Math.sin(botHeading) + forward_power * Math.cos(botHeading));

        if (motionProfile) {

            strafe_cs.updateMotionProfile(id,rise,fall);
            strafe_error = Math.abs(strafe - x);

            rotY = strafe_cs.getProfiledPower(strafe_error, rotY,0);
//            rotY = Range.clip((strafe_power * Math.sin(botHeading) + forward_power * Math.cos(botHeading)),-0.2,0.4);

        }


        denominator = Math.max(Math.abs(forward_power) + Math.abs(strafe_power) + Math.abs(turn_power), 1);

        if (move) {
            move(rotY, rotX, turn_power, (heading_delta * 0.001), denominator);
        }

        move = true;

        heading_was = heading;
//
//        telemetry.addData("F Power",forward_power);
//        telemetry.addData("S Power",strafe_power);
//        telemetry.addData("T Power",turn_power);
//        telemetry.addData("F Current",y);
//        telemetry.addData("S Current",x);
//        telemetry.addData("T Current",rot);
//        telemetry.addData("Forward kP",forward_kp);
//        telemetry.addData("Strafe kP",strafe_kp);
//        telemetry.addData("Turn kP",turn_kp);
//        telemetry.addData("Turn Clip",turn_clip);
//        telemetry.addData("Rotation",-odo.getRotation().getDegrees());
//        telemetry.addData("RotY",rotY);
//        telemetry.addData("RotX",rotX);hh
//        telemetry.addData("Has Reached",has_reached);
    }

    public void updateHeading(Odometry odometry, Telemetry telemetry) {
        odometry.updatePose(new Pose2d(odometry.getPose().getX(),odometry.getPose().getY(), new Rotation2d(Math.toRadians(-imu.getAngularOrientation().firstAngle))));
        telemetry.addData("Pose", odometry.getPose());
        odometry.updatePose(-getHeading());
    }

//    public double getForwardPosition() {
//        return (front_left.getCurrentPosition() + front_right.getCurrentPosition() + back_left.getCurrentPosition() + back_right.getCurrentPosition()) / 4.0;
//    }
//
//    public double getStrafePosition() {
//        return (front_left.getCurrentPosition() - front_right.getCurrentPosition() - back_left.getCurrentPosition() + back_right.getCurrentPosition()) / 4.0;
//    }

    public double getHeading() {
        return imu.getAngularOrientation().firstAngle;
    }
}
