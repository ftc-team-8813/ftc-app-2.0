package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;

import org.firstinspires.ftc.teamcode.util.Logger;
import org.firstinspires.ftc.teamcode.util.Storage;

public class Lift {
    private DcMotor lift1;
    private DcMotor lift2;
    private DcMotor pivoter;
    public DigitalChannel lift_limit;
    private Logger log = new Logger("Lift");

    public double lift_target;
    private double lift_summed_error;
    public double pivot_target;
    private double pivot_summed_error;
    public boolean lifting;
    public boolean pivoting;
    private boolean can_reset;

    private double LIFT_KP;
    private double LIFT_KI;
    private double PIVOT_KP;
    private double PIVOT_KI;
    private double MAX_HEIGHT;
    private double TURN_LIMIT;
    private double DEGREES_PER_TICK;
    private double PITSTOP;

    // Raising makes encoder values more negative
    public Lift(DcMotor lift1, DcMotor lift2, DcMotor pivoter, DigitalChannel lift_limit) {
        this.lift1 = lift1;
        this.lift2 = lift2;
        this.pivoter = pivoter;
        this.lift_limit = lift_limit;
        lift_target = 0.1;
        pivot_target = 0.01;

        LIFT_KP = Storage.getJsonValue("lift_kp");
        LIFT_KI = Storage.getJsonValue("lift_ki");
        PIVOT_KP = Storage.getJsonValue("pivot_kp");
        PIVOT_KI = Storage.getJsonValue("pivot_ki");
        MAX_HEIGHT = Storage.getJsonValue("max_height");
        TURN_LIMIT = Storage.getJsonValue("turn_limit");
        DEGREES_PER_TICK = Storage.getJsonValue("degrees_per_tick");
        PITSTOP = Storage.getJsonValue("pitstop");
    }

    public void resetLift() {
        if (liftAtBottom()) {
            lift1.setPower(0);
            lift2.setPower(0);

            lift1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            lift2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            lift1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            lift2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

            pivoter.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            pivoter.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

            can_reset = false;
        } else {
            lift1.setPower(-0.3);
            lift2.setPower(-0.3);
        }
    }

    public void raise(double target_ticks, boolean tracking) {
        if (0 <= target_ticks && target_ticks <= MAX_HEIGHT) {
            lift_target = target_ticks;
            lifting = tracking;
        }
    }

    public void raise(double target_ticks) {
        raise(target_ticks, false);
    }

    /**
     * @param target_theta 0 degrees is vertical, left is negative, right is positive
     */
    public void rotate(double target_theta, boolean tracking) {
        if (-TURN_LIMIT <= target_theta && target_theta <= TURN_LIMIT && getLiftPosition() > PITSTOP) {
            pivot_target = target_theta;
            pivoting = tracking;
        }
    }

    public void rotate(double target_theta) {
        rotate(target_theta, false);
    }

    public boolean liftReached() {
        double min = lift_target - 1000;
        double max = lift_target + 1000;
        log.i("Lift Position: %d", getLiftPosition());
        log.i("Target Position: %f", lift_target);
        if (lifting){
            log.i("%d", 1);
        } else {
            log.i("%d", 0);
        }
        if (min <= getLiftPosition() && getLiftPosition() <= max && lifting) {
            lifting = false;
            return true;
        }
        return false;
    }

    public boolean pivotReached() {
        double min = pivot_target - 1;
        double max = pivot_target + 1;
        if (min <= getPivotPosition() && getPivotPosition() <= max && pivoting){
            pivoting = false;
            return true;
        }
        return false;
    }

    public void update() {
        if (liftAtBottom() && can_reset) {
            lift1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            lift1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            can_reset = false;
        } else if (!liftAtBottom()) {
            can_reset = true;
        }

        // Lift
        double lift_error = lift_target - getLiftPosition();
        lift_summed_error += lift_error;

        double lift_proportional = lift_error * LIFT_KP;
        double lift_integral = lift_summed_error * LIFT_KI;

        lift1.setPower(lift_proportional);
        lift2.setPower(lift_integral);

        // Pivot
        double pivot_error = pivot_target - getPivotPosition();
        pivot_summed_error += pivot_error;

        double pivot_proportional = pivot_error * PIVOT_KP;
        double pivot_integral = pivot_summed_error * PIVOT_KI;

        pivoter.setPower((pivot_proportional + pivot_integral) * 0.5);
    }

    public int getLiftPosition() {
        // Turns negative ticks into positive
        return -lift1.getCurrentPosition();
    }

    public double getPivotPosition() {
        return -pivoter.getCurrentPosition() * DEGREES_PER_TICK;
    }

    public boolean liftAtBottom() {
        return !lift_limit.getState();
    }
}