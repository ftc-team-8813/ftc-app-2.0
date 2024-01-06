package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.arcrobotics.ftclib.hardware.motors.MotorEx;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Drivetrain;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;

public class DriveControl extends ControlModule{

    private Drivetrain drivetrain;

    private ControllerMap.AxisEntry ax_drive_left_x;
    private ControllerMap.AxisEntry ax_drive_left_y;
    private ControllerMap.AxisEntry ax_drive_right_x;
    private ControllerMap.ButtonEntry switchDriveMode;
    private boolean robotCentric = true;
    public DriveControl(String name) {
        super(name);
    }

    @Override
    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager) {
        this.drivetrain = robot.drivetrain;

        ax_drive_left_x = controllerMap.getAxisMap("drive:left_x", "gamepad1", "left_stick_x");
        ax_drive_left_y = controllerMap.getAxisMap("drive:right_y", "gamepad1", "left_stick_y");
        ax_drive_right_x = controllerMap.getAxisMap("drive:right_x", "gamepad1", "right_stick_x");
        switchDriveMode = controllerMap.getButtonMap("switchDriveMode", "gamepad2", "a");
        robotCentric = true;
    }

    @Override
    public void update(Telemetry telemetry) {
//        drivetrain.move(-ax_drive_left_y.get(), ax_drive_left_x.get(), ax_drive_right_x.get(), 0);
//        if(robotCentric){
        drivetrain.move(-ax_drive_left_y.get(), ax_drive_left_x.get(), ax_drive_right_x.get(), 0);
        drivetrain.getMotorPowers(telemetry);
//        }else if(!robotCentric){
//            drivetrain.moveFieldCentric(ax_drive_left_x.get(), -ax_drive_left_y.get(), ax_drive_right_x.get());
//        }
//
//        if(switchDriveMode.edge() == -1 && robotCentric){
//            robotCentric = false;
//        }else if(switchDriveMode.edge() == -1 && !robotCentric){
//            robotCentric = true;
//        }
    }
}
