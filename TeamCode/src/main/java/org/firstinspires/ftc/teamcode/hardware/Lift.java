package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

public class Lift {

    // (0,115) xy cord of starting point positive is direction with the rev hubs
    // (400,820) High Goal Position
    //xy is millimeters
    // Arm1 is 488.89580 mm
    // Arm2 is 424.15230 mm

    private final DcMotor arm_lower;
    private final DcMotor arm_upper;
    private final DcMotor wrist;

    private final double ARM_LOWER_LENGTH = 488.89580;
    private final double ARM_UPPER_LENGTH = 424.15230;

    private final double DEGREES_PER_TICK = 0.0279;

    private double x = 0;
    private double y = 115;


    public Lift(DcMotor arm_lower, DcMotor arm_upper, DcMotor wrist) {
        this.arm_lower = arm_lower;
        this.arm_upper = arm_upper;
        this.wrist = wrist;
    }

    public void setCoordinates(double set_x, double set_y) {
        x = set_x;
        y = set_y;
    }


    public void setLiftPower(double arm_low_pow, double arm_up_pow, double wrist_pow) {
        arm_lower.setPower(arm_low_pow);
        arm_upper.setPower(arm_up_pow);
        wrist.setPower(wrist_pow);
    }


    public double[] getEncoderValue() {
        double[] a = new double[3];

        a[0] = arm_lower.getCurrentPosition();
        a[1] = arm_upper.getCurrentPosition();
        a[2] = wrist.getCurrentPosition();

        return a;
    }

    public void resetLiftEncoder() {
        arm_lower.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        arm_upper.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        wrist.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        arm_lower.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        arm_upper.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        wrist.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public double[] get_POI(double l1, double l2, double a, double b) {
        double[] cords = new double[4];

        double dist = Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2));
        if ((dist > l1 + l2) || (dist < Math.abs(l1 - l2))) {
            cords[0] = 0;
            cords[1] = 0;
            cords[2] = 0;
            cords[3] = 0;
            return cords;
        }
        if (b == 0) {
            double x = (Math.pow(a, 2) + Math.pow(l1, 2) - Math.pow(l2, 2)) / (2 * a);
            double y = Math.sqrt(Math.pow(l1, 2) - Math.pow(x, 2));
            cords[0] = x;
            cords[1] = y;
            cords[2] = x;
            cords[3] = -y;
            return cords;
        } else {
            double L = (Math.pow(a, 2) + Math.pow(b, 2) + Math.pow(l1, 2) - Math.pow(l2, 2)) / (2 * b);
            double A = (1 + (Math.pow(a, 2) / Math.pow(b, 2)));
            double B = (-2 * a * L) / b;
            double C = Math.pow(L, 2) - Math.pow(l1, 2);
            double D = Math.sqrt(Math.pow(B, 2) - (4 * A * C));
            double x1 = (-B + D) / (2 * A);
            double x2 = (-B - D) / (2 * A);
            double y1 = (-a / b) * x1 + L;
            double y2 = (-a / b) * x2 + L;
            cords[0] = x1;
            cords[1] = y1;
            cords[2] = x2;
            cords[3] = y2;
            return cords;
        }
    }

    public double get_qangle(double x, double y) {
        double deg = Math.toDegrees(Math.atan(Math.abs(y) / Math.abs(x)));
        if (x < 0) {
            deg += 2 * (90 - deg);
        }
        if (y < 0) {
            deg *= -1;
        }
        return deg;
    }

    public double get_min_diff(double a, double b) {
        double q = Math.abs(get_pos_angle(a) - get_pos_angle(b));
        double w = Math.abs(a - b);
        return Math.min(q, w);
    }


    public double[] get_ang(double l1, double l2, double a, double b, double cural, double curbe) {
        double[] cords = new double[2];
        if (a == 0 && b == 0) {
            cords[0] = cural;
            cords[1] = -(180 - cural);
            return cords;
        }
        double[] points = get_POI(l1, l2, a, b);

        double x1 = points[0];
        double y1 = points[1];
        double x2 = points[2];
        double y2 = points[3];

        double a1 = get_qangle(x1, y1);
        double b1 = get_qangle(a - x1, b - y1);

         if ((Math.abs(a1 - b1) > 180)) { // for arms not intersecting
             b1 = b1 + 360;
         }

        double a2 = get_qangle(x2, y2);
        double b2 = get_qangle(a - x2, b - y2);

         if ((Math.abs(-a2 + b2) > 180)) { // for arms not intersecting
             b2 = b2 - 360;
         }

        double diff1 = get_min_diff(cural, a1) + get_min_diff(curbe, b1);
        double diff2 = get_min_diff(cural, a2) + get_min_diff(curbe, b2);


        if ((diff1 < diff2)) {
            cords[0] = a1;
            cords[1] = b1;
            return cords;
        } else {
            cords[0] = a2;
            cords[1] = b2;
            return cords;
        }
    }


    public double get_pos_angle(double angle) {
        if (angle < 0) {
            return 360 + angle;
        } else {
            return angle;
        }
    }




}
