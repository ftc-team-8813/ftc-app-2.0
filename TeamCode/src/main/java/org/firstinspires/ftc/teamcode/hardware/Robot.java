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
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.AndroidSerialNumberNotFoundException;

//import org.firstinspires.ftc.teamcode.hardware.navigation.Odometry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.opmodes.teleop.HorizControl;
import org.firstinspires.ftc.teamcode.util.Scheduler;
import org.firstinspires.ftc.teamcode.util.event.EventBus;

public class Robot {

    public Drivetrain drivetrain;
//    public Camera camera;
    public WebcamName camera1;
    public DroneLauncher droneLauncher;
    public DistanceSensors sensors;
    public HolonomicOdometry odometry;
    public Intake intake;
    public Horizontal horiz;
    public Hoist hoist;
    public Lift lift;
    public Deposit deposit;
    public EventBus eventBus = new EventBus();
    public Scheduler scheduler = new Scheduler(eventBus);

    private static Robot instance;
    private final double TRACKWIDTH = 9.167;
    private final double CENTER_WHEEL_OFFSET = -6.024;
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
        MotorEx front_left_a = new MotorEx(hardwareMap, "front left");
        MotorEx front_right_a = new MotorEx(hardwareMap, "front right");
        MotorEx back_left_a = new MotorEx(hardwareMap, "back left");
        MotorEx back_right_a = new MotorEx(hardwareMap, "back right");
        AnalogInput analogInput = hardwareMap.get(AnalogInput.class, "pivInput");

        DcMotorEx front_left = hardwareMap.get(DcMotorEx.class, "front left");
        DcMotorEx front_right = hardwareMap.get(DcMotorEx.class, "front right");
        DcMotorEx back_left = hardwareMap.get(DcMotorEx.class, "back left");
        DcMotorEx back_right = hardwareMap.get(DcMotorEx.class, "back right");

        DcMotorEx intake = hardwareMap.get(DcMotorEx.class, "intake");
        DcMotorEx horiz = hardwareMap.get(DcMotorEx.class, "horiz");

        DcMotorEx lift1 = hardwareMap.get(DcMotorEx.class, "lift1");
        DcMotorEx lift2 = hardwareMap.get(DcMotorEx.class, "lift2");

//        WebcamName camera = hardwareMap.get(WebcamName.class, "Webcam 1");

        Servo hoist1 = hardwareMap.get(Servo.class, "leftHoist");
        Servo hoist2 = hardwareMap.get(Servo.class, "rightHoist");

        Servo droneServo = hardwareMap.get(Servo.class, "droneLauncher");
        Servo droneHeight = hardwareMap.get(Servo.class, "droneAngle");

        Rev2mDistanceSensor left = hardwareMap.get(Rev2mDistanceSensor.class, "left");
        Rev2mDistanceSensor right = hardwareMap.get(Rev2mDistanceSensor.class, "right");
        RevColorSensorV3 tape = hardwareMap.get(RevColorSensorV3.class, "tape");

        Servo intakelock = hardwareMap.get(Servo.class, "intakeLock");

        Servo depoPivot = hardwareMap.get(Servo.class, "depoPivot");
        Servo depoLock = hardwareMap.get(Servo.class, "depoLock");
        Servo leftLiftDepo = hardwareMap.get(Servo.class, "leftLiftDepo");
        Servo rightLiftDepo = hardwareMap.get(Servo.class, "rightLiftDepo");
//
        this.drivetrain = new Drivetrain(front_left, front_right, back_left, back_right);
        this.sensors = new DistanceSensors(left, right, tape);
        this.intake = new Intake(intake, intakelock);
        this.horiz = new Horizontal(horiz);
        this.lift = new Lift(lift1, lift2);
        this.deposit = new Deposit(leftLiftDepo, rightLiftDepo, depoPivot, depoLock, analogInput);
//        this.camera = new Camera(camera);
        this.hoist = new Hoist(hoist1, hoist2);
//        this.camera1 = camera;
        this.droneLauncher = new DroneLauncher(droneServo, droneHeight);

        left_odometer = back_left_a.encoder.setDistancePerPulse(DISTANCE_PER_PULSE);
        right_odometer = front_left_a.encoder.setDistancePerPulse(DISTANCE_PER_PULSE);
        center_odometer = back_right_a.encoder.setDistancePerPulse(DISTANCE_PER_PULSE);

        right_odometer.setDirection(MotorEx.Direction.REVERSE);

        this.odometry = new HolonomicOdometry(
            left_odometer::getDistance,
            right_odometer::getDistance,
            center_odometer::getDistance,
            TRACKWIDTH, CENTER_WHEEL_OFFSET
        );

    }

    }
