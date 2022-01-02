package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.ColorRangeSensor;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PwmControl;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoImplEx;

import org.firstinspires.ftc.teamcode.util.Logger;
import org.firstinspires.ftc.teamcode.util.Scheduler;
import org.firstinspires.ftc.teamcode.util.event.EventBus;
import org.firstinspires.ftc.teamcode.vision.ImageDraw;

public class Robot
{
    // Hardware Vars
    public Drivetrain drivetrain;
    public Intake intake;
    public Lift lift;
    public Duck duck;
    public IMU imu;

    public EventBus eventBus = new EventBus();
    public Scheduler scheduler = new Scheduler(eventBus);

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
        DcMotor back_left = hardwareMap.get(DcMotor.class, "back left");
        DcMotor back_right = hardwareMap.get(DcMotor.class, "back right");
        DcMotor lift = hardwareMap.get(DcMotor.class, "lift");
        DcMotor intake_front = hardwareMap.get(DcMotor.class, "intake front");
        DcMotor intake_back = hardwareMap.get(DcMotor.class, "intake back");
        DcMotor duck = hardwareMap.get(DcMotor.class, "duck");

        // Servos
        ServoImplEx bucket = hardwareMap.get(ServoImplEx.class, "bucket");
        bucket.setPwmRange(new PwmControl.PwmRange(500,2500));
        Servo arm = hardwareMap.get(Servo.class, "arm");
        ServoImplEx outrigger = hardwareMap.get(ServoImplEx.class, "outrigger");
        outrigger.setPwmRange(new PwmControl.PwmRange(500,2500));

        // Sensors
        BNO055IMU imu = hardwareMap.get(BNO055IMU.class, "imu2");
        DistanceSensor freight_checker = hardwareMap.get(DistanceSensor.class, "freight checker");
        DigitalChannel limit_switch = hardwareMap.get(DigitalChannel.class, "lift limit");

        // Sub-Assemblies
        this.drivetrain = new Drivetrain(front_left, front_right, back_left, back_right);
        this.intake = new Intake(intake_front, intake_back, freight_checker, bucket);
        this.lift = new Lift(lift, arm, limit_switch, outrigger);
        this.duck = new Duck(duck);
        this.imu = new IMU(imu);
        this.imu.initialize(eventBus, scheduler);
    }
}
