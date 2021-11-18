package org.firstinspires.ftc.teamcode.hardware;

import android.text.method.Touch;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;

import org.firstinspires.ftc.teamcode.util.Status;

public class Lift {
    private final DcMotor lift;
    private final Servo dropper;
    private final Servo arm;
    private final TouchSensor limit;

    private int bottom = 0;
    private int height_preset = 0;
    private int extension = 0;

    private double target_pos;
    private double integral;
    private double past_error;
    private double p_term;
    private double i_term;
    private double d_term;


    public Lift(DcMotor lift, Servo arm, Servo dropper, TouchSensor limit){
        this.lift = lift; // Encoder and motor on same port
        this.lift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        this.lift.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        this.arm = arm;
        this.dropper = dropper;
        this.limit = limit;
    }


    public double getCurrentLiftPos(){ return lift.getCurrentPosition(); }

    public double getTargetLiftPos(){
        return target_pos;
    }

    public double getCurrentArmPos(){
        return arm.getPosition();
    }

    public double getCurrentDropperPos(){
        return dropper.getPosition();
    }

    public double getPower(){
        return lift.getPower();
    }

    public double[] getPIDTerms(){
        return new double[]{p_term, i_term, d_term};
    }

    public boolean reachedTarget(){
        double min = target_pos - 30;
        double max = target_pos + 30;
        return min <= target_pos && target_pos <= max;
    }

    public void raise(double target_ticks){
        if (0 <= target_ticks && target_ticks <= Status.UPPER_LIMIT){
            target_pos = target_ticks;
        }
    }

    public void extend(double target_pos){
        if (getCurrentLiftPos() > 10){
            arm.setPosition(target_pos);
        }
    }

    public void deposit(double target_pos){
        dropper.setPosition(target_pos);
    }


    public void updateLift(){
        double curr_pos = lift.getCurrentPosition();
        double error = target_pos - curr_pos;

        p_term = error * Status.kP;

        integral += error;
        i_term = integral * Status.kI;

        double derivative = error - past_error;
        d_term = derivative * Status.kD;

        double power = p_term + i_term + d_term;
        if (power < 0){
            power *= Status.LOWER_SPEED;
        } else {
            power *= Status.RAISE_SPEED;
        }
        lift.setPower(power);
        past_error = error;
    }

    public void updateStates(int bottom, int height_preset, int extension){
        this.bottom = bottom;
        this.height_preset = height_preset;
        this.extension = extension;
    }

    public double[] getStates(){
        return new double[]{bottom, height_preset, extension};
    }
}
