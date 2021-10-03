package org.firstinspires.ftc.teamcode.opmodes.teleop;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Intake;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;

public class IntakeControl extends ControlModule{
    private Intake intake;
    private ControllerMap.AxisEntry ax_intake;
    private ControllerMap.AxisEntry ax_outtake;


    public IntakeControl(String name){super(name);}


    @Override
    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager) {
        this.intake = robot.intake;
        ax_intake = controllerMap.getAxisMap("intake:trigger_in", "gamepad1", "left_trigger");
        ax_outtake = controllerMap.getAxisMap("intake:trigger_out", "gamepad1", "right_trigger");
    }

    @Override
    public void update(Telemetry telemetry) {
        if (ax_intake.get() > 0.5){
            intake.intake();
        } else if (ax_outtake.get() > 0.5){
            intake.outtake();
        }
    }
}
