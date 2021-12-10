package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.util.Status;

public class Drivetrain {
    private final DcMotor front_left;
    private final DcMotor front_right;
    private final DcMotor back_left;
    private final DcMotor back_right;

    private double target_y = 0;
    private double target_x = 0;
    private double target_heading = 0;
    private double target_speed;

    private double forward_integral;
    private double strafe_integral;
    private double heading_integral;


    private double delta_y;
    private double delta_x;
    private double delta_heading;

    public boolean reached = true;
    public boolean turned = true;

    public Drivetrain(DcMotor front_left, DcMotor front_right, DcMotor back_left, DcMotor back_right){
        this.front_left = front_left;
        this.front_right = front_right;
        this.back_left = back_left;
        this.back_right = back_right;

        front_right.setDirection(DcMotorSimple.Direction.REVERSE);
        back_right.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    public void setStart(double y, double x, double heading){
        this.target_y = y;
        this.target_x = x;
        this.target_heading = heading;
    }

    public void teleMove(double forward, double strafe, double turn){
        // Slowing right side to keep forward moving straight
        front_left.setPower((forward - strafe + turn) * 0.87);
        front_right.setPower(forward - strafe - turn);
        back_left.setPower((forward + strafe + turn) * 0.87);
        back_right.setPower(forward + strafe - turn);
    }

    public void stop(){
        front_left.setPower(0);
        front_right.setPower(0);
        back_left.setPower(0);
        back_right.setPower(0);
    }

    // Only updates reached status in one loop cycle
    public boolean ifReached(){
        if (!reached && Math.abs(delta_y) < 2 && Math.abs(delta_x) < 2 && Math.abs(delta_heading) < 5){
            reached = true;
            return true;
        }
        return false;
    }

    public void goToPosition(double y, double x, double speed){
        target_y = y;
        target_x = x;
        target_speed = speed;
        reached = false;
    }

    public void goToHeading(double heading, double speed){
        target_heading = heading;
        target_speed = speed;
        turned = false;
    }

    public void update(){
        double[] odo_data = new double[]{0.0, 0.0, 0.0};
        double heading = odo_data[2];
        delta_y = odo_data[0] - target_y; // Flipped to change power direction
        delta_x = target_x - odo_data[1];
        delta_heading = target_heading + odo_data[2]; // Adding to flip rotation direction

        double relative_heading = Math.atan2(delta_y, delta_x) * (180/Math.PI) + heading;
        double vector = Math.sqrt(Math.pow(delta_y, 2) + Math.pow(delta_x, 2));
        double strafe_distance = Math.sin(relative_heading * (Math.PI/180)) * vector;
        double forward_distance = Math.cos(relative_heading * (Math.PI/180)) * vector;

        forward_integral += forward_distance;
        strafe_integral += strafe_distance;
        heading_integral += delta_heading;

        double forward_power = (forward_distance * Status.FORWARD_KP) + (forward_integral * Status.FORWARD_KI) * target_speed;
        double strafe_power = (strafe_distance * Status.STRAFE_KP) + (strafe_integral * Status.STRAFE_KI) * target_speed;
        double turn_power = ((heading_integral * Status.TURN_KI) + (delta_heading * Status.TURN_KP)) * target_speed;

        teleMove(forward_power, strafe_power, turn_power);
    }

    public double[] getPositionDeltas(){
        return new double[]{delta_y, delta_x, delta_heading};
    }

    public double[] getTargets(){
        return new double[]{target_y, target_x, target_heading};
    }
}
