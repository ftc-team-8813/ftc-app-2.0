package org.firstinspires.ftc.teamcode.hardware.navigation;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.hardware.IMU;
import org.firstinspires.ftc.teamcode.vision.ImageDraw;

/**
 * Tracks position relative to starting location
 * Currently uses motor encoders for position feedback
 */
public class Odometry {
    public DcMotor top_left;
    public DcMotor top_right;
    public double x, y;
    public double past_l, past_r;
    final double TICKS = 537.6;
    final double CIRCUMFERENCE = 2.83 * Math.PI; // Inches
    final double h = 7.5; // Half-Width of the robot in ticks
    public ImageDraw.Color drawColor = ImageDraw.BLUE;
    
    public IMU imu;

    public Odometry(DcMotor top_left, DcMotor top_right, IMU imu){
        this.top_left = top_left;
        this.top_right = top_right;
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
        double heading = Math.toRadians(imu.getHeading()); // (curr_r - curr_l)/(2 * h);
        
        double dist = (l + r) / 2;

        leg_x = Math.cos(heading) * dist;
        leg_y = Math.sin(heading) * dist;

//        if (r > l){
//            radius = l / delta_heading;
//            leg_x = Math.cos(delta_heading) * (radius + h) - (radius + h);
//            leg_y = Math.sin(delta_heading * (radius + h));
//        } else if (l > r){
//            radius = r / delta_heading;
//            leg_x = (radius + h) - Math.cos(delta_heading) * (radius + h);
//            leg_y = Math.sin(delta_heading * (radius + h));
//        } else {
//            leg_x = Math.cos(delta_heading) * l;
//            leg_y = Math.sin(delta_heading) * l;
//        }
//
//        chord = Math.sqrt(Math.pow(leg_x, 2) + Math.pow(leg_y, 2));
//        deltax = chord * Math.sin(delta_heading);
//        deltay = chord * Math.cos(delta_heading);

        this.x += leg_x;
        this.y += leg_y;
        // heading += delta_heading;
        past_l = curr_l;
        past_r = curr_r;
    }

    public double getCurrentL(){
        return ticksToInches(top_left.getCurrentPosition());
    }

    public double getCurrentR(){
        return ticksToInches(top_right.getCurrentPosition());
    }

    public double ticksToInches(double ticks){
        return ticks / 16.528;
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
}
