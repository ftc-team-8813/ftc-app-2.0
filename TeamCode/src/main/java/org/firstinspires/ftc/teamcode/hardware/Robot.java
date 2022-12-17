package org.firstinspires.ftc.teamcode.hardware;

import com.arcrobotics.ftclib.hardware.motors.MotorEx;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.hardware.navigation.OdometryNav;
import org.firstinspires.ftc.teamcode.util.Scheduler;
import org.firstinspires.ftc.teamcode.util.event.EventBus;

public class Robot {

    public Drivetrain drivetrain;
    public Intake intake;
    public Lift lift;
    public OdometryNav odometryNav;
    public IMU imu;

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
        MotorEx front_left = new MotorEx(hardwareMap, "front left");
        MotorEx front_right = new MotorEx(hardwareMap, "front right");
        MotorEx back_left = new MotorEx(hardwareMap, "back left");
        MotorEx back_right = new MotorEx(hardwareMap, "back right");

        DcMotorEx horizontal = hardwareMap.get(DcMotorEx.class, "horizontal");
        DcMotorEx lift_1 = hardwareMap.get(DcMotorEx.class, "lift1");  //lift1 and lift2 have to be inversed
        MotorEx lift_2 = new MotorEx(hardwareMap, "lift2");
        DcMotorEx arm = hardwareMap.get(DcMotorEx.class, "arm");

        // Servos
        Servo center_odo = hardwareMap.get(Servo.class, "back odo lift");
        Servo left_odo = hardwareMap.get(Servo.class, "left odo lift");
        Servo right_odo = hardwareMap.get(Servo.class, "right odo lift");
        Servo claw = hardwareMap.get(Servo.class, "claw");
        Servo dumper = hardwareMap.get(Servo.class, "wrist");

        //Sensors
        DigitalChannel lift_limit = hardwareMap.get(DigitalChannel.class, "lift limit");
        DigitalChannel arm_limit = hardwareMap.get(DigitalChannel.class, "arm limit");
        DigitalChannel horizontal_limit = hardwareMap.get(DigitalChannel.class, "horizontal limit");

        // Sensors
        BNO055IMU imu_sensor = hardwareMap.get(BNO055IMU.class, "imu");
        DistanceSensor claw_sensor = hardwareMap.get(DistanceSensor.class, "claw sensor");

        // Sub-Assemblies
        this.drivetrain = new Drivetrain(front_left.motorEx, front_right.motorEx, back_left.motorEx, back_right.motorEx, imu_sensor, center_odo, left_odo, right_odo);
        this.lift = new Lift(lift_limit, lift_1, lift_2.motorEx, dumper);
        this.intake = new Intake(horizontal,arm,horizontal_limit,arm_limit,claw_sensor,claw,dumper);
        this.odometryNav = new OdometryNav(front_left, front_right, back_left, back_right,lift_2, center_odo, left_odo, right_odo);
    }
}
