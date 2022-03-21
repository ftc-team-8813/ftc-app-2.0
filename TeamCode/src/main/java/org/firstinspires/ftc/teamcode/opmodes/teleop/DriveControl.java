package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngularVelocity;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.hardware.Drivetrain;
import org.firstinspires.ftc.teamcode.hardware.IMU;
import org.firstinspires.ftc.teamcode.hardware.Lift;
import org.firstinspires.ftc.teamcode.hardware.LineFinder;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.util.Status;

public class DriveControl extends ControlModule{
    private Drivetrain drivetrain;

    private ControllerMap.AxisEntry ax_drive_left_x;
    private ControllerMap.AxisEntry ax_drive_left_y;
    private ControllerMap.AxisEntry ax_drive_right_x;


    public DriveControl(String name) {
        super(name);
    }


    @Override
    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager) {
        this.drivetrain = robot.drivetrain;

        ax_drive_left_x = controllerMap.getAxisMap("drive:left_x", "gamepad1", "left_stick_x");
        ax_drive_left_y = controllerMap.getAxisMap("drive:right_y", "gamepad1", "left_stick_y");
        ax_drive_right_x = controllerMap.getAxisMap("drive:right_x", "gamepad1", "right_stick_x");
    }


    @Override
    public void update(Telemetry telemetry) {
        telemove();
    }

    /**
     * Decreases the power of the faster side
     * Faster side will be opposite the direction of rotation
     */
    public void telemove(){
        double rotation_rate = drivetrain.getIMU().getAngularVelocity().xRotationRate;
        rotation_rate = 1;
        if (rotation_rate < 0){
            drivetrain.move(-ax_drive_left_y.get(),
                            ax_drive_left_x.get(),
                            ax_drive_right_x.get(),
                            1,
                            rotation_rate * Status.HEADING_CORRECTION_SCALAR);
        } else {
            drivetrain.move(-ax_drive_left_y.get(),
                            ax_drive_left_x.get(),
                            ax_drive_right_x.get(),
                           rotation_rate * Status.HEADING_CORRECTION_SCALAR,
                           1);
        }
    }
}
