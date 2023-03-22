package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.Servo;

public class Arm {

    private final Servo arm_left;
    private final Servo arm_right;
    private final DcMotorEx arm_encoder;
    private double arm_servo_position;
    private double arm_encoder_position;
    private double arm_position;
    private double arm_target;

    public Arm (Servo arm_left, Servo arm_right, DcMotorEx arm_encoder) {
        this.arm_left = arm_left;
        this.arm_right = arm_right;
        this.arm_encoder = arm_encoder;
    }

    public void update() {
        arm_servo_position = arm_left.getPosition();
        arm_encoder_position = arm_encoder.getCurrentPosition() * 288.0 / 8192.0;
        if(arm_servo_position != arm_target) {
            arm_left.setPosition((1 - (arm_target/-130) - 0.05));
            arm_right.setPosition((1 - (arm_target/-130) + 0.05));
        }
    }

    public double getCurrentServoPosition() {
        return arm_servo_position;
    }

    public double getCurrentEncoderPosition() {
        return arm_encoder_position;
    }

    public void setPosition(double pos) {
        arm_target = pos;
    }

    public double getTargetPosition() {
        return arm_target;
    }

    public void resetEncoders() {
        arm_encoder.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        arm_encoder.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

}
