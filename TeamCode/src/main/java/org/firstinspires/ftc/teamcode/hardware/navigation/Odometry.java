package org.firstinspires.ftc.teamcode.hardware.navigation;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.ServoImplEx;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.teamcode.util.Status;

public class Odometry {
    private final DcMotor l_enc;
    private final DcMotor r_enc;
    private final DcMotor s_enc;
    private final ServoImplEx left_drop;
    private final ServoImplEx right_drop;
    private BNO055IMU imu;

    private double y;
    private double x;
    private double heading;
    
    private double past_l;
    private double past_r;
    private double past_s;

    public Odometry(DcMotor l_enc, DcMotor r_enc, DcMotor s_enc, ServoImplEx left_drop, ServoImplEx right_drop, BNO055IMU imu){
        this.l_enc = l_enc;
        this.r_enc = r_enc;
        this.s_enc = s_enc;
        this.left_drop = left_drop;
        this.right_drop = right_drop;
        this.y = 0.0;
        this.x = 0.0;
        this.heading = 0.0;

        this.l_enc.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        this.r_enc.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        this.s_enc.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        this.l_enc.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        this.r_enc.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        this.s_enc.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit           = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit           = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.calibrationDataFile = "BNO055IMUCalibration.json";
        parameters.loggingEnabled      = true;
        parameters.loggingTag          = "IMU";
        parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();
        this.imu = imu;
        this.imu.initialize(parameters);
    }

    public void podsUp(){
        left_drop.setPosition(Status.UP_POS_LEFT);
        right_drop.setPosition(Status.UP_POS_RIGHT);
    }

    public void podsDown(){
        left_drop.setPosition(Status.DOWN_POS_LEFT);
        right_drop.setPosition(Status.DOWN_POS_RIGHT);
    }

    public void deltaPositionLeft(double position){
        left_drop.setPosition(left_drop.getPosition() + position);
    }

    public void deltaPositionRight(double position){
        right_drop.setPosition(right_drop.getPosition() + position);
    }

    public double[] getServoDropPositions(){
        return new double[]{left_drop.getPosition(), right_drop.getPosition()};
    }


    public void update(){
        /*
            Coordinate System:
                      x
                      |
                      |
                y --------- -y
                      |
                      |
                      -x
             Robot starts facing positive x
         */
        double[] poses = getCurrentPositions();

        double change_l = poses[0] - past_l;
        double change_r = poses[1] - past_r;
        double change_b = poses[2] - past_s;

        past_l = poses[0];
        past_r = poses[1];
        past_s = poses[2];

        double delta_f = (change_l + change_r) / 2;
        double delta_s = (change_l - change_r) / 2 + change_b;

        double vector = Math.sqrt(Math.pow(delta_f, 2) + Math.pow(delta_s, 2));
        double relative_heading = Math.atan2(delta_f, delta_s);
        double delta_y = Math.cos(heading * (Math.PI/180) + relative_heading) * vector;
        double delta_x = Math.sin(heading * (Math.PI/180) + relative_heading) * vector;

        y += convertToInches(delta_y);
        x += convertToInches(delta_x);
        Orientation angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        heading = angles.firstAngle;
    }

    public double convertToInches(double ticks){
        double revolutions = ticks / Status.ROTATIONAL_TICKS;
        return revolutions * Status.WHEEL_CIRCUMFERENCE;
    }

    public double[] getCurrentPositions(){
        return new double[]{-l_enc.getCurrentPosition(), r_enc.getCurrentPosition(), s_enc.getCurrentPosition()};
    }

    public double[] getOdoData(){
        return new double[]{y, x, heading};
    }
}
