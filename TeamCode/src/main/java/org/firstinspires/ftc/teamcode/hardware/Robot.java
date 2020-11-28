package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import static com.qualcomm.robotcore.hardware.DcMotorSimple.Direction.REVERSE;

public class Robot {
    public final Drivetrain drivetrain;
    public final Turret turret;

    public Robot(HardwareMap hardwareMap){
        // Hardware Maps
        DcMotor top_left = hardwareMap.get(DcMotor.class, "top_left");
        DcMotor bottom_left = hardwareMap.get(DcMotor.class, "bottom_left");
        DcMotor top_right = hardwareMap.get(DcMotor.class, "top_right");
        DcMotor bottom_right = hardwareMap.get(DcMotor.class, "bottom_right");
        DcMotor shooter = hardwareMap.get(DcMotor.class, "shooter");
        DcMotor rotator = hardwareMap.get(DcMotor.class, "rotator");
        DcMotor forward_enc = hardwareMap.get(DcMotor.class, "forward_enc");
        DcMotor side_enc = hardwareMap.get(DcMotor.class, "side_enc");

        AnalogInput left_potentiometer = hardwareMap.get(AnalogInput.class, "left_potentiometer");
        AnalogInput right_potentiometer = hardwareMap.get(AnalogInput.class, "right_potentiometer");

        Servo finger = hardwareMap.get(Servo.class, "finger");
        CRServo leftLift = hardwareMap.get(CRServo.class, "left_lift");
        CRServo rightLift = hardwareMap.get(CRServo.class, "right_lift");


        //Reverses left side to match right side rotation in Drivetrain
        top_right.setDirection(REVERSE);
        bottom_right.setDirection(REVERSE);

        // Sub-Assemblies
        drivetrain = new Drivetrain(top_left, bottom_left, top_right, bottom_right, forward_enc, side_enc);
        turret = new Turret(left_potentiometer, right_potentiometer, finger, leftLift, rightLift, shooter);
    }


}
