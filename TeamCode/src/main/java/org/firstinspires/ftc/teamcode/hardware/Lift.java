package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.util.Status;

public class Lift {
    private final DcMotor lift;
    private final Servo arm;
    private final DigitalChannel limit_switch;
    private final Servo outrigger;

    private boolean lift_reached = true;

    private double target_pos;
    private double integral;
    private double past_error;
    private double p_term;
    private double i_term;
    private double d_term;


    public Lift(DcMotor lift, Servo arm, DigitalChannel limit_switch, Servo outrigger){
        lift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        lift.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        lift.setDirection(DcMotorSimple.Direction.REVERSE);

        this.lift = lift; // Encoder and motor on same port
        this.arm = arm;
        this.limit_switch = limit_switch;
        this.outrigger = outrigger;
    }

    public void resetEncoder(){
        lift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        lift.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void extend(double target_ticks, boolean tracking){
        if (0 <= target_ticks && target_ticks <= Status.UPPER_LIMIT){
            target_pos = target_ticks;
            if (tracking){
                lift_reached = false;
            }
        }
    }

    public void rotate(double target_pos){
        arm.setPosition(target_pos);
    }

    public boolean ifReached(double check_pos){
        double min = check_pos - 1000;
        double max = check_pos + 1000;
        if (!lift_reached && min <= getLiftCurrentPos() && getLiftCurrentPos() <= max){
            lift_reached = true;
            return true;
        }
        return false;
    }

    public void moveOutrigger(double target_pos) { outrigger.setPosition((target_pos)); }

    public void updateLift(){
        double curr_pos = getLiftCurrentPos();
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

    public double getLiftCurrentPos(){
        return lift.getCurrentPosition();
    }
    public boolean limitPressed(){
        return !limit_switch.getState();
    }
    public double getLiftTargetPos(){
        return target_pos;
    }
    public void resetLitTarget(){
        target_pos = 0;
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
