package org.firstinspires.ftc.teamcode.hardware.navigation;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.internal.android.dx.cf.direct.CodeObserver;
import org.firstinspires.ftc.teamcode.hardware.IMU;
import org.firstinspires.ftc.teamcode.vision.ImageDraw;

/**
 * Tracks position relative to starting location
 * Currently uses motor encoders for position feedback
 */
public class Odometry {
    public final DcMotor l_enc;
    public final DcMotor r_enc;
    private final IMU imu;
    public double x, y, theta;
    public double past_l, past_r, past_theta;
    public double calc_heading;
    private static final double TICKS_PER_INCH = 29.10196;
    private static final double h = 7.5; // Half-Width of the robot in inches
    private static final double TURN_FACTOR = 0.9945; // adjustment factor for something or another
    public ImageDraw.Color drawColor = ImageDraw.BLUE;
    
    public Odometry(DcMotor l_enc, DcMotor r_enc){
        this.l_enc = l_enc;
        this.r_enc = r_enc;
        this.imu = new OdoIMU(this);
    }

    /**
     * Updates overall x, y, and theta based on deltas from arc
     * x (inches) is left/right, y (inches) is forward/backward
     * theta (radians) is based on Unit Circle
     */
    public void updateDeltas(){
        double l = getCurrentL() - past_l;
        double r = getCurrentR() - past_r;
        past_l = getCurrentL();
        past_r = getCurrentR();

        double delta_heading = Math.toRadians((imu.getHeading() + 90) - past_theta);
        double arc_length = (l+r) / 2;

        this.x = -arc_length * Math.sin(delta_heading);
        this.y = arc_length * Math.cos(delta_heading);
        this.theta = (this.theta + delta_heading) % (2 * Math.PI);
    }

    public double getCurrentL(){
        return -ticksToInches(l_enc.getCurrentPosition());
    }

    public double getCurrentR(){
        return -ticksToInches(r_enc.getCurrentPosition());
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

    public void setPosition(double x, double y)
    {
        this.x = x;
        this.y = y;
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
