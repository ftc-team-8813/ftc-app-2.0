package org.firstinspires.ftc.teamcode.hardware.navigation;

import org.firstinspires.ftc.teamcode.hardware.IMU;
import org.firstinspires.ftc.teamcode.util.Scheduler;
import org.firstinspires.ftc.teamcode.util.event.EventBus;

public class OdoIMU extends IMU
{
    private Odometry odometry;
    private double headingOrig = 0;
    
    public OdoIMU(Odometry odometry)
    {
        super(null);
        this.odometry = odometry;
    }
    
    @Override
    public void initialize(EventBus evBus, Scheduler scheduler) { }
    
    @Override
    public int getStatus()
    {
        return STARTED;
    }
    
    @Override
    public String getStatusString()
    {
        return "started";
    }
    
    @Override
    public String getDetailStatus()
    {
        return "running w/odometry";
    }
    
    @Override
    public double getHeading()
    {
        return Math.toDegrees(odometry.calc_heading) - headingOrig;
    }
    
    @Override
    public double getRoll()
    {
        return 0;
    }
    
    @Override
    public double getPitch()
    {
        return 0;
    }
    
    @Override
    public void resetHeading()
    {
        headingOrig = getHeading();
    }
    
    @Override
    public void stop() { }
}
