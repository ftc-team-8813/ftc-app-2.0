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

    private double target_y = 0;
    private double target_x = 0;
    private double target_heading = 0;
    private double target_speed;

    private double y_integral;
    private double x_integral;
    private double heading_integral;


    private double delta_y;
    private double delta_x;
    private double delta_heading;

    public boolean reached = true;
    public boolean turned = true;

    public Drivetrain(Odometry odometry, DcMotor front_left, DcMotor front_right, DcMotor back_left, DcMotor back_right){
        this.odometry = odometry;
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
        // Reversed because of bevel gears
        front_left.setPower(-1 * (forward + strafe + turn));
        front_right.setPower(-1 * (forward - strafe - turn));
        back_left.setPower(-1 * (forward - strafe + turn));
        back_right.setPower(-1 * (forward + strafe - turn));
    }

    // Only updates reached status in one loop cycle
    public boolean ifReachedPosition(){
        if (!reached && Math.abs(delta_y) < 2 && Math.abs(delta_x) < 2){
            reached = true;
            return true;
        }
        return false;
    }

    public boolean ifReachedHeading(){
        if (!turned && Math.abs(delta_heading) < 4){
            turned = true;
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

    public void updatePosition(){
        double[] odoData = odometry.getOdoData();
        delta_y = odoData[0] - target_y; // Flipped to change power direction
        delta_x = target_x - odoData[1];

        y_integral += delta_y;
        x_integral += delta_x;

        double forward_power = ((x_integral * Status.FORWARD_KI) + (delta_x * Status.FORWARD_KP)) * target_speed;
        double strafe_power = ((y_integral * Status.STRAFE_KI) + (delta_y * Status.STRAFE_KP)) * target_speed;

        teleMove(forward_power, strafe_power, 0);
    }

    public void updateHeading(){
        double[] odoData = odometry.getOdoData();
        delta_heading = target_heading + odoData[2]; // Adding to flip rotation direction
        heading_integral += delta_heading;

        double turn_power = ((heading_integral * Status.TURN_KI) + (delta_heading * Status.TURN_KP)) * target_speed;
        teleMove(0, 0, turn_power);

    }

    public double[] getPositionDeltas(){
        return new double[]{delta_y, delta_x, delta_heading};
    }

    public double[] getTargets(){
        return new double[]{target_y, target_x, target_heading};
    }
}
