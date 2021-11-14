package org.firstinspires.ftc.teamcode.opmodes.teleop;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Drivetrain;
import org.firstinspires.ftc.teamcode.hardware.Lift;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.hardware.navigation.Odometry;
import org.firstinspires.ftc.teamcode.input.ControllerMap;

public class DriveControl extends ControlModule{
    private Drivetrain drivetrain;
    private Odometry odometry;
    private Lift lift;

    private double turn_scalar;

    private ControllerMap.AxisEntry ax_drive_left_x;
    private ControllerMap.AxisEntry ax_drive_left_y;
    private ControllerMap.AxisEntry ax_drive_right_x;
    private ControllerMap.ButtonEntry btn_dpad_up;
    private ControllerMap.ButtonEntry btn_dpad_down;


    public DriveControl(String name) {
        super(name);
    }


    @Override
    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager) {
        this.drivetrain = robot.drivetrain;
        this.odometry = robot.odometry;
        this.lift = robot.lift;

        ax_drive_left_x = controllerMap.getAxisMap("drive:left_x", "gamepad1", "left_stick_x");
        ax_drive_left_y = controllerMap.getAxisMap("drive:right_y", "gamepad1", "left_stick_y");
        ax_drive_right_x = controllerMap.getAxisMap("drive:right_x", "gamepad1", "right_stick_x");
        btn_dpad_up = controllerMap.getButtonMap("drive:up", "gamepad1", "dpad_up");
        btn_dpad_down = controllerMap.getButtonMap("drive:down", "gamepad1", "dpad_down");
    }


    @Override
    public void update(Telemetry telemetry) {
        drivetrain.teleMove(-ax_drive_left_y.get() * 0.8, ax_drive_left_x.get() * 0.8, ax_drive_right_x.get() * turn_scalar);

        double extension = lift.getStates()[2];
        if (extension > 0){
            turn_scalar = 0.2;
        } else {
            turn_scalar = 0.4;
        }
    }
}
