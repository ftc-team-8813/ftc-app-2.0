package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.hardware.navigation.Odometry;
import org.firstinspires.ftc.teamcode.util.Logger;
import org.firstinspires.ftc.teamcode.util.Scheduler;
import org.firstinspires.ftc.teamcode.util.event.EventBus;

import java.security.DigestInputStream;

public class Robot
{
    // Hardware Vars
    public Drivetrain drivetrain;
    public Odometry odometry;
    public Intake intake;
    public FourBar fourbar;

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
        DcMotor fourbar = hardwareMap.get(DcMotor.class, "fourbar");

        // Servos
        Servo left_odo_drop = hardwareMap.get(Servo.class, "left odo drop");
        Servo right_odo_drop = hardwareMap.get(Servo.class, "right odo drop");

        // Sensors
        DigitalChannel arm_lower_limit = hardwareMap.get(DigitalChannel.class, "arm lower limit");


        // Sub-Assemblies
        this.drivetrain = new Drivetrain(front_left, front_right, back_left, back_right);
        this.odometry = new Odometry(back_right, front_right, back_left, left_odo_drop, right_odo_drop);
        this.intake = new Intake(intake);
        this.fourbar = new FourBar(fourbar, arm_lower_limit);
    }
}
