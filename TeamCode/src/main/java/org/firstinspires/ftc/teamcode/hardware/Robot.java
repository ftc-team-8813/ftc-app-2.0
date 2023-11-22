package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.AndroidSerialNumberNotFoundException;

import org.firstinspires.ftc.teamcode.util.Scheduler;
import org.firstinspires.ftc.teamcode.util.event.EventBus;

public class Robot {
    public Drivetrain drivetrain;
    public DroneLauncher droneLauncher;
    public Sensor sensors;
    public Intake intake;
//    public Hoist hoist;
//    public Intake intake;
//    public Lift lift;
//    public Transfer transfer;
//    public Deposit deposit;
//    public DroneLauncher droneLauncher;

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
//        //Motors
        DcMotorEx front_left = hardwareMap.get(DcMotorEx.class, "front left");
        DcMotorEx front_right = hardwareMap.get(DcMotorEx.class, "front right");
        DcMotorEx back_left = hardwareMap.get(DcMotorEx.class, "back left");
        DcMotorEx back_right = hardwareMap.get(DcMotorEx.class, "back right");
        DcMotorEx intake = hardwareMap.get(DcMotorEx.class, "intake");
//        DcMotorEx lift1 = hardwareMap.get(DcMotorEx.class, "lift1");
//        DcMotorEx lift2 = hardwareMap.get(DcMotorEx.class, "lift2");
//        DcMotorEx Hoist = hardwareMap.get(DcMotorEx.class, "hoist");
//        DcMotorEx intake = hardwareMap.get(DcMotorEx.class, "intake");
//
//
//        //Servos
//        Servo horizServo = hardwareMap.get(Servo.class, "horiz");
//        Servo clawServo = hardwareMap.get(Servo.class, "claw");
//        Servo lockin1 = hardwareMap.get(Servo.class, "l1");
//        Servo lockin2 = hardwareMap.get(Servo.class, "l2");
//        Servo intake1 = hardwareMap.get(Servo.class, "i1");
//        Servo intake2 = hardwareMap.get(Servo.class, "i2");
//        Servo deposit1 = hardwareMap.get(Servo.class, "d1");
//        Servo deposit2 = hardwareMap.get(Servo.class, "d2");

        Servo droneLauncher = hardwareMap.get(Servo.class, "drone");

//        Rev2mDistanceSensor sensor1 = hardwareMap.get(Rev2mDistanceSensor.class, "sens1");
//        Rev2mDistanceSensor sensor2 = hardwareMap.get(Rev2mDistanceSensor.class, "sens2");
        ColorSensor sensor1 = hardwareMap.get(ColorSensor.class, "sens1");
//
//        //Distance Sensor
//        DistanceSensor claw_sensor = hardwareMap.get(DistanceSensor.class, "sensor");

        this.drivetrain = new Drivetrain(front_left, front_right, back_left, back_right);
        this.droneLauncher = new DroneLauncher(droneLauncher);
        this.sensors = new Sensor(sensor1);
        this.intake = new Intake(intake);
//        this.claw = new Claw(clawServo, claw_sensor);
//        this.lift = new Lift(lift1, lift2);
//        this.horiz = new Horizontal(horizServo);
    }


}
