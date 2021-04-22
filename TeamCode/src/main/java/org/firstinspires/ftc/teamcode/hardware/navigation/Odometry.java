package org.firstinspires.ftc.teamcode.hardware.navigation;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.hardware.IMU;
import org.firstinspires.ftc.teamcode.vision.ImageDraw;

/**
 * Tracks position relative to starting location
 * Currently uses motor encoders for position feedback
 */
public class Odometry
{
    public DcMotor l_enc;
    public DcMotor r_enc;
    private IMU imu;
    // TODO these variables should be private
    public double x, y;
    public double past_l, past_r;
    public double calc_heading;
    public static final double TICKS_PER_INCH = 29.10196;
    public static final double h = 7.5; // Half-Width of the robot in inches
    public static final double TURN_FACTOR = 0.9945; // adjustment factor for something or another
    public ImageDraw.Color drawColor = ImageDraw.BLUE;
    
    public Odometry(DcMotor l_enc, DcMotor r_enc)
    {
        this.l_enc = l_enc;
        this.r_enc = r_enc;
        this.imu = new OdoIMU(this);
    }
    
    /**
     * Updates overall x, y based on deltas from arc
     * x is forward/back and y is left/right
     */
    public void updateDeltas()
    {
        double new_l = getCurrentL();
        double new_r = getCurrentR();
        double dl = new_l - past_l;
        double dr = new_r - past_r;
        
        double rotation_amt = TURN_FACTOR * (new_r - new_l) / 2;
        
        past_l = new_l;
        past_r = new_r;
        double heading = rotation_amt / h;
        calc_heading = heading;
        double dist = (dl + dr) / 2;
        
        double leg_x = Math.cos(heading) * dist;
        double leg_y = Math.sin(heading) * dist;
        
        this.x += leg_x;
        this.y += leg_y;
    }
    
    public double getCurrentL()
    {
        return -ticksToInches(l_enc.getCurrentPosition());
    }
    
    public double getCurrentR()
    {
        return -ticksToInches(r_enc.getCurrentPosition());
    }
    
    public double ticksToInches(double ticks)
    {
        return ticks / TICKS_PER_INCH;
    }
    
    public double inchesToTicks(double inches)
    {
        return inches * TICKS_PER_INCH;
    }
    
    public void resetEncoders()
    {
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
    
    public IMU getIMU() { return this.imu;}
    
    public double getX()
    {
        return this.x;
    }
    
    public double getY()
    {
        return this.y;
    }
    
    public double getH()
    {
        return this.h;
    }
}
