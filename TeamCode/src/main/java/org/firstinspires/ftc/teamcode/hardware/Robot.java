package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.CRServoImplEx;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PwmControl;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoImplEx;

import org.firstinspires.ftc.teamcode.util.Scheduler;
import org.firstinspires.ftc.teamcode.util.event.EventBus;

public class Robot
{
    // Hardware Vars
    public Drivetrain drivetrain;
    public AutoDrive navigation;
    public Intake intake;
    public Lift lift;
    public Duck duck;
    public LineFinder lineFinder;
    public CapstoneDetector detector;

    public int direction;

    public EventBus eventBus = new EventBus();
    public Scheduler scheduler = new Scheduler(eventBus);

    ///////////////////////////////
    // Singleton things          //
    private static Robot instance;
    public static Robot initialize(HardwareMap hardwareMap, String initMessage, int direction)
    {
        instance = new Robot(hardwareMap, initMessage, direction);
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


    public Robot(HardwareMap hardwareMap, String initMessage, int direction)
    {
        // Motors
        DcMotorEx front_left = hardwareMap.get(DcMotorEx.class, "front left");
        DcMotorEx front_right = hardwareMap.get(DcMotorEx.class, "front right");
        DcMotorEx back_left = hardwareMap.get(DcMotorEx.class, "back left");
        DcMotorEx back_right = hardwareMap.get(DcMotorEx.class, "back right");
        DcMotor lift = hardwareMap.get(DcMotor.class, "lift");
        DcMotor lift2 = hardwareMap.get(DcMotor.class, "lift2");
        DcMotor intake_front = hardwareMap.get(DcMotor.class, "intake front");
        DcMotor intake_back = hardwareMap.get(DcMotor.class, "intake back");

        // Servos
        ServoImplEx bucket = hardwareMap.get(ServoImplEx.class, "bucket");
        bucket.setPwmRange(new PwmControl.PwmRange(500,2500));
        Servo arm = hardwareMap.get(Servo.class, "arm");
        ServoImplEx outrigger = hardwareMap.get(ServoImplEx.class, "outrigger");
        outrigger.setPwmRange(new PwmControl.PwmRange(500,2500));
        CRServoImplEx duckFront = hardwareMap.get(CRServoImplEx.class, "duck front");
        CRServoImplEx duckback = hardwareMap.get(CRServoImplEx.class, "duck back");

        // Sensors
        BNO055IMU imu_sensor = hardwareMap.get(BNO055IMU.class, "imu2");
        DistanceSensor freight_checker = hardwareMap.get(DistanceSensor.class, "freight checker");
        DigitalChannel limit_switch = hardwareMap.get(DigitalChannel.class, "lift limit");
        ColorSensor line_finder = hardwareMap.get(ColorSensor.class, "line finder");
        DistanceSensor left_cap = hardwareMap.get(DistanceSensor.class, "leftDS");
        DistanceSensor right_cap = hardwareMap.get(DistanceSensor.class, "rightDS");
        DistanceSensor dist_y = hardwareMap.get(DistanceSensor.class, "dist_y");

        // Sub-Assemblies
        this.lineFinder = new LineFinder(line_finder);
        this.drivetrain = new Drivetrain(front_left, front_right, back_left, back_right, dist_y);
        this.navigation = new AutoDrive(drivetrain, lineFinder);
        this.intake = new Intake(intake_front, intake_back, freight_checker, bucket);
        this.lift = new Lift(lift, lift2, arm, limit_switch, outrigger);
        this.duck = new Duck(duckFront, duckback);
        this.detector = new CapstoneDetector(left_cap, right_cap);
        this.direction = direction;
    }
}
