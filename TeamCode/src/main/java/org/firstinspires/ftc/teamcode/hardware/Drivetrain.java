package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.hardware.events.AutoMoveEvent;
import org.firstinspires.ftc.teamcode.hardware.navigation.Odometry;
import org.firstinspires.ftc.teamcode.util.event.EventBus;

import static com.qualcomm.robotcore.hardware.DcMotorSimple.Direction.REVERSE;

/**
 * Drivetrain -- handles movement of the drive wheels.
 */

public class Drivetrain {
    public final DcMotor top_left;
    public final DcMotor bottom_left;
    public final DcMotor top_right;
    public final DcMotor bottom_right;
    private Odometry odometry;
    private EventBus ev;
    private int auto_id = -1;
    public double l_target = 0;
    public double r_target = 0;
    double past_l_target = 0;
    double past_r_target = 0;
    boolean send_event = false;

    public Drivetrain(DcMotor top_left, DcMotor bottom_left, DcMotor top_right, DcMotor bottom_right, Odometry odometry){
        this.top_left = top_left;
        this.bottom_left = bottom_left;
        this.top_right = top_right;
        this.bottom_right = bottom_right;
        this.odometry = odometry;

        //Reverses left side to match right side rotation and sets mode
        top_right.setDirection(REVERSE);
        bottom_right.setDirection(REVERSE);
        top_left.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        bottom_left.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        top_right.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        bottom_right.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        odometry.resetEncoders();
    }

    public void resetEncoders(){
        top_right.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        bottom_right.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        top_left.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        bottom_left.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        // reset to driving mode
        top_left.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        bottom_left.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        top_right.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        bottom_right.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    /**
     * Move the drivetrain based on gamepad-compatible inputs
     * @param left_stick_y Left Wheel Velocity
     * @param right_stick_y Right Wheel Velocity
     */
    public void telemove(double left_stick_y, double right_stick_y){
        //Subtracts power from forward based on the amount of rotation in the other stick
        double left_wheel_speed = -left_stick_y + right_stick_y;
        double right_wheel_speed = -left_stick_y - right_stick_y;
        top_left.setPower(left_wheel_speed);
        bottom_left.setPower(left_wheel_speed);
        top_right.setPower(right_wheel_speed);
        bottom_right.setPower(right_wheel_speed);
    }

    /**
     * Updates target distance in ticks
     * Appends to current position to account for previous movements
     * @param distance Desired distance in inches
     */
    public void setTargetPos(double distance){
        double rotations = distance / odometry.CIRCUMFERENCE;
        double target_ticks = rotations * odometry.TICKS;
        if (l_target != 0.0 && r_target != 0.0) {
            l_target = target_ticks + getPastTicks();
            r_target = target_ticks + getPastTicks();
        }
        send_event = true;
    }

    /**
     * Turns robot certain degrees
     * @param target_angle Range = 180 to -180 (counter-clockwise)
     */
    public void setTargetTurn(double target_angle){
        double target_ticks = odometry.getH() * target_angle;
        if (l_target != 0 && r_target != 0) {
            double direction = Math.signum(target_angle);
            l_target = direction * -target_ticks + getPastTicks();
            r_target = direction * target_ticks + getPastTicks();
        }
    }

    /**
     * Accelerates towards a set target positions for both wheels
     * Must be ran at the end of each loop cycle
     */
    public void autoPIDUpdate(){
        // TODO Find PID constant
        final double kP = 1;
        double l_error = l_target - past_l_target;
        double r_error = r_target - past_r_target;
        double left_wheel_speed = l_error * kP;
        double right_wheel_speed = r_error * kP;
        // TODO Increase deadband for error to make it possible to reach
        if (l_error > 10 || l_error < -10){
            top_left.setPower(left_wheel_speed);
            bottom_left.setPower(left_wheel_speed);
            top_right.setPower(right_wheel_speed);
            bottom_right.setPower(right_wheel_speed);
        } else {
            if (ev != null && send_event){
                past_l_target = l_target;
                past_r_target = r_target;
                send_event = false;
                ev.pushEvent(new AutoMoveEvent(AutoMoveEvent.MOVED));
            }
        }
    }

    /**
     * Averages both encoders to get center line position
     * Also negates tick changes from turns
     */
    public double getPastTicks(){
        return (past_l_target + past_r_target) / 2.0;
    }

    public Odometry getOdometry(){
        return this.odometry;
    }

    public void connectEventBus(EventBus ev){
        this.ev = ev;

    }
}
