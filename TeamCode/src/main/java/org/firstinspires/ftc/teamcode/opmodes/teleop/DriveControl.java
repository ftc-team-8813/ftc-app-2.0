package org.firstinspires.ftc.teamcode.opmodes.teleop;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Drivetrain;
import org.firstinspires.ftc.teamcode.hardware.Lift;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.util.Storage;

public class DriveControl extends ControlModule{
    private Drivetrain drivetrain;
    private Lift lift;

    private ControllerMap.AxisEntry ax_drive_left_x;
    private ControllerMap.AxisEntry ax_drive_left_y;
    private ControllerMap.AxisEntry ax_drive_right_x;
    private ControllerMap.ButtonEntry right_bumper;

    private double SENSITIVITY;
    private double PITSTOP;
    private double HEADING_CORRECTION_kP;

    private boolean endgame = false;

    private double heading_delta = 0;
    private double heading_was = 0;
    private double target_heading = 0;

    public DriveControl(String name) {
        super(name);
    }


    @Override
    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager) {
        this.drivetrain = robot.drivetrain;
        this.lift = robot.lift;

        ax_drive_left_x = controllerMap.getAxisMap("drive:left_x", "gamepad1", "left_stick_x");
        ax_drive_left_y = controllerMap.getAxisMap("drive:right_y", "gamepad1", "left_stick_y");
        ax_drive_right_x = controllerMap.getAxisMap("drive:right_x", "gamepad1", "right_stick_x");

        right_bumper = controllerMap.getButtonMap("endgame", "gamepad1", "right_bumper");

        SENSITIVITY = Storage.getJsonValue("sensitivity");
        PITSTOP = Storage.getJsonValue("pitstop");
        HEADING_CORRECTION_kP = Storage.getJsonValue("heading_correction_kp");
    }


    @Override
    public void update(Telemetry telemetry) {
        if (right_bumper.edge() == -1) {
            drivetrain.stop();
            endgame = !endgame;
        }

        heading_delta = drivetrain.getHeading() - heading_was;

        double scalar;
        if (lift.getLiftPosition() > PITSTOP) {
            scalar = 0.6;
        } else {
            scalar = 1;
        }

        if (!endgame) {
            drivetrain.move(-ax_drive_left_y.get() * scalar,
                    ax_drive_left_x.get() * scalar,
                    ax_drive_right_x.get() * 0.5 * scalar,
                    heading_delta * HEADING_CORRECTION_kP);
        }

        if (ax_drive_right_x.get() != 0) {
            heading_delta = 0;
        }

        telemetry.addData("Heading: ", drivetrain.getHeading());
        telemetry.addData("Target Heading: ", target_heading);
        telemetry.addData("Angular Velocity: ", drivetrain.getAngularVelocity());
        heading_was = drivetrain.getHeading();
    }

    @Override
    public void stop() {
        super.stop();
        drivetrain.closeIMU();
    }
}
