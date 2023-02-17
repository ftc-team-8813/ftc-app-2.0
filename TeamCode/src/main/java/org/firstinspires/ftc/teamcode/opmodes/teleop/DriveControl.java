package org.firstinspires.ftc.teamcode.opmodes.teleop;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Drivetrain;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.hardware.navigation.Odometry;
import org.firstinspires.ftc.teamcode.input.ControllerMap;

public class DriveControl extends ControlModule {

    private Drivetrain drivetrain;
    private Odometry odometry;

    private ControllerMap.AxisEntry ax_drive_left_x;
    private ControllerMap.AxisEntry ax_drive_left_y;
    private ControllerMap.AxisEntry ax_drive_right_x;
    private ControllerMap.ButtonEntry dpad_up;

    private boolean field_centric = false;

    private double heading_delta = 0;
    private double heading_was = 0;

    public DriveControl(String name) {
        super(name);
    }

    @Override
    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager) {
        this.drivetrain = robot.drivetrain;
        this.odometry = robot.odometry;

        drivetrain.resetEncoders();
        odometry.Up();

        ax_drive_left_x = controllerMap.getAxisMap("drive:strafe", "gamepad1", "left_stick_x");
        ax_drive_left_y = controllerMap.getAxisMap("drive:forward", "gamepad1", "left_stick_y");
        ax_drive_right_x = controllerMap.getAxisMap("drive:turn", "gamepad1", "right_stick_x");
        dpad_up = controllerMap.getButtonMap("drive:mode", "gamepad1","dpad_up");
    }

    @Override
    public void update(Telemetry telemetry) {

        if (dpad_up.edge() == -1) {
            field_centric = !field_centric;
        }

        heading_delta = drivetrain.getHeading() - heading_was;

        if (ax_drive_right_x.get() != 0) {
            heading_delta = 0;
        }

        if (heading_delta > 300) {
            heading_delta -= 360;
        }

        if (heading_delta < -300) {
            heading_delta += 360;
        }

        double y = -ax_drive_left_y.get();
        double x = ax_drive_left_x.get() /* 1.1 */;
        double rx = ax_drive_right_x.get() * 0.7;


        double botHeading = -1* Math.toRadians(drivetrain.getHeading());

        double rotX = x * Math.cos(botHeading) - y * Math.sin(botHeading);
        double rotY = x * Math.sin(botHeading) + y * Math.cos(botHeading);



        double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);


        if (field_centric) {
            drivetrain.move(rotY, rotX, rx, (heading_delta * 0.001), denominator);
        }
        else {
            drivetrain.move(Math.pow(Math.abs(-ax_drive_left_y.get()), 1.6) * Math.signum(-ax_drive_left_y.get()),Math.pow(Math.abs(ax_drive_left_x.get()), 1.6) * Math.signum(ax_drive_left_x.get()),Math.pow(Math.abs(ax_drive_right_x.get()), 1.6) * Math.signum(ax_drive_right_x.get()) * 0.7,(heading_delta * 0.001));
        }

        heading_was = drivetrain.getHeading();

        telemetry.addData("IMU Radians", Math.toRadians(drivetrain.getHeading()));
        telemetry.addData("IMU", drivetrain.getHeading());

        telemetry.addData("rotX", rotX);
        telemetry.addData("rotY", rotY);
        telemetry.addData("denominator", denominator);

        telemetry.addData("Heading: ", drivetrain.getHeading());
        telemetry.addData("Field Centric",field_centric);
    }

    @Override
    public void stop() {
        super.stop();
    }
}
