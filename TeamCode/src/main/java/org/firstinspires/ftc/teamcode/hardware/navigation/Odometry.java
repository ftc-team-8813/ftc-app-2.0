package org.firstinspires.ftc.teamcode.hardware.navigation;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.hardware.IMU;
import org.firstinspires.ftc.teamcode.vision.ImageDraw;

/**
 * Tracks position relative to starting location
 * Currently uses motor encoders for position feedback
 */
public class Odometry {
    public DcMotor l_enc;
    public DcMotor r_enc;
    public IMU imu;
    public double x, y;
    public double past_l, past_r;
    final double TICKS = 537.6;
    final double CIRCUMFERENCE = 2.83 * Math.PI; // Inches
    final double h = 7.5; // Half-Width of the robot in ticks
    public ImageDraw.Color drawColor = ImageDraw.BLACK;

    public Odometry(DcMotor l_enc, DcMotor r_enc, IMU imu){
        this.l_enc = l_enc;
        this.r_enc = r_enc;
        this.imu = imu;
    }

    /**
     * Updates overall x, y based on deltas from arc
     * x is forward/back and y is left/right
     */
    public void updateDeltas(){
        double radius, leg_x, leg_y, deltax, deltay, chord;
        double curr_l = getCurrentL();
        double curr_r = getCurrentR();
        double l = curr_l - past_l;
        double r = curr_r - past_r;
        double heading = Math.toRadians(imu.getHeading());
        
        double dist = (l + r) / 2;

        leg_x = Math.cos(heading) * dist;
        leg_y = Math.sin(heading) * dist;

        this.x += leg_x;
        this.y += leg_y;
        past_l = curr_l;
        past_r = curr_r;
    }

    public double getCurrentL(){
        return ticksToInches(l_enc.getCurrentPosition());
    }

    public double getCurrentR(){
        return ticksToInches(r_enc.getCurrentPosition());
    }

    public double ticksToInches(double ticks){
        double ratio = ticks / TICKS;
        return ratio * CIRCUMFERENCE;
    }

    public void setStartingPos(double start_y){
        this.x = -65;
        this.y = start_y;
    }

    public double getX(){
        return this.x;
    }

    public double getY(){
        return this.y;
    }

    public double getH(){
        return this.h;
    }
}
