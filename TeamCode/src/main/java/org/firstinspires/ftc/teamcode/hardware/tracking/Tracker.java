package org.firstinspires.ftc.teamcode.hardware.tracking;

import org.firstinspires.ftc.teamcode.hardware.IMU;
import org.firstinspires.ftc.teamcode.hardware.Turret;
import org.firstinspires.ftc.teamcode.hardware.navigation.Odometry;

public class Tracker {
    public Odometry odometry;
    public IMU imu;
    public Turret turret;
    public final double TURRET_CIRCUMFERENCE = 0;

    public Tracker(Odometry odometry, Turret turret, IMU imu){
        this.odometry = odometry;
        this.turret = turret;
        this.imu = imu;
    }

    public void updateVars(){
        double rotation_distance = (this.imu.getHeading() / 360.0) * TURRET_CIRCUMFERENCE;
        this.turret.rotate(rotation_distance);
    }
}
