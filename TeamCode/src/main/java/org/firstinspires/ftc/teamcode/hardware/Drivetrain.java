package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.hardware.navigation.Odometry;

public class Drivetrain {
    private Odometry odometry;
    private DcMotor front_left;
    private DcMotor front_right;
    private DcMotor back_left;
    private DcMotor back_right;

    private double target_x;
    private double target_y;
    private double target_heading;

    public Drivetrain(Odometry odometry, DcMotor front_left, DcMotor front_right, DcMotor back_left, DcMotor back_right){
        this.odometry = odometry;
        this.front_left = front_left;
        this.front_right = front_right;
        this.back_left = back_left;
        this.back_right = back_right;

        front_right.setDirection(DcMotorSimple.Direction.REVERSE);
        back_right.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    public void teleMove(double forward, double strafe, double turn){
        // Reversed because of bevel gears
        front_left.setPower(-1 * (forward + strafe + turn));
        front_right.setPower(-1 * (forward - strafe - turn));
        back_left.setPower(-1 * (forward - strafe + turn));
        back_right.setPower(-1 * (forward + strafe - turn));
    }

    public void goToPosition(double x, double y, double heading){
        target_x = x;
        target_y = y;
        target_heading = heading;
    }

//    public void updatePosition(){
//        double[] odoData = odometry.getOdoData();
//        double delta_x = target_x - odoData[0];
//        double delta_y = target_y - odoData[1];
//        double delta_heading = target_heading - odoData[2];
//
//        delta_x *
//    }
}
