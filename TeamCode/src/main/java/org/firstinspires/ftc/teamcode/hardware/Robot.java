package org.firstinspires.ftc.teamcode.hardware;

import com.arcrobotics.ftclib.hardware.motors.MotorEx;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.hardware.navigation.OdometryNav;
import org.firstinspires.ftc.teamcode.util.Scheduler;
import org.firstinspires.ftc.teamcode.util.event.EventBus;

public class Robot {

    public Drivetrain drivetrain;
    public Intake intake;
    public Lift lift;
    public OdometryNav odometryNav;
    public IMU imu;

    public EventBus eventBus = new EventBus();
    public Scheduler scheduler = new Scheduler(eventBus);

    private static Robot instance;
    public static Robot initialize(HardwareMap hardwareMap)
    {
        instance = new Robot(hardwareMap);
        return instance;
    }

    public static void close()
    {
        instance = null;
    }
    public static Robot instance()
    {
        return instance;
    }

    public Robot(HardwareMap hardwareMap)
    {
        // Motors
        MotorEx front_left = new MotorEx(hardwareMap, "front left");
        MotorEx front_right = new MotorEx(hardwareMap, "front right");
        MotorEx back_left = new MotorEx(hardwareMap, "back left");
        MotorEx back_right = new MotorEx(hardwareMap, "back right");

        DcMotor arm_lower = hardwareMap.get(DcMotor.class, "arm lower");
        DcMotor arm_upper = hardwareMap.get(DcMotor.class, "arm upper");
        DcMotor wrist = hardwareMap.get(DcMotor.class, "wrist");

        // Servos
        Servo claw = hardwareMap.get(Servo.class, "claw");

        // Sensors
        BNO055IMU imu_sensor = hardwareMap.get(BNO055IMU.class, "imu");
        DistanceSensor claw_sensor = hardwareMap.get(DistanceSensor.class, "claw sensor");

        // Sub-Assemblies
        this.drivetrain = new Drivetrain(front_left.motorEx, front_right.motorEx, back_left.motorEx, back_right.motorEx, imu_sensor);
        this.intake = new Intake(claw,claw_sensor);
        this.lift = new Lift(arm_lower, arm_upper, wrist);
        this.odometryNav = new OdometryNav(front_left, front_right, back_left, back_right);
    }
}
