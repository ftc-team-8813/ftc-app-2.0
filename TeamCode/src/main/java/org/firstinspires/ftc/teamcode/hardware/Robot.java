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
    public final Lift_Grabber lift_grabber;

    public Robot(HardwareMap hardwareMap){
        // Hardware Maps
        DcMotor topLeft = hardwareMap.get(DcMotor.class, "lf");
        DcMotor bottomLeft = hardwareMap.get(DcMotor.class, "lb");
        DcMotor topRight = hardwareMap.get(DcMotor.class, "rf");
        DcMotor bottomRight = hardwareMap.get(DcMotor.class, "rb");
        DcMotor rotator = null; // hardwareMap.get(DcMotor.class, "rotator");
        AnalogInput left_potentiometer = hardwareMap.get(AnalogInput.class, "a0");
        AnalogInput right_potentiometer = hardwareMap.get(AnalogInput.class, "a1");
        CRServo left_lift = hardwareMap.get(CRServo.class, "lift l");
        CRServo right_lift = hardwareMap.get(CRServo.class, "lift r");

        //Reverses left side to match right side rotation in Drivetrain
        topRight.setDirection(REVERSE);
        bottomRight.setDirection(REVERSE);

        // Sub-Assemblies
        drivetrain = new Drivetrain(topLeft, bottomLeft, topRight, bottomRight);
        turret = new Turret(rotator);
        lift_grabber = new Lift_Grabber(left_potentiometer, right_potentiometer, left_lift, right_lift);
    }


}
