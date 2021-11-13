package org.firstinspires.ftc.teamcode.opmodes.teleop;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Intake;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.util.Status;

public class IntakeControl extends ControlModule {
    private Intake intake;
    private ControllerMap.AxisEntry ax_intake;
    private ControllerMap.AxisEntry ax_outtake;

    boolean test = false;


    public IntakeControl(String name) {
        super(name);
    }


    @Override
    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager) {
        this.intake = robot.intake;
        this.intake.stop();

        ax_intake = controllerMap.getAxisMap("intake:intake", "gamepad1", "right_trigger");
        ax_outtake = controllerMap.getAxisMap("intake:outtake", "gamepad1", "left_trigger");
    }

    @Override
    public void update(Telemetry telemetry) {
        if (ax_intake.get() > 0.5){
            intake.intake();
        } else if (ax_outtake.get() > 0.5){
            intake.outtake();
        } else {
            intake.stop();
        }
    }
}