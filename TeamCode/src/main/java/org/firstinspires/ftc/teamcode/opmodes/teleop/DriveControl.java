package org.firstinspires.ftc.teamcode.opmodes.teleop;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Drivetrain;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.util.Storage;

public class DriveControl extends ControlModule{
    private Drivetrain drivetrain;

    private ControllerMap.AxisEntry ax_drive_left_x;
    private ControllerMap.AxisEntry ax_drive_left_y;
    private ControllerMap.AxisEntry ax_drive_right_x;
    private ControllerMap.ButtonEntry right_bumper;

    private double HEADING_CORRECTION_kP;
    private double HEADING_CORRECTION_TARGET_BASED;
    private double past_heading = 0;
    private double summed_heading_error;
    private boolean endgame = false;

    private double target_heading = 0;

    public DriveControl(String name) {
        super(name);
    }


    @Override
    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager) {
        this.drivetrain = robot.drivetrain;

        ax_drive_left_x = controllerMap.getAxisMap("drive:left_x", "gamepad1", "left_stick_x");
        ax_drive_left_y = controllerMap.getAxisMap("drive:right_y", "gamepad1", "left_stick_y");
        ax_drive_right_x = controllerMap.getAxisMap("drive:right_x", "gamepad1", "right_stick_x");

        right_bumper = controllerMap.getButtonMap("endgame", "gamepad1", "right_bumper");

        HEADING_CORRECTION_kP = Storage.getJsonValue("heading_correction_kp");
        HEADING_CORRECTION_TARGET_BASED = Storage.getJsonValue("heading_correction_target_based");
    }


    @Override
    public void update(Telemetry telemetry) {
        if (right_bumper.edge() == -1) {
            drivetrain.stop();
            endgame = !endgame;
        }

        if (!endgame) {
            hold_target_heading();
        }

        telemetry.addData("Heading Correction kP: ", HEADING_CORRECTION_kP);
        telemetry.addData("Heading: ", drivetrain.getHeading());
        telemetry.addData("Angular Velocity: ", drivetrain.getAngularVelocity());
    }

    /**
     * Decreases the power of the faster side
     * Faster side will be opposite the direction of rotation
     */
    public void telemove(){
        double curr_heading = drivetrain.getHeading();
        double heading_error = curr_heading - past_heading;
        summed_heading_error += heading_error;
        if (Math.abs(ax_drive_right_x.get()) > 0.05) heading_error = 0;

        drivetrain.move(-ax_drive_left_y.get(),
                        ax_drive_left_x.get(),
                        ax_drive_right_x.get() + (heading_error * HEADING_CORRECTION_kP));

        past_heading = curr_heading;
    }

    public void hold_target_heading(){
        double curr_heading = target_heading;
        double turn_power;
        if (Math.abs(ax_drive_right_x.get()) > 0.05){
            target_heading = curr_heading;
            turn_power = ax_drive_right_x.get();
        } else {
            double error = target_heading - curr_heading;
            turn_power = error * HEADING_CORRECTION_TARGET_BASED;
        }
        drivetrain.move(-ax_drive_left_y.get(),
                        ax_drive_left_x.get(),
                        turn_power);
    }

    @Override
    public void stop() {
        super.stop();
        drivetrain.closeIMU();
    }
}
