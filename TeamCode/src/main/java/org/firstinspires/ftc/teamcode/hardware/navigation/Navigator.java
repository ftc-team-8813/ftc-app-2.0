package org.firstinspires.ftc.teamcode.hardware.navigation;

import com.qualcomm.ftccommon.configuration.EditServoControllerActivity;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.hardware.Drivetrain;
import org.firstinspires.ftc.teamcode.hardware.IMU;

public class Navigator
{
    private Drivetrain drivetrain;
    private Odometry odometry;
    private IMU imu;
    
    private double fwdTarget;
    private double angleTarget;
    private double fwdSpeed = 0; // Initialized to 0
    private double turnSpeed = 0; // Initialized to 0
    private double forwardKp = 0; // TODO load from config
    private double turnKp = 0; // TODO load from config
    
    public Navigator(Drivetrain drivetrain, Odometry odo)
    {
        this.drivetrain = drivetrain;
        this.odometry = odo;
        imu = odo.getIMU();
    }
    
    public void setForwardSpeed(double fwdSpeed)
    {
        this.fwdSpeed = fwdSpeed;
    }
    
    public void setTurnSpeed(double turnSpeed)
    {
        this.turnSpeed = turnSpeed;
    }
    
    public void update()
    {
        double l = odometry.past_l;
        double r = odometry.past_r;
        
        // Forward
        double fwdPos = (l + r) / 2;
        double fwdError = fwdTarget - fwdPos;
        double fwdPower = Range.clip(fwdError * forwardKp, -fwdSpeed, fwdSpeed);
        
        // Turn
        double heading = imu.getHeading();
        double turnError = angleTarget - heading;
        double turnPower = Range.clip(turnError * turnKp, -turnSpeed, turnSpeed);
        
        // Actually drive
        drivetrain.telemove(fwdPower, turnPower);
    }
    
    public void forward(double distance)
    {
        fwdTarget += distance;
    }
    
    public void forwardAbs(double pos)
    {
        fwdTarget = pos;
    }
    
    public void turn(double angle)
    {
        angleTarget += angle;
    }
    
    public void turnAbs(double angle)
    {
        angleTarget = angle;
    }
    
    public void goTo(double x, double y, double finHeading)
    {
        // TODO
    }
}
