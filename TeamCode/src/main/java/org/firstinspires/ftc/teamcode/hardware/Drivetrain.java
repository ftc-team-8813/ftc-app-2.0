package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.util.Storage;

public class Drivetrain {
    private final DcMotorEx front_left;
    private final DcMotorEx front_right;
    private final DcMotorEx back_left;
    private final DcMotorEx back_right;
    private final BNO055IMU imu;

    private double target_forward;
    private double target_strafe;
    private double target_turn;

    private double forward_error;
    private double strafe_error;
    private double turn_error;

    private boolean auto_moving;

    private boolean reached;

    private double deadband = 35.0;
    private double deadband_turn = 1;

    private double speed = 0.3;
    private double speed_turn = 0.3;

    private double turn_integral_sum;
    private ElapsedTime auto_loop_timer;

    private double FORWARD_KP;
    private double STRAFE_KP;
    private double TURN_KP;
    private double TURN_KI;

    public Drivetrain(DcMotorEx front_left, DcMotorEx front_right, DcMotorEx back_left, DcMotorEx back_right, BNO055IMU imu) {
        this.front_left = front_left;
        this.front_right = front_right;
        this.back_left = back_left;
        this.back_right = back_right;
        this.imu = imu;

        FORWARD_KP = Storage.getJsonValue("forward_kp");
        STRAFE_KP = Storage.getJsonValue("strafe_kp");
        TURN_KP = Storage.getJsonValue("turn_kp");
        TURN_KI = Storage.getJsonValue("turn_ki");

        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();
        parameters.gyroRange = BNO055IMU.GyroRange.DPS2000;
        imu.initialize(parameters);

        auto_loop_timer = new ElapsedTime();

        resetEncoders();

        front_right.setDirection(DcMotorSimple.Direction.REVERSE);
        back_right.setDirection(DcMotorSimple.Direction.REVERSE);

        front_left.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        front_right.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        back_left.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        back_right.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public void move(double forward, double strafe, double turn, double turn_correct) {
        front_left.setPower((forward + strafe + (turn+turn_correct)));
        front_right.setPower((forward - strafe - (turn+turn_correct)));
        back_left.setPower((forward - strafe + (turn+turn_correct)));
        back_right.setPower((forward + strafe - (turn+turn_correct)));
    }

    public void autoMove(double forward, double strafe, double turn) {
        if (!auto_moving) {
            target_forward += forward;
            target_strafe += strafe;
            target_turn += turn;
        }
        auto_moving = true;
    }

    public void autoSpeed(double speed, double speed_turn) {
        this.speed = speed;
        this.speed_turn = speed_turn;
    }

    public boolean ifReached() {
        if (reached) auto_moving = false;
        return reached;
    }

    public void update(Telemetry telemetry) {
        forward_error = target_forward - getForwardPosition();
        strafe_error = target_strafe - getStrafePosition();
        turn_error = target_turn - getHeading();

        if ((Math.abs(forward_error) < deadband) && (Math.abs(strafe_error) < deadband) && (Math.abs(turn_error) < deadband_turn)) {
            reached = true;
        } else {
            reached = false;
        }

        turn_integral_sum += turn_error * auto_loop_timer.seconds();

        if (Math.abs(turn_error) > 10) turn_integral_sum = 0;

        double forward_power = Range.clip(forward_error * FORWARD_KP, -speed, speed);
        double strafe_power = Range.clip(strafe_error * STRAFE_KP, -speed, speed);
        double turn_power = Range.clip((-turn_error * TURN_KP) + (-turn_integral_sum * TURN_KI), -speed_turn, speed_turn);

        move(forward_power, strafe_power, turn_power, 0);
        telemetry.addData("Turn Power: ", turn_power);
        telemetry.addData("Strafe Power: ", strafe_power);
        telemetry.addData("Forward Power: ", forward_power);
        telemetry.addData("Turn Error: ", turn_error);
        telemetry.addData("Strafe Error: ", strafe_error);
        telemetry.addData("Forward Error: ", forward_error);
        telemetry.addData("Turn Integral: ", turn_integral_sum);
        auto_loop_timer.reset();
    }

    public void stop() {
        front_left.setPower(0);
        front_right.setPower(0);
        back_left.setPower(0);
        back_right.setPower(0);
    }

    public void resetEncoders(){
        front_right.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        back_right.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        front_left.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        back_left.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        front_right.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        back_right.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        front_left.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        back_left.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public double getForwardPosition(){
        return (front_left.getCurrentPosition() + front_right.getCurrentPosition() + back_left.getCurrentPosition() + back_right.getCurrentPosition()) / 4.0;
    }

    public double getStrafePosition(){
        return (front_left.getCurrentPosition() - front_right.getCurrentPosition() - back_left.getCurrentPosition() + back_right.getCurrentPosition()) / 4.0;
    }

    public double getHeading(){
        return imu.getAngularOrientation().firstAngle;
    }

    public double getAngularVelocity(){
        return imu.getAngularVelocity().xRotationRate;
    }

    public void closeIMU(){
        imu.close();
    }
}