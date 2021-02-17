package org.firstinspires.ftc.teamcode.hardware.navigation;

import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Drivetrain;
import org.firstinspires.ftc.teamcode.hardware.IMU;
import org.firstinspires.ftc.teamcode.hardware.events.NavMoveEvent;
import org.firstinspires.ftc.teamcode.util.Time;
import org.firstinspires.ftc.teamcode.util.event.EventBus;

public class Navigator
{
    private Drivetrain drivetrain;
    private Odometry odometry;
    private IMU imu;
    private EventBus eventBus;
    
    private double fwdError;
    private double angleTarget;
    private double fwdSpeed = 0; // Initialized to 0
    private double turnSpeed = 0; // Initialized to 0
    public double forwardKp = 0; // TODO load from config
    public double turnKp = 0; // TODO load from config
    public double turnKi = 0; // TODO load from config
    
    private double lastDistance = Double.NaN;
    private double turnInt = 0;
    private double lastSample = 0;
    
    private double xTarget = -1;
    private double yTarget = -1;
    private boolean navigating = false;
    private boolean preTurnComplete = false;
    
    private boolean sendEvent_fwd = false;
    private boolean sendEvent_turn = false;
    
    private double fwdPower;
    private double turnPower;
    
    public Navigator(Drivetrain drivetrain, Odometry odo, EventBus eventBus)
    {
        this.drivetrain = drivetrain;
        this.odometry = odo;
        imu = odo.getIMU();
        
        forwardKp = 0.5;
        turnKp = 0.02;
        turnKi = 0.001;
        this.eventBus = eventBus;
    }
    
    public void setForwardSpeed(double fwdSpeed)
    {
        this.fwdSpeed = fwdSpeed;
    }
    
    public void setTurnSpeed(double turnSpeed)
    {
        this.turnSpeed = turnSpeed;
    }
    
    public void update(Telemetry telemetry)
    {
        /*
        double l = odometry.past_l;
        double r = odometry.past_r;
    
        double fwdPos = (l + r) / 2;
         */
        double heading = imu.getHeading();
        double elapsed = Time.since(lastSample);
        lastSample = Time.now();
        
        if (navigating)
        {
            angleTarget = Math.toDegrees(Math.atan2(yTarget - odometry.y, xTarget - odometry.x));
            angleTarget %= 360;
            if (angleTarget < 0) angleTarget += 360;
            angleTarget -= 180;
            // heading vector = <cos(heading), sin(heading)>
            // error vector   = <xTarget - x, yTarget - y>
            if (!preTurnComplete)
            {
                double angleError = angleTarget - heading;
                if (Math.abs(angleError) < 10) preTurnComplete = true;
            }
            else
            {
                double headingRad = Math.toRadians(heading);
                fwdError = (((xTarget - odometry.x) * Math.cos(headingRad)) + ((yTarget - odometry.y) * Math.sin(headingRad)));
    
                double distanceError = Math.hypot(xTarget - odometry.x, yTarget - odometry.y);
                if (distanceError < 0.5 && Math.abs(distanceError - lastDistance) < 0.01)
                {
                    navigating = false;
                    eventBus.pushEvent(new NavMoveEvent(NavMoveEvent.MOVE_COMPLETE));
                }
                if (distanceError < 5)
                {
                    angleTarget = heading;
                }
                lastDistance = distanceError;
                telemetry.addData("Distance error", "%.1f", distanceError);
                telemetry.addData("'Velocity'", "%.3f", (distanceError - lastDistance));
            }
        }
        else
        {
            fwdError = 0;
            preTurnComplete = false;
            lastDistance = Double.NaN;
        }
        
        // Forward
        double fwdPower = Range.clip(fwdError * forwardKp, -fwdSpeed, fwdSpeed);
        
        // Turn
        double turnError = angleTarget - heading;
        if (Math.abs(turnError) < 5)
        {
            turnInt += turnError * turnKi;
            turnInt = Range.clip(turnInt, -1, 1);
        }
        else
        {
            turnInt = 0;
        }
        double turnPower = Range.clip(turnError * turnKp + turnInt, -turnSpeed, turnSpeed);

        if (eventBus != null){
            if (Math.abs(turnError) < 0.1 && sendEvent_turn) {
                sendEvent_turn = false;
                eventBus.pushEvent(new NavMoveEvent(NavMoveEvent.TURN_COMPLETE));
            }
        }
        
        // Actually drive
        drivetrain.telemove(fwdPower, -turnPower);
        telemetry.addData("Nav Forward", "%.2f", fwdPower);
        telemetry.addData("Nav Turn", "%.2f", turnPower);
        telemetry.addData("Heading", "%.2f", heading);
        telemetry.addData("Target heading", "%.2f", angleTarget);
        telemetry.addData("Odometry X", "%.2f", odometry.x);
        telemetry.addData("Odometry Y", "%.2f", odometry.y);
    }
    
    public void turn(double angle)
    {
        angleTarget += angle;
        sendEvent_turn = true;
    }
    
    public void turnAbs(double angle)
    {
        angleTarget = angle;
        sendEvent_turn = true;
    }
    
    public void goTo(double x, double y)
    {
        xTarget = x;
        yTarget = y;
        navigating = true;
    }
    
    public double getTargetX()
    {
        return xTarget;
    }
    
    public double getTargetY()
    {
        return yTarget;
    }
    
    public double getTargetDistance()
    {
        return fwdError;
    }
    
    public double getTargetHeading()
    {
        return angleTarget;
    }
    
    public double getFwdPower()
    {
        return fwdPower;
    }
    
    public double getTurnPower()
    {
        return turnPower;
    }
    
    public boolean navigating()
    {
        return navigating;
    }

    public void connectEventBus(EventBus ev){
        this.eventBus = ev;
    }
}
