package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.hardware.events.NavMoveEvent;
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
    public final Odometry odometry;
    public IMU imu;
    private EventBus ev;
    public int auto_id = -1;
    final double TICKS = 537.6;
    final double CIRCUMFERENCE = 2.83 * Math.PI; // Inches
    double target_pos = 0;
    boolean send_event = false;

    public Drivetrain(DcMotor top_left, DcMotor bottom_left, DcMotor top_right, DcMotor bottom_right){
        this.top_left = top_left;
        this.bottom_left = bottom_left;
        this.top_right = top_right;
        this.bottom_right = bottom_right;
        this.odometry = new Odometry(top_left, top_right);

        //Reverses left side to match right side rotation and sets mode
        top_right.setDirection(REVERSE);
        bottom_right.setDirection(REVERSE);
        // TODO drive motor encoders seem to be faulty right now
        top_left.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        bottom_left.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        top_right.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        bottom_right.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
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
        double rotations = distance / CIRCUMFERENCE;
        double total_ticks = rotations * TICKS;
        target_pos = total_ticks + top_left.getCurrentPosition();
        send_event = true;
    }

    /**
     * Accelerates towards a set target position
     */
    public void autoPIDUpdate(){
        // TODO Find PID constant
        final double kP = 1;
        double error = target_pos - top_left.getCurrentPosition();
        double left_wheel_speed = -error * kP;
        double right_wheel_speed = error * kP;
        // TODO Increase deadband for error to make it possible to reach
        if (error > 10 || error < -10){
            top_left.setPower(left_wheel_speed);
            bottom_left.setPower(left_wheel_speed);
            top_right.setPower(right_wheel_speed);
            bottom_right.setPower(right_wheel_speed);
        } else {
            if (ev != null && send_event){
                send_event = false;
                ev.pushEvent(new NavMoveEvent(NavMoveEvent.NAVIGATION_COMPLETE));
            }
        }
    }

    public void connectEventBus(EventBus ev){
        this.ev = ev;
    }
}
