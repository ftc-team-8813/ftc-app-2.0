package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.teamcode.util.Status;

public class Drivetrain {
    private final DcMotor front_left;
    private final DcMotor front_right;
    private final DcMotor back_left;
    private final DcMotor back_right;
    private final Orientation angles;

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

    public Drivetrain(DcMotor front_left, DcMotor front_right, DcMotor back_left, DcMotor back_right, BNO055IMU imu) {
        this.front_left = front_left;
        this.front_right = front_right;
        this.back_left = back_left;
        this.back_right = back_right;
        this.angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);

        front_right.setDirection(DcMotorSimple.Direction.REVERSE);
        back_right.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    public void setStart(double y, double x, double heading) {
        this.target_y = y;
        this.target_x = x;
        this.target_heading = heading;
    }

    public void move(double forward, double strafe, double turn) {
        // Slowing right side to keep forward moving straight
        front_left.setPower((forward - strafe + turn) * 0.87);
        front_right.setPower(forward - strafe - turn);
        back_left.setPower((forward + strafe + turn) * 0.87);
        back_right.setPower(forward + strafe - turn);
    }

    public void headlessMove(double forward, double strafe, double turn) {
        double heading = angles.firstAngle;
        double factor = Math.sin(Math.abs(heading));
        double sign = Math.signum(heading);
        if (sign == 0) sign = 1;
        move(forward * factor * sign, strafe * (1 - factor) * sign, turn);
    }

    public void stop() {
        front_left.setPower(0);
        front_right.setPower(0);
        back_left.setPower(0);
        back_right.setPower(0);
    }

    // Only updates reached status in one loop cycle
    public boolean ifReached() {
        if (!reached && Math.abs(delta_y) < 2 && Math.abs(delta_x) < 2 && Math.abs(delta_heading) < 5) {
            reached = true;
            return true;
        }
        return false;
    }

    public enum encoderNames {FRONT_RIGHT, BACK_RIGHT, FRONT_LEFT, BACK_LEFT}

    public int getEncoderValue(encoderNames motor) {
        switch (motor) {
            case FRONT_RIGHT:
                return front_right.getCurrentPosition();
            case BACK_RIGHT:
                return back_right.getCurrentPosition();
            case FRONT_LEFT:
                return front_left.getCurrentPosition();
            case BACK_LEFT:
                return back_left.getCurrentPosition();
        }
    return 0;
    }
}