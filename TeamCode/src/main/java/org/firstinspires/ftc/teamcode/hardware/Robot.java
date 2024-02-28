package org.firstinspires.ftc.teamcode.hardware;

import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.arcrobotics.ftclib.hardware.motors.MotorEx;
import com.arcrobotics.ftclib.kinematics.HolonomicOdometry;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.AndroidSerialNumberNotFoundException;

//import org.firstinspires.ftc.teamcode.hardware.navigation.Odometry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.util.Scheduler;
import org.firstinspires.ftc.teamcode.util.event.EventBus;

public class Robot {

    public Drivetrain drivetrain;
//    public Camera camera;
    public DistanceSensors sensors;
    public HolonomicOdometry odometry;
    public Intake intake;
    public Lift lift;
    public Deposit deposit;
    public EventBus eventBus = new EventBus();
    public Scheduler scheduler = new Scheduler(eventBus);
    private static Robot instance;
    private final double TRACKWIDTH = 10.89;
    private final double CENTER_WHEEL_OFFSET = -5.805;
    private final double WHEEL_DIAMETER = 1.37795;
    private final double TICKS_PER_REV = 8192;
    private final double DISTANCE_PER_PULSE = Math.PI * WHEEL_DIAMETER / TICKS_PER_REV;
    public Motor.Encoder left_odometer;
    public Motor.Encoder right_odometer;
    public Motor.Encoder center_odometer;
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

    public Robot(HardwareMap hardwareMap) {
//
        //Motors

        DcMotorEx front_left = hardwareMap.get(DcMotorEx.class, "fl");
        DcMotorEx front_right = hardwareMap.get(DcMotorEx.class, "fr");
        DcMotorEx back_left = hardwareMap.get(DcMotorEx.class, "bl");
        DcMotorEx back_right = hardwareMap.get(DcMotorEx.class, "br");

        DcMotorEx l1 = hardwareMap.get(DcMotorEx.class, "L1");
        DcMotorEx l2 = hardwareMap.get(DcMotorEx.class, "L2");

        DcMotorEx intake = hardwareMap.get(DcMotorEx.class, "intake");

        Servo roller = hardwareMap.get(Servo.class, "rol");
        Servo cage = hardwareMap.get(Servo.class, "cage");

        Servo swivel = hardwareMap.get(Servo.class, "swivel");
        Servo c1 = hardwareMap.get(Servo.class, "c1");
        Servo c2 = hardwareMap.get(Servo.class, "c2");
        Servo pivot = hardwareMap.get(Servo.class, "pivot");

        AnalogInput rightC = hardwareMap.get(AnalogInput.class, "rightC");
        AnalogInput leftC = hardwareMap.get(AnalogInput.class, "leftC");
        AnalogInput pivotI = hardwareMap.get(AnalogInput.class, "swiv");

        DigitalChannel rightP = hardwareMap.get(DigitalChannel.class, "rightP");
        DigitalChannel leftP = hardwareMap.get(DigitalChannel.class, "leftP");


        this.drivetrain = new Drivetrain(front_left, front_right, back_left, back_right);
        this.intake = new Intake(intake, roller, cage);
        this.lift = new Lift(l1, l2);
        this.deposit = new Deposit(swivel, c1, c2, pivot, rightC, leftC, pivotI);
        this.sensors = new DistanceSensors(rightP, leftP);
        //Add hang and drone

//
//        right_odometer.setDirection(MotorEx.Direction.REVERSE);
//
//        this.odometry = new HolonomicOdometry(
//            left_odometer::getDistance,
//            right_odometer::getDistance,
//            center_odometer::getDistance,
//            TRACKWIDTH, CENTER_WHEEL_OFFSET
//        );

    }

    }
