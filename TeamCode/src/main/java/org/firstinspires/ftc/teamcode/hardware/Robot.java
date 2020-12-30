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
        // Hardware Maps
        DcMotor top_left = hardwareMap.get(DcMotor.class, "top left");
        DcMotor bottom_left = hardwareMap.get(DcMotor.class, "bottom left");
        DcMotor top_right = hardwareMap.get(DcMotor.class, "top right");
        DcMotor bottom_right = hardwareMap.get(DcMotor.class, "bottom right");
        DcMotor shooter = null; // hardwareMap.get(DcMotor.class, "shooter");
        DcMotor intaker = hardwareMap.get(DcMotor.class, "intake");
        DcMotor rotator = hardwareMap.get(DcMotor.class, "turret");
        DcMotor ramp = hardwareMap.get(DcMotor.class, "ramp");

        AnalogInput left_potentiometer = null; // hardwareMap.get(AnalogInput.class, "lift l");
        AnalogInput right_potentiometer = null; // hardwareMap.get(AnalogInput.class, "lift r");
        AnalogInput rotate_potentiometer = null; // hardwareMap.get(AnalogInput.class, "turret");
        DigitalChannel top_button = null; // hardwareMap.get(DigitalChannel.class, "top switch");

        ring_detector = null; // hardwareMap.get(ColorSensor.class, "light sensor");

        Servo finger = null; // hardwareMap.get(Servo.class, "finger");
        Servo aim = null; // hardwareMap.get(Servo.class, "aim");

        clawIn = null; // hardwareMap.servo.get("wobble_in");
        clawOut = null; // hardwareMap.servo.get("wobble_out");
        CRServo leftLift = null; // hardwareMap.get(CRServo.class, "lift l");
        CRServo rightLift = null; // hardwareMap.get(CRServo.class, "lift r");


        // Sub-Assemblies
        drivetrain = new Drivetrain(top_left, bottom_left, top_right, bottom_right);
        turret = null; // new Turret(left_potentiometer, right_potentiometer, finger, aim, leftLift, rightLift, shooter, rotator, rotate_potentiometer);
        intake = new Intake(intaker, ramp);

        CalibratedAnalogInput lPot = new CalibratedAnalogInput(left_potentiometer, Storage.getFile("lift_calib_l.json"));
        CalibratedAnalogInput rPot = new CalibratedAnalogInput(right_potentiometer, Storage.getFile("lift_calib_r.json"));
        lift = new Lift(leftLift, rightLift, lPot, rPot, top_button);
    }
}
