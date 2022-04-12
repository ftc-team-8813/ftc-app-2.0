package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.CRServoImplEx;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoImplEx;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.util.Scheduler;
import org.firstinspires.ftc.teamcode.util.event.EventBus;

import dalvik.system.DelegateLastClassLoader;

public class Robot
{
    // Hardware Vars
    public Drivetrain drivetrain;
    public IMU imu;
    public Lift lift;
    public Intake intake;
    public Duck duck;
    public Capper capper;
    public SensorCapstoneDetector capDetector;

    public int direction;

    public EventBus eventBus = new EventBus();
    public Scheduler scheduler = new Scheduler(eventBus);

    ///////////////////////////////
    // Singleton things          //
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
    //                           //
    ///////////////////////////////


    public Robot(HardwareMap hardwareMap)
    {
        // Motors
        DcMotorEx front_left = hardwareMap.get(DcMotorEx.class, "front left");
        DcMotorEx front_right = hardwareMap.get(DcMotorEx.class, "front right");
        DcMotorEx back_left = hardwareMap.get(DcMotorEx.class, "back left");
        DcMotorEx back_right = hardwareMap.get(DcMotorEx.class, "back right");
        DcMotor lift1 = hardwareMap.get(DcMotor.class, "lift1");
        DcMotor lift2 = hardwareMap.get(DcMotor.class, "lift2");
        DcMotor pivot = hardwareMap.get(DcMotor.class, "pivot");
        DcMotor intake = hardwareMap.get(DcMotor.class, "intake");

        // Servos
        Servo claw = hardwareMap.get(Servo.class, "claw");
        Servo kickstand = hardwareMap.get(Servo.class, "kickstand");
        CRServo left_intake = hardwareMap.get(CRServo.class, "intake left");
        CRServo right_intake = hardwareMap.get(CRServo.class, "intake right");
        CRServoImplEx duck_front = hardwareMap.get(CRServoImplEx.class, "duck front");
        CRServoImplEx duck_back = hardwareMap.get(CRServoImplEx.class, "duck back");
        CRServoImplEx tape = hardwareMap.get(CRServoImplEx.class, "tape");
        ServoImplEx tape_tilt = hardwareMap.get(ServoImplEx.class, "tape tilt");
        ServoImplEx tape_swivel = hardwareMap.get(ServoImplEx.class, "tape swivel");

        // Sensors
        BNO055IMU imu_sensor = hardwareMap.get(BNO055IMU.class, "imu");
        DigitalChannel lift_limit = hardwareMap.get(DigitalChannel.class, "lift limit");
        DistanceSensor freight_checker = hardwareMap.get(DistanceSensor.class, "freight checker");
        DistanceSensor cap_left = hardwareMap.get(DistanceSensor.class, "cap left");
        DistanceSensor cap_right = hardwareMap.get(DistanceSensor.class, "cap right");

        // Sub-Assemblies
        this.capDetector = new SensorCapstoneDetector(cap_left, cap_right);
        this.drivetrain = new Drivetrain(front_left, front_right, back_left, back_right, imu_sensor);
        this.lift = new Lift(lift1, lift2, pivot, lift_limit);
        this.intake = new Intake(intake, freight_checker, claw, left_intake, right_intake);
        this.duck = new Duck(duck_front, duck_back);
        this.capper = new Capper(tape, tape_tilt, tape_swivel);
    }
}
