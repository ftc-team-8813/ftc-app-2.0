package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.util.Status;

public class Lift {
    private final DcMotor lift;
    private final DcMotor lift2;
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
    private final ElapsedTime timer = new ElapsedTime();
    public boolean auto_override = false;
    private boolean was_reset = false;

    public Lift(DcMotor lift, DcMotor lift2, Servo arm, DigitalChannel limit_switch, Servo outrigger){
        lift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        lift.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        lift2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        lift.setDirection(DcMotorSimple.Direction.REVERSE);
        lift2.setDirection(DcMotorSimple.Direction.REVERSE);

        this.lift = lift; // Encoder and motor on same port
        this.lift2 = lift2; // No encoder
        this.arm = arm;
        this.limit_switch = limit_switch;
        this.outrigger = outrigger;
    }

    public void resetEncoder(){
        lift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        lift.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void extend(double target_ticks, boolean tracking){
        if (target_ticks <= Status.UPPER_LIMIT){
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
        if (!auto_override) {
            double curr_pos = getLiftCurrentPos();
            double error = target_pos - curr_pos;

            p_term = error * Status.kP;

        integral += error * timer.seconds();
        i_term = integral * Status.kI;

        double derivative = (error - past_error) / timer.seconds();
        d_term = derivative * Status.kD;

            double power = p_term + i_term + d_term;

        if (curr_pos < Status.RETRACT_POWER_THRESHOLD){
            lift.setPower(power * Status.RETRACT_SPEED);
            lift2.setPower(power * Status.RETRACT_SPEED);
        } else {
            lift.setPower(power * Status.MAX_SPEED);
            lift2.setPower(power * Status.MAX_SPEED);
        }
        past_error = error;

        if (lift.getTargetPosition() == 0 && Math.signum(power) == -1 && curr_pos < 5000 && !limitPressed()) {
            lift.setPower(-1);
            lift2.setPower(-1);
        }
        timer.reset();
    }

        if (!was_reset && limitPressed()){
            resetEncoder();
            was_reset = true;
        } else if (!limitPressed()){
            was_reset = false;
        }
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
    public void resetLiftTarget(){
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
