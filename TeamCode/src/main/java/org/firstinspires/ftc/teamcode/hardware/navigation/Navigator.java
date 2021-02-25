package org.firstinspires.ftc.teamcode.hardware.navigation;

import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Drivetrain;
import org.firstinspires.ftc.teamcode.hardware.IMU;
import org.firstinspires.ftc.teamcode.hardware.events.NavMoveEvent;
import org.firstinspires.ftc.teamcode.util.Time;
import org.firstinspires.ftc.teamcode.util.Vec2;
import org.firstinspires.ftc.teamcode.util.event.EventBus;
import org.firstinspires.ftc.teamcode.util.websocket.Server;

import java.nio.ByteBuffer;

import static java.lang.Math.PI;

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
    public double forwardKi = 0; // TODO load from config
    
    private double lastDistance = Double.NaN;
    private double turnInt = 0;
    private double fwdInt = 0;
    private double lastSample = 0;
    
    private Vec2 targetPos = new Vec2(0, 0);
    private boolean navigating = false;
    private boolean preTurnComplete = false;
    
    private boolean sendEvent_fwd = false;
    private boolean sendEvent_turn = false;
    
    private boolean backwards = false;
    
    private double fwdPower;
    private double turnPower;
    
    private double speedAdj;
    
    // 0: Odo X
    // 1: Odo Y
    // 2: Heading
    // 3: Target X
    // 4: Target Y
    // 5: Target heading
    // 6: Target distance
    // 7: Forward power
    // 8: Turn power
    // 9: Distance change
    private double[] serverBuffer = new double[10];
    private final Object serverLock = new Object();
    
    public Navigator(Drivetrain drivetrain, Odometry odo, EventBus eventBus)
    {
        this.drivetrain = drivetrain;
        this.odometry = odo;
        imu = odo.getIMU();
        
        forwardKp = 0.15;
        turnKp = 0.01;
        forwardKi = 0.001;
        turnKi = 0.001;
        this.eventBus = eventBus;
    }
    
    public void adjForVoltage(double voltage)
    {
        speedAdj = 0.15 * (12-voltage);
    }
    
    public void serve(Server server, int cmdid)
    {
        server.registerProcessor(cmdid, (cmd, payload, resp) -> {
             synchronized (serverLock)
             {
                 ByteBuffer buf = ByteBuffer.allocate(4 * serverBuffer.length);
                 for (double v : serverBuffer)
                 {
                    buf.putFloat((float)v);
                 }
                 buf.flip();
                 resp.respond(buf);
             }
        });
    }
    
    public void setForwardSpeed(double fwdSpeed)
    {
        this.fwdSpeed = fwdSpeed + speedAdj;
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
        double heading = Math.toRadians(imu.getHeading());
        double headingBackwards = heading + PI;
        double elapsed = Time.since(lastSample);
        lastSample = Time.now();
        
        double theta = heading;
        if (backwards) theta = headingBackwards;
        
        Vec2 position = new Vec2(odometry.x, odometry.y);
        
        double distanceError = 0;
        
        if (navigating)
        {
            if (targetPos == null)
            {
                navigating = false;
                return;
            }
            
            Vec2 errorVec = new Vec2(targetPos).sub(position);
            // rotate and then calculate angle in order to reduce likelihood of atan2 wraparound
            errorVec.rotate(-theta);
            angleTarget = errorVec.angle() + theta;
            
            if (!preTurnComplete)
            {
                double angleError = Math.toDegrees(angleTarget - theta);
                if (Math.abs(angleError) < 10) preTurnComplete = true;
            }
            else
            {
                Vec2 headingVec = Vec2.fromPolar(1, heading);
                Vec2 worldErrorVec = new Vec2(targetPos).sub(position);
                fwdError = worldErrorVec.dot(headingVec);
    
                distanceError = worldErrorVec.magnitude();
                if (distanceError < 1 && Math.abs(distanceError - lastDistance) < 0.01)
                {
                    navigating = false;
                    eventBus.pushEvent(new NavMoveEvent(NavMoveEvent.MOVE_COMPLETE));
                }
                if (distanceError < 4)
                {
                    angleTarget = theta;
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
        if (Math.abs(fwdError) < 10 && navigating)
        {
            fwdInt += fwdError * forwardKi;
            fwdInt = Range.clip(fwdInt, -1, 1);
        }
        else
        {
            fwdInt = 0;
        }
        double fwdPower = Range.clip(fwdError * forwardKp + fwdInt, -fwdSpeed, fwdSpeed);
        
        // Turn
        double turnError = Math.toDegrees(angleTarget - theta); // convert to degrees to keep kP and kI the same
        // if (backwards) turnError = -turnError;
        if (Math.abs(turnError) < 10)
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
            if (Math.abs(turnError) < 0.5 && sendEvent_turn) {
                sendEvent_turn = false;
                eventBus.pushEvent(new NavMoveEvent(NavMoveEvent.TURN_COMPLETE));
            }
        }
        
        // Actually drive
        drivetrain.telemove(fwdPower, -turnPower);
        telemetry.addData("Nav Forward", "%.2f", fwdPower);
        telemetry.addData("Nav Turn", "%.2f", turnPower);
        telemetry.addData("Nav FwdError", "%.2f", fwdError);
        telemetry.addData("Nav TurnError", "%.2f", turnError);
        telemetry.addData("Heading", "%.2f", heading);
        telemetry.addData("Target heading", "%.2f", angleTarget);
        telemetry.addData("Odometry X", "%.2f", odometry.x);
        telemetry.addData("Odometry Y", "%.2f", odometry.y);
    
        synchronized (serverLock)
        {
            serverBuffer[0] = odometry.x;
            serverBuffer[1] = odometry.y;
            serverBuffer[2] = theta;
            serverBuffer[3] = targetPos.x;
            serverBuffer[4] = targetPos.y;
            serverBuffer[5] = angleTarget;
            serverBuffer[6] = distanceError;
            serverBuffer[7] = fwdPower;
            serverBuffer[8] = turnPower;
            serverBuffer[9] = distanceError - lastDistance;
        }
    }
    
    public void turn(double angle)
    {
        angleTarget += angle;
        sendEvent_turn = true;
    }
    
    public void turnAbs(double angle)
    {
        angleTarget = angle;
        if (backwards) angleTarget = angle + PI;
        sendEvent_turn = true;
    }
    
    public void goTo(double x, double y)
    {
        goTo(x, y, false);
    }
    
    public void goTo(double x, double y, boolean backwards)
    {
        targetPos = new Vec2(x, y);
        this.backwards = backwards;
        navigating = true;
    }
    
    public double getTargetX()
    {
        return targetPos.x;
    }
    
    public double getTargetY()
    {
        return targetPos.y;
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
