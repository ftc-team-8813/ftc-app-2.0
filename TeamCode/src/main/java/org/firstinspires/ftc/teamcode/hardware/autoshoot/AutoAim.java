package org.firstinspires.ftc.teamcode.hardware.autoshoot;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Drivetrain;
import org.firstinspires.ftc.teamcode.hardware.IMU;
import org.firstinspires.ftc.teamcode.hardware.Turret;
import org.firstinspires.ftc.teamcode.hardware.navigation.Odometry;
import org.firstinspires.ftc.teamcode.util.Logger;

public class AutoAim
{
    private Odometry odometry;
    private IMU imu;
    private double turretHome;
    
    private double x_target;
    private double y_target;
    private Logger log;

    public AutoAim(Odometry odometry, double turretHome){
        this.odometry = odometry;
        this.imu = odometry.getIMU();
        this.turretHome = turretHome;
        this.log = new Logger("Auto Aim");
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

    public double getTurretRotation(Telemetry telemetry){
        double x_dist = x_target - odometry.x;
        double y_dist = y_target - odometry.y;

        // calculate target heading
        // CCW for imu is positive
        double robot_heading = imu.getHeading();
        double field_heading = Math.toDegrees(Math.atan2(y_dist, x_dist));
        
        double turret_heading = field_heading - robot_heading + 180;
        double rotation = turret_heading / 360.0;
        double rotation_pos = turretHome + rotation;
        
        // wrap to between 0 and 1
        rotation_pos %= 1; // -1 to 1
        if (rotation_pos < 0) rotation_pos += 1; // 0 to 1

        telemetry.addData("Tracker Target Heading: ", turret_heading);
        telemetry.addData("Tracker Target Position: ", rotation_pos);
        
        return rotation_pos;
    }
}