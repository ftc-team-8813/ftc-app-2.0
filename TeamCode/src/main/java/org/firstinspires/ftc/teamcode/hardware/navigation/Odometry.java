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
    private IMU imu;
    public double x, y;
    public double past_l, past_r;
    public final double TICKS_PER_INCH = 29.167;
    final double h = 7.5; // Half-Width of the robot in ticks
    public ImageDraw.Color drawColor = ImageDraw.BLUE;
    

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
        double l = getCurrentL() - past_l;
        double r = getCurrentR() - past_r;
        double heading = Math.toRadians(imu.getHeading());
        double dist = (l + r) / 2;
        
        double leg_x = Math.cos(heading) * dist;
        double leg_y = Math.sin(heading) * dist;

        this.x += leg_x;
        this.y += leg_y;
        past_l = getCurrentL();
        past_r = getCurrentR();
    }

    public double getCurrentL(){
        return ticksToInches(l_enc.getCurrentPosition());
    }

    public double getCurrentR(){
        return ticksToInches(r_enc.getCurrentPosition());
    }

    public double ticksToInches(double ticks){
        return ticks / TICKS_PER_INCH;
    }

    public double inchesToTicks(double inches){
        return inches * TICKS_PER_INCH;
    }

    public void resetEncoders(){
        l_enc.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        r_enc.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        l_enc.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        r_enc.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void setStartingPos(double start_y){
        this.x = 0;
        this.y = start_y;
    }

    public IMU getIMU(){ return this.imu;}

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
