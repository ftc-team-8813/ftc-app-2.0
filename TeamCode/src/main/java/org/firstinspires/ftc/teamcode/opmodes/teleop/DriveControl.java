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

    private double SENSITIVITY;
    private double past_error;
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

        SENSITIVITY = Storage.getJsonValue("sensitivity");
    }


    @Override
    public void update(Telemetry telemetry) {
        if (right_bumper.edge() == -1) {
            drivetrain.stop();
            endgame = !endgame;
        }

        if (!endgame) {
            drivetrain.move(-ax_drive_left_y.get(),
                            ax_drive_left_x.get(),
                            ax_drive_right_x.get() * 0.5,
                            0);
        }

        telemetry.addData("Heading: ", drivetrain.getHeading());
        telemetry.addData("Target Heading: ", target_heading);
        telemetry.addData("Angular Velocity: ", drivetrain.getAngularVelocity());
    }

    @Override
    public void stop() {
        super.stop();
        drivetrain.closeIMU();
    }
}
