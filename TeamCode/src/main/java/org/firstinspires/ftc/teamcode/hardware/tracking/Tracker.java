package org.firstinspires.ftc.teamcode.hardware.tracking;

import org.firstinspires.ftc.teamcode.hardware.Drivetrain;
import org.firstinspires.ftc.teamcode.hardware.IMU;
import org.firstinspires.ftc.teamcode.hardware.Turret;
import org.firstinspires.ftc.teamcode.hardware.navigation.Odometry;

public class Tracker {
    private Odometry odometry;
    private IMU imu;
    private Turret turret;
    
    private double x_target;
    private double y_target;
    private double heading_target;
    
    private double heading_offset;

    public Tracker(Turret turret, Drivetrain drivetrain, double heading_offset){
        this.odometry = drivetrain.getOdometry();
        this.turret = turret;
        this.imu = odometry.getIMU();
        this.heading_offset = heading_offset;
    }
    
    public void setTarget(double xTarget, double yTarget)
    {
        this.x_target = xTarget;
        this.y_target = yTarget;
    }
    
    public double getTargetX()
    {
        return x_target;
    }
    
    public double getTargetY()
    {
        return y_target;
    }

    public void update(){
        double x_dist = x_target - odometry.x;
        double y_dist = y_target - odometry.y;
        
        // calculate target heading
        double robot_heading = odometry.getIMU().getHeading();
        double turret_heading = -Math.toDegrees(Math.atan2(y_dist, x_dist)) + robot_heading - 180;
        turret_heading %= 360;
        if (turret_heading < 0) turret_heading += 360;
        
        heading_target = turret_heading;
        
        double rotation_distance = -turret_heading / 360.0;
        turret.rotate(turret.getTurretHome() + rotation_distance);
    }
    
    
    public double getTargetHeading(){
        return heading_target;
    }
}
