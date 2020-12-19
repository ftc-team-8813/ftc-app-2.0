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

import static com.qualcomm.robotcore.hardware.DcMotorSimple.Direction.REVERSE;

public class Robot {
    public final Drivetrain drivetrain;
    public final Turret turret;
    public final ColorSensor ring_detector;
    public final Servo clawIn;
    public final Servo clawOut;
    public final Intake intake;
    public final Lift lift;

    public Robot(HardwareMap hardwareMap){
        //inleft inright outright outleft

        // Hardware Maps
        DcMotor top_left = hardwareMap.get(DcMotor.class, "out_left");
        DcMotor bottom_left = hardwareMap.get(DcMotor.class, "in_left");
        DcMotor top_right = hardwareMap.get(DcMotor.class, "out_right");
        DcMotor bottom_right = hardwareMap.get(DcMotor.class, "in_right");
        DcMotor shooter = hardwareMap.get(DcMotor.class, "shooter");
        DcMotor intaker = hardwareMap.get(DcMotor.class, "intaker");
        DcMotor rotator = hardwareMap.get(DcMotor.class, "rotator");
        // DcMotor forward_enc = hardwareMap.get(DcMotor.class, "forward_enc");
        // DcMotor side_enc = hardwareMap.get(DcMotor.class, "side_enc");

        AnalogInput left_potentiometer = hardwareMap.get(AnalogInput.class, "left_potentiometer");
        AnalogInput right_potentiometer = hardwareMap.get(AnalogInput.class, "right_potentiometer");
        AnalogInput rotate_potentiometer = hardwareMap.get(AnalogInput.class, "turret");
        DigitalChannel top_button = hardwareMap.get(DigitalChannel.class, "top switch");

        ring_detector = hardwareMap.get(ColorSensor.class, "light sensor");

        Servo finger = hardwareMap.get(Servo.class, "finger");
        Servo aim = hardwareMap.get(Servo.class, "aim");

        clawIn = hardwareMap.servo.get("wobble_in");
        clawOut = hardwareMap.servo.get("wobble_out");
        CRServo leftLift = hardwareMap.get(CRServo.class, "lift l");
        CRServo rightLift = hardwareMap.get(CRServo.class, "lift r");


        // Sub-Assemblies
        drivetrain = new Drivetrain(top_left, bottom_left, top_right, bottom_right);
        turret = new Turret(left_potentiometer, right_potentiometer, finger, aim, leftLift, rightLift, shooter, rotator, rotate_potentiometer);
        intake = new Intake(intaker);

        CalibratedAnalogInput lPot = new CalibratedAnalogInput(left_potentiometer, Storage.getFile("lift_calib_l.json"));
        CalibratedAnalogInput rPot = new CalibratedAnalogInput(right_potentiometer, Storage.getFile("lift_calib_r.json"));
        lift = new Lift(leftLift, rightLift, lPot, rPot, top_button);
    }
}
