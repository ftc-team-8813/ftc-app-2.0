package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.util.Status;

import static com.qualcomm.robotcore.hardware.DigitalChannel.Mode.INPUT;

public class FourBar {
    private final DcMotor arm;
    private final Servo dropper;
    private final DigitalChannel limit_checker;

    private double target_pos;
    private double integral;
    private double past_error;
    private double p_term;
    private double i_term;
    private double d_term;
    public boolean manual = false;


    public FourBar(DcMotor arm, Servo dropper, DigitalChannel limit_checker){
        this.arm = arm; // Encoder and motor on same port
        this.arm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        this.arm.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        this.dropper = dropper;
        this.limit_checker = limit_checker;
        this.limit_checker.setMode(INPUT);
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

    public void dropperExtendLeft(){
        this.dropper.setPosition(Status.DEPOSIT_EXTEND_LEFT);
    }

    public void dropperExtendRight(){
        this.dropper.setPosition(Status.DEPOSIT_EXTEND_RIGHT);
    }

    public void dropperReset(){
        this.dropper.setPosition(Status.DEPOSIT_RESET);
    }

    public double[] getPIDTerms(){
        return new double[]{p_term, i_term, d_term};
    }


    public void rotate(double target_ticks){
        if (-Status.UPPER_LIMIT < target_ticks && target_ticks < Status.UPPER_LIMIT){
            target_pos = target_ticks;
        }
    }


    public void update(){
        double curr_pos = arm.getCurrentPosition();
        double error = target_pos - curr_pos;

        p_term = error * Status.kP;

        integral += error;
        i_term = integral * Status.kI;

        double derivative = error - past_error;
        d_term = derivative * Status.kD;

        double power = p_term + i_term + d_term;
        arm.setPower(power * Status.SPEED_CAP);
        past_error = error;
    }
}
