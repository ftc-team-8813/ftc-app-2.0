package org.firstinspires.ftc.teamcode.hardware;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.opmodes.teleop.ControlMgr;
import org.firstinspires.ftc.teamcode.opmodes.teleop.ControlModule;
import org.firstinspires.ftc.teamcode.util.Status;
import org.firstinspires.ftc.teamcode.util.event.EventBus;
import org.firstinspires.ftc.teamcode.util.websocket.InetSocketServer;
import org.firstinspires.ftc.teamcode.util.websocket.Server;

import java.io.IOException;
import java.lang.Math;
import java.nio.ByteBuffer;

public class AutoDrive {
    private Drivetrain drivetrain;
    private IMU imu;

    private boolean drivetrain_reached;

    private double delta_x; // change in robot x-position (forward/backward)
    private double delta_y; // change in robot y-position (strafing)
    private double delta_a; // change in robot heading (turning)


    private double fl_enc;
    private double fr_enc;
    private double bl_enc;
    private double br_enc;
    private double heading;

    private double fl_enc_was;
    private double fr_enc_was;
    private double bl_enc_was;
    private double br_enc_was;
    private double heading_was;

    private double delta_field_x = 0.0;
    private double delta_field_y = 0.0;

    private double field_x = 0.0;
    private double field_y = 0.0;

    private double target_x;
    private double target_y;
    private double target_a;

    private double error_x;
    private double error_y;
    private double error_a;

    private final double strafe_efficiency = 0.9014;
    private final double radians_per_tick = (10 * Math.PI) / (10246); // 10246 was the tick count after we spun the robot 2pi radians 10 times
    private final double inches_per_tick = (1 / Status.TICKS_PER_ROTATION) * (96 * Math.PI / 25.4) * (1 / 15.2);
    private double loop_start = 0.0;
    private double loop_end = 0.0;
    private double loop_time = 0.0;

    public AutoDrive(Drivetrain drivetrain, IMU imu){
        this.drivetrain = drivetrain;
        this.imu = imu;
    }

    public boolean ifReached(){
        double min_x = target_x - 0.5;
        double max_x = target_x - 0.5;
        double min_y = target_y - 0.5;
        double max_y = target_y - 0.5;
        if (!drivetrain_reached && min_x < field_x && field_x < max_x && min_y < field_y && field_y < max_y){
            drivetrain_reached = true;
            return true;
        }
        return false;
    }

    public void getFieldPos() {
        loop_start = System.nanoTime() / 1000000000.0;
        heading = Math.toRadians(imu.getHeading());

        double error;
        double direction;// direction of robot motion

        fl_enc = drivetrain.getEncoderValue(Drivetrain.encoderNames.FRONT_LEFT);
        fr_enc = drivetrain.getEncoderValue(Drivetrain.encoderNames.FRONT_RIGHT);
        bl_enc = drivetrain.getEncoderValue(Drivetrain.encoderNames.BACK_LEFT);
        br_enc = drivetrain.getEncoderValue(Drivetrain.encoderNames.BACK_RIGHT);

        double delta_fl = fl_enc - fl_enc_was;
        double delta_fr = fr_enc - fr_enc_was;
        double delta_bl = bl_enc - bl_enc_was;
        double delta_br = br_enc - br_enc_was;

        // converts encoder values to changes in robot x, y, and a
        delta_x = inches_per_tick * (delta_fl + delta_fr) / 2;
        delta_y = inches_per_tick * strafe_efficiency * (delta_bl - delta_fl) / 2;
        //delta_a = -radians_per_tick * (delta_fr-delta_bl) / 2; // heading based on wheel encoders
        delta_a = heading - heading_was;

        double s = Math.sqrt((delta_x * delta_x) + (delta_y * delta_y)); // arc length, or distance travelled

        // Straight lines method
        direction = heading + Math.atan2(delta_x, delta_y); // arctangent of y/x
        delta_field_x = s * Math.cos(direction);
        delta_field_y = s * Math.sin(direction);

        fl_enc_was = fl_enc;
        fr_enc_was = fr_enc;
        bl_enc_was = bl_enc;
        br_enc_was = br_enc;
        heading_was = heading;

        field_x += delta_field_x;
        field_y += delta_field_y;

        loop_end = System.nanoTime() / 1000000000.0;
        loop_time = loop_end - loop_start;

    }

    public void moveToPosition(double x, double y, double a) {
        final double power_cap = 0.1; // NOT ACTUALLY CAPPING THE MOTOR POWER! Caps the values of fwd, strafe, and turn

        target_x = x;
        target_y = y;
        target_a = a;
        final double K = 0.01;
        final double Kturn = 0.05;

        error_x = target_x - field_x;
        error_y = target_y - field_y;
        error_a = target_a - heading;
        double error_d = Math.sqrt((error_x * error_x) + (error_y * error_y)); // distance between target position and actual position
        double theta = Math.atan2(error_x, error_y) - heading; // angle between direction of motion and heading

        double forward = Range.clip(error_d * Math.cos(theta) * K, -power_cap, power_cap);
        double strafe = -Range.clip(error_d * Math.sin(theta) * K, -power_cap, power_cap);
        double turn = -Range.clip(error_a * Kturn, -power_cap, power_cap);

        drivetrain.move(forward, strafe, turn);
        drivetrain_reached = false;
    }

    public void update(Telemetry telemetry) {

        getFieldPos();
        /*
        telemetry.addData("encoder FL: ", fl_enc);
        telemetry.addData("encoder FR: ", fr_enc);
        telemetry.addData("encoder BL: ", bl_enc);
        telemetry.addData("encoder BR: ", br_enc);
         */
        telemetry.addData("x Error: ", error_x);
        telemetry.addData("y Error: ", error_y);
        telemetry.addData("a Error: ", error_a);

        //telemetry.addData("delta x: ", delta_x);
        //telemetry.addData("delta y: ", delta_y);

        telemetry.addData("x position: ", field_x);
        telemetry.addData("y position: ", field_y);
        //telemetry.addData("center_Y", center_y);
        telemetry.addData("angle: ", heading);
        //telemetry.addData("r = ", r);
        //telemetry.addData("Server status ", server.getStatus());
        telemetry.addData("Loop Time in seconds", loop_time);

    }
}

