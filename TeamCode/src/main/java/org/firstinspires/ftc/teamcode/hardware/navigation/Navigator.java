package org.firstinspires.ftc.teamcode.hardware.navigation;

import com.qualcomm.robotcore.util.Range;

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
    
    public Navigator(Drivetrain drivetrain, Odometry odo)
    {
        this.drivetrain = drivetrain;
        this.odometry = odo;
        imu = odo.getIMU();
        goTo = new EventFlow(ev);
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


        if (ev != null){
            if (turnError < 10 && turnError > 10){
                ev.pushEvent(new NavMoveEvent(NavMoveEvent.TURN_COMPLETE));
            }
            if (fwdError < 10 && fwdError > 10){
                ev.pushEvent(new NavMoveEvent(NavMoveEvent.FORWARD_COMPLETE));
            }
        }
        
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
        double targetHeading = Math.toDegrees(Math.atan2(y, x));
        if (targetHeading < 0) targetHeading = 360 - Math.abs(targetHeading);
        final double finalTargetHeading = targetHeading;
        goTo.start(new Subscriber<>(NavMoveEvent.class, (ev, bus, sub) -> {
            turnAbs(finalTargetHeading);
        }, "Direction Aim", NavMoveEvent.TURN_COMPLETE))
        .then(new Subscriber<>(NavMoveEvent.class, (ev, bus, sub) -> {
            forward(Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2)));
        }, "Move Forward", NavMoveEvent.FORWARD_COMPLETE))
        .then(new Subscriber<>(NavMoveEvent.class, (ev, bus, sub) -> {
            turnAbs(finHeading);
        }, "Final Turn", NavMoveEvent.TURN_COMPLETE));
    }

    private void connectEventBus(EventBus ev){
        this.ev = ev;
    }
}
