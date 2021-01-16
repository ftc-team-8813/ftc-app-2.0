package org.firstinspires.ftc.teamcode.hardware.navigation;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.hardware.IMU;

public class Odometry {
    public DcMotor top_left;
    public DcMotor top_right;
    public IMU imu;
    private double deltax;
    private double heading;
    private double target_pos;

    public Odometry(DcMotor top_left, DcMotor top_right, IMU imu){
        this.top_left = top_left;
        this.top_right = top_right;
        this.imu = imu;
    }

    public void updateDeltas(){
        this.deltax = top_left.getCurrentPosition()-top_right.getCurrentPosition();
        this.heading = imu.getHeading();
    }

    public void setTargetPos(double distance){
        final double TICKS = 537.6;
        final double CIRCUMFERENCE = 2.83 * Math.PI;
        double rotations = distance / CIRCUMFERENCE;
        double total_ticks = rotations * TICKS;
        this.target_pos = total_ticks;
    }

    public double getDeltas(){
        return this.deltax;
    }

    public double getHeading(){
        return this.heading;
    }
}
