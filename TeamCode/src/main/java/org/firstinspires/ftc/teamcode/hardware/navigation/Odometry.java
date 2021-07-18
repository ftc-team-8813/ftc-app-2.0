package org.firstinspires.ftc.teamcode.hardware.navigation;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.util.Logger;

public class Odometry {
    private final DcMotor l_enc;
    private final DcMotor r_enc;
    private final DcMotor f_enc;

    public double forward;
    public double side;
    public double heading;
    
    private double past_l;
    private double past_r;
    private double past_b;

    private final double ROTATIONAL_TICKS = 8192;
    private final double WHEEL_RADIUS = 1.377953;
    private final double ROBOT_RADIUS_SIDE = 8.5;
    private final double ROBOT_RADIUS_FRONT = 5.5;

    public Odometry(DcMotor l_enc, DcMotor r_enc, DcMotor f_enc){
        this.l_enc = l_enc;
        this.r_enc = r_enc;
        this.f_enc = f_enc;

        l_enc.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        r_enc.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        f_enc.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        l_enc.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        r_enc.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        f_enc.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void update(){
        double[] poses = getCurrentPositions();

        double change_l = poses[0] - past_l;
        double change_r = poses[1] - past_r;
        double change_b = poses[2] - past_b;

        past_l = poses[0];
        past_r = poses[1];
        past_b = poses[2];

        double pod_scalar = ROBOT_RADIUS_SIDE / ROBOT_RADIUS_FRONT;
        double delta_f = (change_l + change_r) / 2;
        double delta_s = (change_l - change_r) / 2 + (change_b * pod_scalar);
        double delta_theta = (change_r - change_l) / 2;

        forward += convertToInches(delta_f);
        side += convertToInches(delta_s);
        heading += convertToRadians(delta_theta);
    }

    public double convertToInches(double ticks){
        double revolutions = ticks / ROTATIONAL_TICKS;
        double delta_inches = revolutions * (2 * Math.PI * WHEEL_RADIUS);
        return delta_inches;
    }

    public double convertToRadians(double ticks){
        double ticks_per_inch = ROTATIONAL_TICKS / (2 * Math.PI * WHEEL_RADIUS);
        double delta_inches = ticks / ticks_per_inch;
        double revolutions = delta_inches / (2 * Math.PI * ROBOT_RADIUS_SIDE);
        double delta_radians = revolutions * (2 * Math.PI);
        return delta_radians;
    }

    public double[] getCurrentPositions(){
        return new double[]{l_enc.getCurrentPosition(), -r_enc.getCurrentPosition(), f_enc.getCurrentPosition()};
    }
}
