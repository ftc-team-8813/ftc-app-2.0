package org.firstinspires.ftc.teamcode.hardware.navigation;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.hardware.IMU;

/**
 * Tracks position relative to starting location
 * Currently uses motor encoders for position feedback
 */
public class Odometry {
    public DcMotor top_left;
    public DcMotor top_right;
    public IMU imu;
    public double x, y;
    public double past_x, past_y;
    private double past_heading;
    private double target_pos;
    final double TICKS = 537.6;
    final double CIRCUMFERENCE = 2.83 * Math.PI; // Inches
    final double h = 0; // Width of the robot

    public Odometry(DcMotor top_left, DcMotor top_right, IMU imu){
        this.top_left = top_left;
        this.top_right = top_right;
        this.imu = imu;
    }

    /**
     * Updates overall x, y based on deltas from arc
     */
    public void updateDeltas(){
        double curr_l = top_left.getCurrentPosition();
        double curr_r = top_right.getCurrentPosition();
        double l = curr_l - past_x;
        double r = curr_r - past_y;
        double x;
        double deltax = 0;
        double deltay = 0;
        if (r > l){
            x = l / getHeading();
            deltax = Math.sin(getHeading() * (x + h));
            deltay = Math.cos(getHeading()) * (x + h) - (x + h);
        } else if (l > r){
            x = r / -getHeading();
            deltax = Math.sin(-getHeading() * (x + h));
            deltay = (x + h) - Math.cos(-getHeading()) * (x + h);
        }
        this.x += deltax;
        this.y += deltay;
        past_x = curr_l;
        past_y = curr_r;
    }

    /**
     * Coverts wanted distance to ticks
     * @param distance In inches
     */
    public void setTargetPos(double distance){
        double rotations = distance / CIRCUMFERENCE;
        double total_ticks = rotations * TICKS;
        target_pos = total_ticks;
    }

    public void setStartingPos(double start_x, double start_y){
        this.x = start_x;
        this.y = start_y;
    }

    /**
     * Used to have x, y in inches instead of ticks
     * @param ticks covered so far
     * @return return inches
     */
    public double ticksToInches(double ticks){
        double rotations = ticks / TICKS;
        return rotations * CIRCUMFERENCE;
    }

    public double getX(){
        return this.x;
    }

    public double getY(){
        return this.y;
    }

    public double getHeading(){
        return Math.toRadians(this.imu.getHeading());
    }
}
