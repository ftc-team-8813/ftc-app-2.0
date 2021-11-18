package org.firstinspires.ftc.teamcode.hardware;

import android.text.method.Touch;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ColorRangeSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoImplEx;
import com.qualcomm.robotcore.hardware.TouchSensor;

import org.firstinspires.ftc.teamcode.hardware.navigation.Odometry;
import org.firstinspires.ftc.teamcode.util.Logger;
import org.firstinspires.ftc.teamcode.util.Scheduler;
import org.firstinspires.ftc.teamcode.util.event.EventBus;

public class Robot
{
    // Hardware Vars
    public Drivetrain drivetrain;
    public Odometry odometry;
    public Intake intake;
    public Lift lift;
    public Duck duck;

    public EventBus eventBus = new EventBus();
    public Scheduler scheduler = new Scheduler(eventBus);
    private final Logger log = new Logger("Robot");


    ///////////////////////////////
    // Singleton things          //
    private static Robot instance;
    public static Robot initialize(HardwareMap hardwareMap, String initMessage)
    {
        instance = new Robot(hardwareMap, initMessage);
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


    private Robot(HardwareMap hardwareMap, String initMessage)
    {
        // Hardware Maps
        // Motors
        DcMotor front_left = hardwareMap.get(DcMotor.class, "front left");
        DcMotor front_right = hardwareMap.get(DcMotor.class, "front right");
        DcMotor back_left = hardwareMap.get(DcMotor.class, "back left"); // Doubles as right odo pod
        DcMotor back_right = hardwareMap.get(DcMotor.class, "back right");
        DcMotor intake = hardwareMap.get(DcMotor.class, "intake");
        DcMotor lift = hardwareMap.get(DcMotor.class, "lift");

        // Odo Pods
        DcMotor left_odo_motor = hardwareMap.get(DcMotor.class, "left odo");
        DcMotor back_odo_motor = hardwareMap.get(DcMotor.class, "side odo");

        // Servos
        ServoImplEx left_odo_drop = hardwareMap.get(ServoImplEx.class, "left odo drop");
        ServoImplEx right_odo_drop = hardwareMap.get(ServoImplEx.class, "right odo drop");
        Servo arm = hardwareMap.get(Servo.class, "arm");
        Servo dropper = hardwareMap.get(Servo.class, "dropper");
        CRServo spinner = hardwareMap.get(CRServo.class, "spinner");

        // Sensors
        BNO055IMU imu = hardwareMap.get(BNO055IMU.class, "imu");
        TouchSensor lift_limit = hardwareMap.get(TouchSensor.class, "lift limit");
        ColorRangeSensor dist = hardwareMap.get(ColorRangeSensor.class, "freight sensor");

        // Sub-Assemblies
        this.odometry = new Odometry(left_odo_motor, back_left, back_odo_motor, left_odo_drop, right_odo_drop, imu);
        this.drivetrain = new Drivetrain(this.odometry, front_left, front_right, back_left, back_right);
        this.intake = new Intake(intake, dist);
        this.lift = new Lift(lift, arm, dropper, lift_limit);
        this.duck = new Duck(spinner);
    }
}
