package org.firstinspires.ftc.teamcode.opmodes.teleop;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Intake;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.util.Status;

public class IntakeControl extends ControlModule {
    private Intake intake;
    private ControllerMap.AxisEntry ax_intake;


    public IntakeControl(String name) {
        super(name);
    }


    @Override
    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager) {
        this.intake = robot.intake;
        this.intake.dropperOpen();
        this.intake.stop();

        ax_intake = controllerMap.getAxisMap("intake:trigger", "gamepad1", "right_trigger");
    }

    @Override
    public void update(Telemetry telemetry) {

        if (intake.getDistance() < Status.BLOCK_DETECT){
            intake.dropperClose();
            if (ax_intake.get() > 0.5){
                intake.outtake();
            } else {
                intake.stop();
            }
        } else if (intake.getDistance() > Status.ARM_AWAY){
            intake.dropperClose();
            intake.stop();
        } else {
            intake.dropperOpen();
            if (ax_intake.get() > 0.5){
                intake.intake();
            } else {
                intake.stop();
            }
        }

        telemetry.addData("Distance Sensor: ", intake.getDistance());
    }
}