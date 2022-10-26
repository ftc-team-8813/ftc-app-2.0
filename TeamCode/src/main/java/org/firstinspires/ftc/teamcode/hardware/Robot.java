package org.firstinspires.ftc.teamcode.hardware;

import com.arcrobotics.ftclib.hardware.motors.MotorEx;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.util.Encoder;
import org.firstinspires.ftc.teamcode.util.Scheduler;
import org.firstinspires.ftc.teamcode.util.event.EventBus;
import org.firstinspires.ftc.teamcode.vision.webcam.Webcam;

public class Robot {

    public Drivetrain drivetrain;
    public Lift lift;
    public IMU imu;
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

    public Robot(HardwareMap hardwareMap)
    {
        // Motors
//        DcMotorEx front_left_dc = hardwareMap.get(DcMotorEx.class, "front left");
//        DcMotorEx front_right_dc = hardwareMap.get(DcMotorEx.class, "front right");
//        DcMotorEx back_left_dc = hardwareMap.get(DcMotorEx.class, "back left");
//        DcMotorEx back_right_dc = hardwareMap.get(DcMotorEx.class, "back right");

        DcMotorEx arm_lower = hardwareMap.get(DcMotorEx.class, "arm lower");
        DcMotorEx arm_upper = hardwareMap.get(DcMotorEx.class, "arm upper");
        DcMotorEx wrist = hardwareMap.get(DcMotorEx.class, "wrist");

        MotorEx front_left = new MotorEx(hardwareMap, "front left");
        MotorEx front_right = new MotorEx(hardwareMap, "front right");
        MotorEx back_left = new MotorEx(hardwareMap, "back left");
        MotorEx back_right = new MotorEx(hardwareMap, "back right");

        //Servos
        Servo claw = hardwareMap.get(Servo.class, "claw");

        // Sensors
        BNO055IMU imu_sensor = hardwareMap.get(BNO055IMU.class, "imu");

        // Sub-Assemblies
//        this.drivetrain = new Drivetrain(front_left, front_right, back_left, back_right);
        this.drivetrain = new Drivetrain(front_left, front_right, back_left, back_right);
        this.claw = new Claw(claw);
    }
}
