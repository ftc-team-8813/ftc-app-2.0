package org.firstinspires.ftc.teamcode.hardware;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.opmodes.teleop.ControlMgr;
import org.firstinspires.ftc.teamcode.opmodes.teleop.ControlModule;
import org.firstinspires.ftc.teamcode.util.Status;

import java.lang.Math;

public class AutoDrive extends ControlModule {

    public AutoDrive() { super("Auto Drive"); }
    private double fl_enc;
    private double fr_enc;
    private double bl_enc;
    private double br_enc;

    private double fl_enc_was;
    private double fr_enc_was;
    private double bl_enc_was;
    private double br_enc_was;

    private double delta_field_x = 0.0;
    private double delta_field_y = 0.0;

    private double field_a = 0.0;
    private double field_x = 0.0;
    private double field_y = 0.0;

    private double target_x;
    private double target_y;
    private double target_a;

    private final double strafe_efficiency = 1.0;
    private final double radians_per_tick = (10*Math.PI)/(10246); // 10246 was the tick count after we spun the robot 2pi radians 10 times
    private final double inches_per_tick = (1/Status.TICKS_PER_ROTATION)*(96*Math.PI/25.4)*(1/15.2);

    private double center_x; // x-position of arc center
    private double center_y; // y-position of arc center
    private double q; // angle between y-axis and robot heading after loop cycle
    private double r; // radius of arc robot moves in when its heading changes in a loop cycle

    private Drivetrain drivetrain;


    public void setTarget(double x, double y, double a){
        target_x = x;
        target_y = y;
        target_a = a;
    }

    @Override
    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager) {
        drivetrain = robot.drivetrain;
    }

    public void checkPosition(){

    }

    @Override
    public void update(Telemetry telemetry){
        double delta_x; // change in robot x-position (forward/backward)
        double delta_y; // change in robot y-position (strafing)
        double delta_a; // change in robot angle

        double error;
        double direction;// direction of robot motion

        fl_enc = drivetrain.getEncoderValue(Drivetrain.encoderNames.FRONT_LEFT) ;
        fr_enc = drivetrain.getEncoderValue(Drivetrain.encoderNames.FRONT_RIGHT) ;
        bl_enc = drivetrain.getEncoderValue(Drivetrain.encoderNames.BACK_LEFT) ;
        br_enc = drivetrain.getEncoderValue(Drivetrain.encoderNames.BACK_RIGHT) ;

        double delta_fl = fl_enc-fl_enc_was;
        double delta_fr = fr_enc-fr_enc_was;
        double delta_bl = bl_enc-bl_enc_was;
        double delta_br = br_enc-br_enc_was;

        // converts encoder values to changes in robot x, y, and a
        delta_x = inches_per_tick * (delta_fl+delta_fr) / 2;
        delta_y = inches_per_tick * strafe_efficiency * (delta_bl-delta_fl) / 2;
        delta_a = -radians_per_tick * (delta_fr-delta_bl) / 2;

        double s = Math.sqrt((delta_x * delta_x) + (delta_y * delta_y)); // arc length, or distance travelled

        if (delta_a == 0) // Travelling in a straight line, an arc with infinite radius
        {
            direction = field_a + Math.atan2(delta_y, delta_x);
            delta_field_x = s * Math.cos(direction);
            delta_field_y = s * Math.sin(direction);
        }
        else
        { // Travelling in an arc with finite radius
            direction = field_a + Math.atan2(delta_y, delta_x);
            q = direction + delta_a;
            r = s/delta_a;
            center_x = r*Math.cos(direction+(Math.PI/2));
            center_y = r*Math.sin(direction+(Math.PI/2));
            delta_field_x = (r*Math.sin(q))+center_x;
            delta_field_y = (r*Math.cos(q))+center_y;
            field_a += delta_a;
        }

        fl_enc_was = fl_enc;
        fr_enc_was = fr_enc;
        bl_enc_was = bl_enc;
        br_enc_was = br_enc;

        field_x += delta_field_x;
        field_y += delta_field_y;

        telemetry.addData("encoder FL: ", fl_enc);
        telemetry.addData("encoder FR: ", fr_enc);
        telemetry.addData("encoder BL: ", bl_enc);
        telemetry.addData("encoder BR: ", br_enc);

        telemetry.addData("delta x: ", delta_x);
        telemetry.addData("delta y: ", delta_y);
        telemetry.addData("delta a: ", delta_a);

        telemetry.addData("x position: ", field_x);
        telemetry.addData("y position: ", field_y);
        telemetry.addData("center_Y", center_y);
        telemetry.addData("angle: ", field_a);
        telemetry.addData("r = ", r);
    }

}
