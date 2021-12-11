package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.util.Status;

public class Lift {
    private final DcMotor lift;
    private final Servo arm;

    private boolean lift_reached = true;

    private double target_pos;
    private double integral;
    private double past_error;
    private double p_term;
    private double i_term;
    private double d_term;


    public Lift(DcMotor lift, Servo arm){
        lift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        lift.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        lift.setDirection(DcMotorSimple.Direction.REVERSE);

        this.lift = lift; // Encoder and motor on same port
        this.arm = arm;
    }

    public void extend(double target_ticks, boolean tracking){
        if (0 <= target_ticks && target_ticks <= Status.UPPER_LIMIT){
            target_pos = target_ticks;
            lift_reached = !tracking;
        }
    }

    public void rotate(double target_pos){
        arm.setPosition(target_pos);
    }

    public boolean ifReached(double check_pos){
        double min = check_pos - 1000;
        double max = check_pos + 1000;
        if (!lift_reached && min <= getCurrentLiftPos() && getCurrentLiftPos() <= max){
            lift_reached = true;
            return true;
        }
        return false;
    }

    public void updateLift(){
        double curr_pos = getCurrentLiftPos();
        double error = target_pos - curr_pos;

        p_term = error * Status.kP;

        integral += error;
        i_term = integral * Status.kI;

        double derivative = error - past_error;
        d_term = derivative * Status.kD;

        double power = p_term + i_term + d_term;

        if (curr_pos < Status.RETRACT_POWER_THRESHOLD){
            lift.setPower(power * Status.RETRACT_SPEED);
        } else {
            lift.setPower(power * Status.MAX_SPEED);
        }
        past_error = error;
    }

    public double getCurrentLiftPos(){
        return lift.getCurrentPosition();
    }
    public double getTargetLiftPos(){
        return target_pos;
    }
    public double getCurrentArmPos(){
        return arm.getPosition();
    }
    public double getPower(){
        return lift.getPower();
    }
    public double[] getPIDTerms(){
        return new double[]{p_term, i_term, d_term};
    }
}
