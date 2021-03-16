package org.firstinspires.ftc.teamcode.hardware.autoshoot;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Drivetrain;
import org.firstinspires.ftc.teamcode.hardware.IMU;
import org.firstinspires.ftc.teamcode.hardware.Turret;
import org.firstinspires.ftc.teamcode.hardware.navigation.Odometry;
import org.firstinspires.ftc.teamcode.util.Logger;

public class Tracker {
    private Odometry odometry;
    private IMU imu;
    private Turret turret;
    
    private double x_target;
    private double y_target;
    private double heading_target;
    private Logger log;

    public Tracker(Turret turret, Drivetrain drivetrain){
        this.odometry = drivetrain.getOdometry();
        this.turret = turret;
        this.imu = odometry.getIMU();
        this.log = new Logger("Tracker");
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

    public double update(Telemetry telemetry){
        double x_dist = x_target - odometry.x;
        double y_dist = y_target - odometry.y;

        // calculate target heading
        double robot_heading = odometry.getIMU().getHeading();
        double turret_heading = robot_heading - Math.toDegrees(Math.atan(y_dist/x_dist));
        turret_heading %= 360;
        if (turret_heading < 0) turret_heading += 360;

        heading_target = turret_heading;

        double offset = (turret_heading / 360.0) * turret.getTurretFullRotation();
        double rotation_pos = turret.getTurretHome() + offset;
        turret.rotate(rotation_pos);
        return rotation_pos;
    }
    
    
    public double getTargetHeading(){
        return heading_target;
    }
}
