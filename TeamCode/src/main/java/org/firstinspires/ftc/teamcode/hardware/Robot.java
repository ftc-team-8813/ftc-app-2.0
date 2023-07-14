package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.util.Scheduler;
import org.firstinspires.ftc.teamcode.util.event.EventBus;

public class Robot {
    public Drivetrain drivetrain;
    public Lift lift;
    public Horizontal horiz;
    public Claw claw;

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
        DcMotorEx front_left = hardwareMap.get(DcMotorEx.class, "FL");
        DcMotorEx front_right = hardwareMap.get(DcMotorEx.class, "FR");
        DcMotorEx back_left = hardwareMap.get(DcMotorEx.class, "BL");
        DcMotorEx back_right = hardwareMap.get(DcMotorEx.class, "BR");
        DcMotorEx lift1 = hardwareMap.get(DcMotorEx.class, "lift1");
        DcMotorEx lift2 = hardwareMap.get(DcMotorEx.class, "lift2");

        //Servos
        Servo horizServo = hardwareMap.get(Servo.class, "horiz");
        Servo clawServo = hardwareMap.get(Servo.class, "claw");

        //Distance Sensor
        DistanceSensor claw_sensor = hardwareMap.get(DistanceSensor.class, "sensor");


        this.drivetrain = new Drivetrain(front_left, front_right, back_left, back_right);
        this.claw = new Claw(clawServo, claw_sensor);
        this.lift = new Lift(lift1, lift2);
        this.horiz = new Horizontal(horizServo);
    }

}
