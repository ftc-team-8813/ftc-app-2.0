package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.ColorRangeSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.util.Status;

import static com.qualcomm.robotcore.hardware.DigitalChannel.Mode.INPUT;

public class FourBar {
    private final DcMotor arm;
    private final Servo dropper;
    private final Servo dropper_gate;
    private final DigitalChannel limit_checker;
    private final ColorRangeSensor color_dist;

    private double target_pos;
    public boolean manual = false;


    public FourBar(DcMotor arm, Servo dropper, Servo dropper_gate, DigitalChannel limit_checker, ColorRangeSensor color_dist){
        this.arm = arm; // Encoder and motor on same port
        this.dropper_gate = dropper_gate;
        this.arm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        this.arm.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        this.dropper = dropper;
        this.limit_checker = limit_checker;
        this.limit_checker.setMode(INPUT);
        this.color_dist = color_dist;
    }


    public double getCurrentArmPos(){
        return arm.getCurrentPosition();
    }

    public double getTargetArmPos(){
        return target_pos;
    }

    public double getCurrentDropperPos(){
        return dropper.getPosition();
    }

    public double getFreightDistance() { return color_dist.getDistance(DistanceUnit.MM); }


    public void dropperExtendLeft(){
        this.dropper.setPosition(Status.DEPOSIT_EXTEND_LEFT);
    }

    public void dropperExtendRight(){
        this.dropper.setPosition(Status.DEPOSIT_EXTEND_RIGHT);
    }

    public void dropperReset(){
        this.dropper.setPosition(Status.DEPOSIT_RESET);
    }

    public void dropperClose() { this.dropper_gate.setPosition(Status.DEPOSIT_CLOSED); }

    public void dropperOpen() { this.dropper_gate.setPosition(Status.DEPOSIT_OPEN); }


    public void rotate(double target_ticks){
        this.dropperClose();
        if (-Status.UPPER_LIMIT < target_ticks && target_ticks < Status.UPPER_LIMIT){
            target_pos = target_ticks;
        }
    }


    public void update(){
        double curr_pos = arm.getCurrentPosition();
        double ratio = (target_pos - curr_pos) / Status.THRESHOLD;

        if (manual){
            arm.setPower(ratio * Status.MANUAL_SPEED);
        } else {
            arm.setPower(ratio * Status.SPEED);
        }
    }
}
