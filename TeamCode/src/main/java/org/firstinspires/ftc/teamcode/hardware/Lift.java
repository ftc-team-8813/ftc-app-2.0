package org.firstinspires.ftc.teamcode.hardware;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;

import org.firstinspires.ftc.teamcode.util.Status;

public class Lift {

    private DcMotor lift1;
    private DcMotor lift2;
    private DcMotor pivoter;
    public DigitalChannel lift_limit;
    public double lift_target;
    public double pivot_target;
    private boolean lifting;
    private boolean pivoting;

    // Raising makes encoder values more negative
    public Lift(DcMotor lift1, DcMotor lift2, DcMotor pivoter, DigitalChannel lift_limit){
        this.lift1 = lift1;
        this.lift2 = lift2;
        this.pivoter = pivoter;
        this.lift_limit = lift_limit;
        lift_target = 0;
        pivot_target = 0;
    }

    public void resetLift(){
        if (liftAtBottom()){
            lift1.setPower(0);
            lift2.setPower(0);

            lift1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            lift2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            lift1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            lift2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        } else {
            lift1.setPower(-0.4);
            lift2.setPower(-0.4);
        }
    }

    public void raise(double target_ticks, boolean tracking){
        if (0 < target_ticks && target_ticks < 30000){
            lift_target = target_ticks;
            lifting = tracking;
        }
    }

    public void raise(double target_ticks){
        raise(target_ticks, false);
    }

    /**
     * @param target_theta 0 degrees is vertical, left is negative, right is positive
     */
    public void rotate(double target_theta, boolean tracking){
        if (-90 < target_theta && target_theta < 90){
            pivot_target = target_theta;
            pivoting = tracking;
        }
    }

    public void rotate(double target_theta){
        rotate(target_theta, false);
    }

    public boolean liftReached(){
        double min = lift_target - 100;
        double max = lift_target + 100;
        if (min < getLiftPosition() && getLiftPosition() < max && lifting){
            lifting = false;
            return true;
        }
        return false;
    }

    public boolean pivotReached(){
        double min = pivot_target - 10;
        double max = pivot_target + 10;
        if (min < getPivotPosition() && getPivotPosition() < max && pivoting){
            pivoting = false;
            return true;
        }
        return false;
    }

    public void update(){
        // Lift
        double lift_error = lift_target - getLiftPosition();
        lift1.setPower(lift_error * Status.KP);
        lift2.setPower(lift_error * Status.KP);

        // Pivot
        double pivot_error = pivot_target - getPivotPosition();
        pivoter.setPower(pivot_error * Status.KP);
    }

    public int getLiftPosition(){
        // Turns negative ticks into positive
        return -lift1.getCurrentPosition();
    }

    public int getPivotPosition(){
        return pivoter.getCurrentPosition();
    }

    public boolean liftAtBottom(){
        return !lift_limit.getState();
    }
}