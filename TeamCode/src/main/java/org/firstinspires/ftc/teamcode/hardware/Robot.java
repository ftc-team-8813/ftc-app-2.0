package org.firstinspires.ftc.teamcode.hardware;

import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.arcrobotics.ftclib.hardware.motors.MotorEx;
import com.arcrobotics.ftclib.kinematics.HolonomicOdometry;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.AndroidSerialNumberNotFoundException;

import org.firstinspires.ftc.teamcode.opmodes.teleop.HorizControl;
import org.firstinspires.ftc.teamcode.util.Scheduler;
import org.firstinspires.ftc.teamcode.util.event.EventBus;

public class Robot {
    public Drivetrain drivetrain;
    public DroneLauncher droneLauncher;
    public DistanceSensors sensors;
    public Intake intake;
    public Horizontal horiz;
    public Lift lift;
    public Deposit deposit;
    public boolean transferring;
    private Motor.Encoder left_odometer;
    private Motor.Encoder right_odometer;
    private Motor.Encoder center_odometer;
    private final double TRACKWIDTH = 9.167;
    private final double CENTER_WHEEL_OFFSET = -6.024; // Center wheel offset is the distance between the
    // center of rotation of the robot and the center odometer.
    // This is to correct for the error that might occur when turning.
    // A negative offset means the odometer is closer to the back,
    // while a positive offset means it is closer to the front.
    private final double WHEEL_DIAMETER = 1.37795;
    private final double TICKS_PER_REV = 8192;
    private final double DISTANCE_PER_PULSE = Math.PI * WHEEL_DIAMETER / TICKS_PER_REV;

    public HolonomicOdometry odo;
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

    public Robot(HardwareMap hardwareMap){

        //Motors
//        MotorEx front_left = new MotorEx(hardwareMap, "front left"); //done
//        MotorEx front_right = new MotorEx(hardwareMap, "front right"); //done
//        MotorEx back_left = new MotorEx(hardwareMap, "back left"); //done
//        MotorEx back_right = new MotorEx(hardwareMap, "back right"); //done
        DcMotorEx front_left = hardwareMap.get(DcMotorEx.class, "front left"); //done
        DcMotorEx front_right = hardwareMap.get(DcMotorEx.class, "front right"); //done
        DcMotorEx back_left = hardwareMap.get(DcMotorEx.class, "back left"); //done
        DcMotorEx back_right = hardwareMap.get(DcMotorEx.class, "back right"); //done

        DcMotorEx intake = hardwareMap.get(DcMotorEx.class, "intake");
        DcMotorEx horiz = hardwareMap.get(DcMotorEx.class, "horizontal");

        DcMotorEx lift1 = hardwareMap.get(DcMotorEx.class, "lift1"); //done
        DcMotorEx lift2 = hardwareMap.get(DcMotorEx.class, "lift2"); //done

//        Servo droneLauncher = hardwareMap.get(Servo.class, "drone");

//        Rev2mDistanceSensor l1 = hardwareMap.get(Rev2mDistanceSensor.class, "l1");
        Rev2mDistanceSensor r1 = hardwareMap.get(Rev2mDistanceSensor.class, "r1");

        Servo intakelock = hardwareMap.get(Servo.class, "intakeLock"); //done

        Servo depoPivot = hardwareMap.get(Servo.class, "depoPivot");
        Servo depoLock = hardwareMap.get(Servo.class, "depoLock");
        Servo leftLiftDepo = hardwareMap.get(Servo.class, "leftLiftDepo");
        Servo rightLiftDepo = hardwareMap.get(Servo.class, "rightLiftDepo");
//
        this.drivetrain = new Drivetrain(front_left, front_right, back_left, back_right);
//        this.droneLauncher = new DroneLauncher(droneLauncher);
        this.sensors = new DistanceSensors(r1);
        this.intake = new Intake(intake, intakelock);
        this.horiz = new Horizontal(horiz);
        this.lift = new Lift(lift1, lift2);
        this.deposit = new Deposit(leftLiftDepo, rightLiftDepo, depoPivot, depoLock);
////
//        left_odometer = back_left.encoder.setDistancePerPulse(DISTANCE_PER_PULSE);
//        right_odometer = front_left.encoder.setDistancePerPulse(DISTANCE_PER_PULSE);
//        center_odometer = back_right.encoder.setDistancePerPulse(DISTANCE_PER_PULSE);

//        right_odometer.setDirection(MotorEx.Direction.REVERSE);
//
//        odo = new HolonomicOdometry(
//            left_odometer::getDistance,
//            right_odometer::getDistance,
//            center_odometer::getDistance,
//            TRACKWIDTH, CENTER_WHEEL_OFFSET
//        );

    }


}
