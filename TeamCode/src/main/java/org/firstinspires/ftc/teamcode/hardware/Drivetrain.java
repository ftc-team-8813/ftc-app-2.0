package org.firstinspires.ftc.teamcode.hardware;

import com.arcrobotics.ftclib.command.SubsystemBase;

import com.arcrobotics.ftclib.drivebase.MecanumDrive;
import com.arcrobotics.ftclib.geometry.Pose2d;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.arcrobotics.ftclib.hardware.motors.MotorEx;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
public class Drivetrain{

//    private DcMotorEx front_left;
//    private DcMotorEx front_right;
//    private DcMotorEx back_left;
//    private DcMotorEx back_right;
//
//    public Drivetrain(DcMotorEx front_left, DcMotorEx front_right, DcMotorEx back_left, DcMotorEx back_right){
//        this.front_left = front_left;
//        this.front_right = front_right;
//        this.back_left = back_left;
//        this.back_right = back_right;
//
//        front_right.setDirection(DcMotorSimple.Direction.REVERSE);
//        back_right.setDirection(DcMotorSimple.Direction.REVERSE);
//
//        front_left.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        front_right.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        back_left.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        back_right.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//    }
//
//    public void resetEncoders() {
//        front_right.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        back_right.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        front_left.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        back_left.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//
//        front_right.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//        back_right.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//        front_left.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//        back_left.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//    }
//
//
//
//    public void move(double forward, double strafe, double turn, double turn_correct) {
//        front_left.setPower((forward + strafe + (turn + turn_correct)));
//        front_right.setPower((forward - strafe - (turn + turn_correct)));
//        back_left.setPower((forward - strafe + (turn + turn_correct)));
//        back_right.setPower((forward + strafe - (turn + turn_correct)));
//    }

    private MotorEx front_left;
    private MotorEx front_right;
    private MotorEx back_left;
    private MotorEx back_right;
    private MecanumDrive drive;
    private BNO055IMU imu_sensor;
    public Drivetrain(MotorEx front_left, MotorEx front_right, MotorEx back_left, MotorEx back_right, BNO055IMU imu_sensor){
        this.front_left = front_left;
        this.front_right = front_right;
        this.back_left = back_left;
        this.back_right = back_right;
        this.imu_sensor = imu_sensor;
        drive = new MecanumDrive(front_left, front_right, back_left, back_right);
    }

    public void moveRobotCentric(double strafeSpeed, double forwardSpeed, double turnSpeed){
        drive.driveRobotCentric(strafeSpeed, forwardSpeed, turnSpeed);
    }

    public void moveFieldCentric(double strafeSpeed, double forwardSpeed, double turnSpeed){
        drive.driveFieldCentric(strafeSpeed, forwardSpeed, turnSpeed, imu_sensor.getAngularOrientation().firstAngle); //might need to look at imu
    }

    public void autoMove(Pose2d targetPose, Pose2d currentPose, double strafeSpeed, double forwardSpeed, double turnSpeed, boolean redAuto){
        if(targetPose.getX() - currentPose.getX() < 10 && redAuto){
            strafeSpeed = -0.5; ///figure out the speeds
        }else if(!redAuto){
            strafeSpeed = 0.5;
        }
        if(targetPose.getY() - currentPose.getY() < 10){
            forwardSpeed = 0.5;
        }else if(!redAuto){
            forwardSpeed = -0.5;
        }
    }

}
