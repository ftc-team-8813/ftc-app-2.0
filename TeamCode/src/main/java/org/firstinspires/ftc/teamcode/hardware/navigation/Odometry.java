package org.firstinspires.ftc.teamcode.hardware.navigation;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.util.Logger;
import org.firstinspires.ftc.teamcode.util.Status;

public class Odometry {
    private final DcMotor l_enc;
    private final DcMotor r_enc;
    private final DcMotor f_enc;
    private final Servo left_drop;
    private final Servo right_drop;

    public double x;
    public double y;
    public double heading;
    
    private double past_l;
    private double past_r;
    private double past_b;


    public Odometry(DcMotor l_enc, DcMotor r_enc, DcMotor f_enc, Servo left_drop, Servo right_drop){
        this.l_enc = l_enc;
        this.r_enc = r_enc;
        this.f_enc = f_enc;
        this.left_drop = left_drop;
        this.right_drop = right_drop;
        this.x = 0;
        this.y = 0;

        l_enc.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        r_enc.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        f_enc.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        l_enc.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        r_enc.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        f_enc.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }


    public void release(){
        left_drop.setPosition(Status.RELEASE_POS_1);
        right_drop.setPosition(Status.RELEASE_POS_2);
    }


    public void update(){
        double[] poses = getCurrentPositions();

        double change_l = poses[0] - past_l;
        double change_r = poses[1] - past_r;
        double change_b = poses[2] - past_b;

        past_l = poses[0];
        past_r = poses[1];
        past_b = poses[2];

        // double pod_scalar = ROBOT_RADIUS_SIDE / ROBOT_RADIUS_FRONT;
        double pod_scalar = 1;
        double delta_f = (change_l + change_r) / 2;
        double delta_s = (change_l - change_r) / 2 + (change_b * pod_scalar);
        double delta_theta = (change_r - change_l) / 2;

        double vector = Math.sqrt(Math.pow(delta_f, 2) + Math.pow(delta_s, 2));
        double relative_heading = Math.atan2(delta_f, delta_s);
        double delta_x = Math.sin(heading + relative_heading) * vector;
        double delta_y = Math.cos(heading + relative_heading) * vector;

        x += convertToInches(delta_x);
        y += convertToInches(delta_y);
        heading += convertToRadians(delta_theta);
    }


    public double convertToInches(double ticks){
        double revolutions = ticks / Status.ROTATIONAL_TICKS;
        double delta_inches = revolutions * (Math.PI * Status.WHEEL_DIAMETER);
        return delta_inches;
    }


    public double convertToRadians(double ticks){
        double ticks_per_inch = Status.ROTATIONAL_TICKS / (Math.PI * Status.WHEEL_DIAMETER);
        double delta_inches = ticks / ticks_per_inch;
        double revolutions = delta_inches / (2 * Math.PI * Status.ROBOT_RADIUS_SIDE);
        double delta_radians = revolutions * (2 * Math.PI);
        return delta_radians;
    }


    public double[] getCurrentPositions(){
        return new double[]{l_enc.getCurrentPosition(), -r_enc.getCurrentPosition(), f_enc.getCurrentPosition()};
    }
}
