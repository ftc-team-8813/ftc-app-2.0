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
    public double x;
    public double y;
    private double past_heading;
    private double target_pos;
    final double TICKS = 537.6;
    final double CIRCUMFERENCE = 2.83 * Math.PI; // Inches

    public Odometry(DcMotor top_left, DcMotor top_right, IMU imu){
        this.top_left = top_left;
        this.top_right = top_right;
        this.imu = imu;
    }

    /**
     * Updates overall x, y if robot turns outside 2 degree deadband
     */
    public void updateDeltas(){
        if (past_heading + 2 < getHeading() || past_heading > getHeading() - 2){
            double deltax;
            double deltay;
            double average_dist = (top_left.getCurrentPosition() + top_right.getCurrentPosition())/2.0;
            deltax = Math.cos(getHeading()) * average_dist;
            deltay = Math.sin(getHeading()) * average_dist;
            this.x += ticksToInches(deltax);
            this.y += ticksToInches(deltay);
            past_heading = getHeading();
        }
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
        return this.imu.getHeading();
    }
}
