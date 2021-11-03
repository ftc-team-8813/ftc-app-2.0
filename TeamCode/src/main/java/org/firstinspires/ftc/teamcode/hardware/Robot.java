package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ColorRangeSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoImplEx;

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
        DcMotor back_left = hardwareMap.get(DcMotor.class, "back left");
        DcMotor back_right = hardwareMap.get(DcMotor.class, "back right");
        DcMotor intake = hardwareMap.get(DcMotor.class, "intake");
        DcMotor lift = hardwareMap.get(DcMotor.class, "lift");

        // Servos
        ServoImplEx left_odo_drop = hardwareMap.get(ServoImplEx.class, "left odo drop");
        ServoImplEx right_odo_drop = hardwareMap.get(ServoImplEx.class, "right odo drop");
        Servo arm = hardwareMap.get(Servo.class, "arm");
        Servo dropper = hardwareMap.get(Servo.class, "dropper");
        CRServo spinner = hardwareMap.get(CRServo.class, "spinner");

        // Sensors
        ColorRangeSensor dist = hardwareMap.get(ColorRangeSensor.class, "freight sensor");

        // Sub-Assemblies
        this.drivetrain = new Drivetrain(front_left, front_right, back_left, back_right);
        this.odometry = new Odometry(back_right, back_left, front_right, left_odo_drop, right_odo_drop);
        this.intake = new Intake(intake, dist);
        this.lift = new Lift(lift, arm, dropper);
        this.duck = new Duck(spinner);
    }
}
