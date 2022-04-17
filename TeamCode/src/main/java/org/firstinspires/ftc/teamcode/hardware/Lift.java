package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.util.Logger;
import org.firstinspires.ftc.teamcode.util.LoopTimer;
import org.firstinspires.ftc.teamcode.util.Storage;

public class Lift {
    private DcMotor lift1;
    private DcMotor lift2;
    private DcMotor pivoter;
    public DigitalChannel lift_limit;
    public DigitalChannel pivot_limit;
    private Logger log = new Logger("Lift");

    private double lift_power;
    private double pivot_power;
    private double lift_target; // Setting to 8813 disables PID
    private double lift_summed_error;
    private double lift_last_error = 0;
    private double pivot_target; // Setting to 8813 disables PID
    private double pivot_summed_error;
    private double pivot_last_error = 0;

    private int reset_id = 0;
    private boolean can_reset;
    private boolean pivot_reset = false;
    private ElapsedTime pivot_timer = new ElapsedTime();
    private boolean pivot_correcting = false;

    private double LIFT_KP;
    private double LIFT_KI;
    private double LIFT_KD;
    private double PIVOT_KP;
    private double PIVOT_KI;
    private double PIVOT_KD;
    private double MAX_HEIGHT;
    private double TURN_LIMIT;
    private double DEGREES_PER_TICK;
    private double PITSTOP;
    private double PIVOT_THRESHOLD;
    private double LIFT_THRESHOLD;

    public double print_lift_integral = 0;
    public double print_pivot_integral = 0;

    // Raising makes encoder values more negative
    public Lift(DcMotor lift1, DcMotor lift2, DcMotor pivoter, DigitalChannel lift_limit, DigitalChannel pivot_limit) {
        this.lift1 = lift1;
        this.lift2 = lift2;
        this.pivoter = pivoter;
        this.lift_limit = lift_limit;
        this.pivot_limit = pivot_limit;
        lift_target = 10;
        pivot_target = 0.01;

        LIFT_KP = Storage.getJsonValue("lift_kp");
        LIFT_KI = Storage.getJsonValue("lift_ki");
        LIFT_KD = Storage.getJsonValue("lift_kd");
        PIVOT_KP = Storage.getJsonValue("pivot_kp");
        PIVOT_KI = Storage.getJsonValue("pivot_ki");
        PIVOT_KD = Storage.getJsonValue("pivot_kd");
        MAX_HEIGHT = Storage.getJsonValue("max_height");
        TURN_LIMIT = Storage.getJsonValue("turn_limit");
        DEGREES_PER_TICK = Storage.getJsonValue("degrees_per_tick");
        PITSTOP = Storage.getJsonValue("pitstop");
        LIFT_THRESHOLD = Storage.getJsonValue("lift_threshold");
        PIVOT_THRESHOLD = Storage.getJsonValue("pivot_threshold");
    }

    public void resetLift() {
        if (liftAtBottom()) {
            lift1.setPower(0);
            lift2.setPower(0);

            resetLiftEncoder();
            resetPivotEncoder();

            can_reset = false;
            pivot_reset = true;
        } else {
            lift1.setPower(-0.6);
            lift2.setPower(-0.6);
        }
    }

    public boolean resetPivot(String op_mode){
        switch (reset_id){
            case 0:
                raise(MAX_HEIGHT - 5000);
                rotate(getPivotPosition());
                if (liftReached()) {
                    reset_id += 1;
                }
                break;
            case 1:
                raise(PITSTOP - 10000);
                if (liftReached()){
                    reset_id += 1;
                }
                break;
            case 2:
                if (op_mode.contains("Blue")){
                    rotate(30.0);
                } else if (op_mode.contains("Red")){
                    rotate(-30.0);
                }
                if (pivotReached() && !pivot_correcting){
                    pivot_correcting = true;
                    pivot_timer.reset();
                    log.i("Pivot Reached");
                }
                if (pivot_timer.seconds() > 1 && pivot_correcting){
                    rotate(8813);
                    pivot_correcting = false;
                    reset_id += 1;
                    log.i("Stopped Motor");
                }
                break;
            case 3:
                reset_id = -1;
                return true;
        }
        update();
        return false;
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
        if ((-TURN_LIMIT <= target_theta && target_theta <= TURN_LIMIT) || target_theta == 8813) {
            pivot_target = target_theta;
        }
    }

    public void update() {
        if (liftAtBottom() && can_reset) {
            resetLiftEncoder();
            can_reset = false;
        } else if (!liftAtBottom()) {
            can_reset = true;
        }

        // Lift
        double lift_error = lift_target - getLiftPosition();
        lift_summed_error += lift_error * LoopTimer.getLoopTime();

        if (Math.abs(lift_error) > LIFT_THRESHOLD * 3) lift_summed_error = 0;

        double lift_proportional = lift_error * LIFT_KP;
        double lift_integral = lift_summed_error * LIFT_KI;
        double lift_derivative = (lift_error - lift_last_error) / LoopTimer.getLoopTime() * LIFT_KD;

        //if (lift_power < 0) lift_power *= 0.4;

        lift_power = Range.clip(lift_proportional + lift_integral + lift_derivative, -1.0, 0.9);

        if (lift_target != 8813) {
            lift1.setPower(lift_power);
            lift2.setPower(lift_power);
        }

        // Pivot
        double pivot_error = pivot_target - getPivotPosition();
        pivot_summed_error += pivot_error * LoopTimer.getLoopTime();

        if (Math.abs(pivot_error) > PIVOT_THRESHOLD * 3) pivot_summed_error = 0;

        double pivot_proportional = pivot_error * PIVOT_KP;
        double pivot_integral = pivot_summed_error * PIVOT_KI;
        double pivot_derivative = (pivot_error - pivot_last_error) / LoopTimer.getLoopTime() * PIVOT_KD;

        pivot_power = Range.clip(pivot_proportional + pivot_integral + pivot_derivative, -1.0, 1.0);

        if (pivot_target != 8813) {
            pivoter.setPower(pivot_power);
        } else {
            pivoter.setPower(0);
        }

        lift_last_error = lift_error;
        pivot_last_error = pivot_error;

        print_lift_integral = lift_summed_error;
        print_pivot_integral = pivot_summed_error;
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

    public double getLiftPower(){
        return lift_power;
    }

    public double getPivotPower(){
        return pivot_power;
    }

    public boolean liftReached(){
        double min = getLiftTarget() - LIFT_THRESHOLD;
        double max = getLiftTarget() + LIFT_THRESHOLD;
        return min < getLiftPosition() && getLiftPosition() < max;
    }

    public boolean pivotReached(){
        double min = getPivotTarget() - PIVOT_THRESHOLD;
        double max = getPivotTarget() + PIVOT_THRESHOLD;
        return min < getPivotPosition() && getPivotPosition() < max;
    }

    public boolean liftAtBottom() {
        return !lift_limit.getState();
    }

    public boolean pivotAtSide(){
        return !pivot_limit.getState();
    }

    public boolean getPivotReset(){
        return pivot_reset;
    }

    public void resetLiftEncoder(){
        lift1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        lift2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        lift1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        lift2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void resetPivotEncoder(){
        pivoter.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        pivoter.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void setPowers(double lift1, double lift2, double pivoter){
        this.lift1.setPower(lift1);
        this.lift2.setPower(lift2);
        this.pivoter.setPower(pivoter);
    }

    public void stop(){
        lift1.setPower(0);
        lift2.setPower(0);
        pivoter.setPower(0);
    }
}