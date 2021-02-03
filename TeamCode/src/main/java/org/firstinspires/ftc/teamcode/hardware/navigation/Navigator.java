package org.firstinspires.ftc.teamcode.hardware.navigation;

import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Drivetrain;
import org.firstinspires.ftc.teamcode.hardware.IMU;
import org.firstinspires.ftc.teamcode.hardware.events.NavMoveEvent;
import org.firstinspires.ftc.teamcode.util.event.EventBus;
import org.firstinspires.ftc.teamcode.util.event.EventBus.Subscriber;
import org.firstinspires.ftc.teamcode.util.event.EventFlow;

public class Navigator
{
    private Drivetrain drivetrain;
    private Odometry odometry;
    private IMU imu;
    private EventBus ev;

    private EventFlow goTo;
    
    private double fwdTarget;
    private double angleTarget;
    private double fwdSpeed = 0; // Initialized to 0
    private double turnSpeed = 0; // Initialized to 0
    private double forwardKp = 0; // TODO load from config
    private double turnKp = 0; // TODO load from config
    
    private double xTarget = -1;
    private double yTarget = -1;
    private boolean navigating = false;
    
    private boolean sendEvent_fwd = false;
    private boolean sendEvent_turn = false;
    
    public Navigator(Drivetrain drivetrain, Odometry odo)
    {
        this.drivetrain = drivetrain;
        this.odometry = odo;
        imu = odo.getIMU();
        goTo = new EventFlow(ev);
        
        forwardKp = 0.01;
        turnKp = 0.01;
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
        double fwdError = 0;
        double heading = imu.getHeading();
        
        if (navigating)
        {
            angleTarget = Math.toDegrees(Math.atan2(yTarget - odometry.y, xTarget - odometry.x));
            // heading vector = <cos(heading), sin(heading)>
            // error vector   = <xTarget - x, yTarget - y>
            double headingRad = Math.toRadians(heading);
            fwdError = -(((xTarget - odometry.x) * Math.cos(headingRad)) + ((yTarget - odometry.y) * Math.sin(headingRad)));
            
            double distanceError = Math.hypot(xTarget - odometry.x, yTarget - odometry.y);
            if (distanceError < 0.5)
            {
                navigating = false;
                ev.pushEvent(new NavMoveEvent(NavMoveEvent.NAVIGATION_COMPLETE));
            }
            telemetry.addData("Distance error", "%.1f", distanceError);
        }
        
        // Forward
        double fwdPower = Range.clip(fwdError * forwardKp, -fwdSpeed, fwdSpeed);
        
        // Turn
        double turnError = angleTarget - heading;
        double turnPower = Range.clip(turnError * turnKp, -turnSpeed, turnSpeed);


        if (ev != null){
            if (Math.abs(turnError) < 0.1 & sendEvent_turn) {
                sendEvent_turn = false;
                ev.pushEvent(new NavMoveEvent(NavMoveEvent.TURN_COMPLETE));
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
    }
    
    public void turnAbs(double angle)
    {
        angleTarget = angle;
    }
    
    public void goTo(double x, double y)
    {
        xTarget = x;
        yTarget = y;
        navigating = true;
    }

    public void connectEventBus(EventBus ev){
        this.ev = ev;
    }
}
