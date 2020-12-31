package org.firstinspires.ftc.teamcode.hardware;

import android.graphics.Color;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoController;

import org.firstinspires.ftc.teamcode.util.Storage;

import java.io.File;

import static com.qualcomm.robotcore.hardware.DcMotorSimple.Direction.REVERSE;

public class Robot {
    public final Drivetrain drivetrain;
    public final Intake intake;
    public final Turret turret;
    
    
    public Robot(HardwareMap hardwareMap){
        // Hardware Maps
        DcMotor top_left = hardwareMap.get(DcMotor.class, "top left");
        DcMotor bottom_left = hardwareMap.get(DcMotor.class, "bottom left");
        DcMotor top_right = hardwareMap.get(DcMotor.class, "top right");
        DcMotor bottom_right = hardwareMap.get(DcMotor.class, "bottom right");
        DcMotor shooter = hardwareMap.get(DcMotor.class, "shooter");
        DcMotor intake = hardwareMap.get(DcMotor.class, "intake");
        DcMotor turret = hardwareMap.get(DcMotor.class, "turret");
        DcMotor ramp = hardwareMap.get(DcMotor.class, "ramp");

        Servo pusher = hardwareMap.get(Servo.class, "pusher");
        Servo aim = null; // hardwareMap.get(Servo.class, "aim");

        // Sub-Assemblies
        drivetrain = new Drivetrain(top_left, bottom_left, top_right, bottom_right);
        
        AnalogInput turretFeedback = null;
        File turretConfig = Storage.getFile("turret_calib.json");
        File shooterConfig = Storage.getFile("shooter.json");
        this.turret = new Turret(turret, shooter, pusher, aim, turretFeedback, shooterConfig, turretConfig);
        this.intake = new Intake(intake, ramp);
    }
}
