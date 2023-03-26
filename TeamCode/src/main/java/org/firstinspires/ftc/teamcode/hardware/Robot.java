package org.firstinspires.ftc.teamcode.hardware;

import com.arcrobotics.ftclib.hardware.motors.MotorEx;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoImplEx;

import org.firstinspires.ftc.teamcode.hardware.navigation.Odometry;
import org.firstinspires.ftc.teamcode.util.Scheduler;
import org.firstinspires.ftc.teamcode.util.event.EventBus;

public class Robot {

    public Drivetrain drivetrain;
    public Intake intake;
    public Arm arm;
    public Horizontal horizontal;
    public Lift lift;
    public Odometry odometry;
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
        DcMotorEx lift_left = hardwareMap.get(DcMotorEx.class, "lift1");  //TODO RENAME lift left
        MotorEx lift_right = new MotorEx(hardwareMap, "lift2"); //TODO RENAME lift right
        DcMotorEx arm_encoder = hardwareMap.get(DcMotorEx.class, "arm encoder");

        // Servos
        Servo center_odometry = hardwareMap.get(Servo.class, "back odo lift"); //TODO RENAME ALL back odometry
        Servo left_odometry = hardwareMap.get(Servo.class, "left odo lift"); //
        Servo right_odometry = hardwareMap.get(Servo.class, "right odo lift"); //
        Servo claw = hardwareMap.get(Servo.class, "claw");
        Servo wrist = hardwareMap.get(Servo.class, "wrist");
        Servo holder = hardwareMap.get(Servo.class, "deposit"); // holder
        ServoImplEx latch = hardwareMap.get(ServoImplEx.class, "latch");
        Servo arm_left = hardwareMap.get(Servo.class, "arm1");
        Servo arm_right = hardwareMap.get(Servo.class, "arm2");

        // Sensors
        BNO055IMU imu_sensor = hardwareMap.get(BNO055IMU.class, "imu");
        DistanceSensor claw_sensor = hardwareMap.get(DistanceSensor.class, "claw sensor");
        DigitalChannel lift_limit = hardwareMap.get(DigitalChannel.class, "lift limit");
        DigitalChannel horizontal_limit = hardwareMap.get(DigitalChannel.class, "horizontal limit");

        // Sub-Assemblies
        this.drivetrain = new Drivetrain(front_left.motorEx, front_right.motorEx, back_left.motorEx, back_right.motorEx, imu_sensor);
        this.intake = new Intake(claw_sensor,claw,wrist);
        this.arm = new Arm(arm_left,arm_right, arm_encoder);
        this.horizontal = new Horizontal(horizontal,horizontal_limit);
        this.lift = new Lift(lift_left, lift_right.motorEx, lift_limit, holder, latch);
        this.odometry = new Odometry(front_left, front_right, back_left, back_right, center_odometry, left_odometry, right_odometry);
    }
}
