package org.firstinspires.ftc.teamcode.opmodes.teleop;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.hardware.Drivetrain;
import org.firstinspires.ftc.teamcode.hardware.IMU;
import org.firstinspires.ftc.teamcode.hardware.Lift;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.hardware.AutoDrive;
import org.firstinspires.ftc.teamcode.util.Status;

import java.util.List;
import org.firstinspires.ftc.teamcode.util.Status;

public class DriveControl extends ControlModule{
    private Drivetrain drivetrain;
    private Lift lift;
    private IMU imu;


    private double speed_scalar = 2;

    private ControllerMap.AxisEntry ax_drive_left_x;
    private ControllerMap.AxisEntry ax_drive_left_y;
    private ControllerMap.AxisEntry ax_drive_right_x;
    private ControllerMap.ButtonEntry btn_y;
    private ControllerMap.ButtonEntry btn_a;
    private ControllerMap.ButtonEntry btn_x;
    private ControllerMap.ButtonEntry btn_dpad_down;
    private AutoDrive autoDrive;

    private double heading_was;
    private double heading_delta;

    //private double target_angle=0.0; // robot target heading for correction
    //private double turn_scaling=0.1; //scales the right joystick x value for turn correction


    public DriveControl(String name) {
        super(name);
    }


    @Override
    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager) {
        this.drivetrain = robot.drivetrain;
        this.imu = robot.imu;
        this.lift = robot.lift;

        ax_drive_left_x = controllerMap.getAxisMap("drive:left_x", "gamepad1", "left_stick_x");
        ax_drive_left_y = controllerMap.getAxisMap("drive:right_y", "gamepad1", "left_stick_y");
        ax_drive_right_x = controllerMap.getAxisMap("drive:right_x", "gamepad1", "right_stick_x");
        btn_y = controllerMap.getButtonMap("lift:extend_high", "gamepad2", "y");
        btn_a = controllerMap.getButtonMap("lift:extend_low", "gamepad2", "a");
        btn_x = controllerMap.getButtonMap("lift:extend_neutral", "gamepad2", "x");
        btn_dpad_down = controllerMap.getButtonMap("lift:reset", "gamepad2", "dpad_down");
    }


    @Override
    public void update(Telemetry telemetry) {
        if (btn_a.get() || btn_x.get() || btn_y.get()){
            speed_scalar = 1;
        } else if (btn_dpad_down.get()){
            speed_scalar = 2;
        }

        // TURN CORRECTION - NOT WANTED YET
        /*
        double real_angle=0.0;
        double turn_correction;
        target_angle += ax_drive_right_x.get()*turn_scaling*speed_scalar;

        real_angle = Math.toRadians(imu.getHeading());

        double error =  real_angle - target_angle;
        turn_correction = error * Status.turnP;

        if (ax_drive_right_x.get() != 0 || ax_drive_left_x.get() !=0 || ax_drive_left_y.get() !=0 || Math.abs(error)>0) {
            drivetrain.move(-ax_drive_left_y.get() * 0.4 * speed_scalar,
                    ax_drive_left_x.get() * 0.4 * speed_scalar,
                    -turn_correction);
        }

        AutoDrive.update(telemetry);

        telemetry.addData("Z rotation rate", imu.getInternalImu().getAngularVelocity().toAngleUnit(AngleUnit.RADIANS).zRotationRate);
        telemetry.addData("Y rotation rate", imu.getInternalImu().getAngularVelocity().toAngleUnit(AngleUnit.RADIANS).yRotationRate);
        telemetry.addData("X rotation rate", imu.getInternalImu().getAngularVelocity().toAngleUnit(AngleUnit.RADIANS).xRotationRate);

        telemetry.addData("Turn Correction Error", error);
        */

        //telemetry.addData("x Acceleration: ", imu.getInternalImu().getLinearAcceleration().xAccel);
        //telemetry.addData("y Acceleration: ", imu.getInternalImu().getLinearAcceleration().yAccel);
        //telemetry.addData("z Acceleration: ", imu.getInternalImu().getLinearAcceleration().zAccel);
        telemetry.addData("Heading: ", imu.getHeading());
        telemetry.addData("Heading Delta: ", heading_delta);
        teleMove(-ax_drive_left_y.get() * 0.45 * speed_scalar,
                                     ax_drive_left_x.get() * 0.45 * speed_scalar,
                                      ax_drive_right_x.get() * 0.45 * speed_scalar);
    }

    public void teleMove(double forward, double strafe, double turn) {
        heading_delta = imu.getHeading() - heading_was;
        //if (Math.abs(heading_delta) > 4) heading_delta = 0;
        if (turn != 0) heading_delta = 0;

        drivetrain.move(forward, (strafe * 0.7), (turn * 0.6) + (heading_delta * Status.TURN_CORRECTION_P));
        heading_was = imu.getHeading();
    }
}
