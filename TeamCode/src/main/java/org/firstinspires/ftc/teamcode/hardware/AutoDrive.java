package org.firstinspires.ftc.teamcode.hardware;
import java.lang.Math;
public class AutoDrive {
    private double fl_enc;
    private double fr_enc;
    private double bl_enc;
    private double br_enc;

    private double fl_enc_was;
    private double fr_enc_was;
    private double bl_enc_was;
    private double br_enc_was;

    private double field_x;
    private double field_y;
    private double field_a;

    private double target_x;
    private double target_y;
    private double target_a;

    private double strafe_efficiency;
    private double turn_factor;

    public void setTarget(double target_x, double target_y, double target_a){

    }
    public void update(Drivetrain drivetrain){
        double delta_x;
        double delta_y;
        double delta_a;

        fl_enc = drivetrain.getEncoderValue(Drivetrain.encoderNames.FRONT_LEFT);
        fr_enc = drivetrain.getEncoderValue(Drivetrain.encoderNames.FRONT_RIGHT);
        bl_enc = drivetrain.getEncoderValue(Drivetrain.encoderNames.BACK_LEFT);
        br_enc = drivetrain.getEncoderValue(Drivetrain.encoderNames.BACK_RIGHT);

        double delta_fl = fl_enc-fl_enc_was;
        double delta_fr = fr_enc-fr_enc_was;
        double delta_bl = bl_enc-bl_enc_was;
        double delta_br = br_enc-br_enc_was;

        delta_x = (delta_fl+delta_fr)/2;
        delta_y = strafe_efficiency * (delta_bl-delta_fl) / 2;
        delta_a = turn_factor * (delta_fl-delta_br) / 2;

        if (delta_a == 0) // Travelling in a straight line
        {
            field_x = Math.sqrt(Math.pow(delta_x, 2) + Math.pow(delta_y, 2)) * Math.sin(field_a);
            field_y = Math.sqrt(Math.pow(delta_x, 2) + Math.pow(delta_y, 2)) * Math.cos(field_a);
        }
        else {

        }

        field_x =

        field_a += delta_a;

    }
    public void checkPosition(){

    }
}
