package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.hardware.navigation.Odometry;
import org.firstinspires.ftc.teamcode.util.Status;

public class Drivetrain {
    private final Odometry odometry;
    private final DcMotor front_left;
    private final DcMotor front_right;
    private final DcMotor back_left;
    private final DcMotor back_right;

    private double target_x;
    private double target_y;
    private double target_heading;
    private double target_speed;

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

    public void goToPosition(double y, double x, double heading, double speed){
        target_y = y;
        target_x = x;
        target_heading = heading;
        target_speed = speed;
    }

    public void goToHeading(double heading){
        target_heading = heading;
    }

    public boolean updatePosition(){
        double[] odoData = odometry.getOdoData();
        double delta_y = target_y + odoData[0]; // Adding to flip y-axis
        double delta_x = target_x - odoData[1];
        double delta_heading = target_heading + odoData[2]; // Adding to flip rotation direction

        double forward_power = delta_x * Status.FORWARD_KP * target_speed;
        double strafe_power = delta_y * Status.STRAFE_KP * target_speed;
        double turn_power = delta_heading * Status.TURN_KP * target_speed;

        teleMove(forward_power, strafe_power, turn_power);
        return delta_y < 1.5 && delta_x < 1.5 && delta_heading < 2;
    }
}
