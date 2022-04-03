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

    private double lift_target;
    private double lift_summed_error;
    private double pivot_target;
    private double pivot_summed_error;
    private boolean lift_reached;
    private boolean was_lift_reached;
    private int lift_count;
    private boolean pivot_reached;
    private boolean was_pivot_reached;
    private int pivot_count;
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
            lift1.setPower(-0.4);
            lift2.setPower(-0.4);
        }
    }

    public void raise(double target_ticks) {
        if (0 <= target_ticks && target_ticks <= MAX_HEIGHT) {
            lift_target = target_ticks;
        }
    }

    /**
     * @param target_theta 0 degrees is vertical, left is negative, right is positive
     */
    public void rotate(double target_theta) {
        if (-TURN_LIMIT <= target_theta && target_theta <= TURN_LIMIT && getLiftPosition() > PITSTOP - 5000) {
            pivot_target = target_theta;
        }
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
        double lift_power = lift_proportional + lift_integral;

        if (lift_power < 0) lift_power *= 0.4;

        lift1.setPower(lift_power);
        lift2.setPower(lift_power);

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

    public double getLiftTarget(){
        return lift_target;
    }

    public double getPivotTarget(){
        return pivot_target;
    }

    public boolean liftReached(){
        double min = getLiftTarget() - 1000;
        double max = getLiftTarget() + 1000;
        return min < getLiftPosition() && getLiftPosition() < max;
    }

    public boolean pivotReached(){
        double min = getPivotTarget() - 1000;
        double max = getPivotTarget() + 1000;
        return min < getPivotPosition() && getPivotPosition() < max;
    }

    public boolean liftAtBottom() {
        return !lift_limit.getState();
    }
}