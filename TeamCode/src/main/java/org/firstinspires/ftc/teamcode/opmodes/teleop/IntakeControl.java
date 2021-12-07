package org.firstinspires.ftc.teamcode.opmodes.teleop;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Intake;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.util.Status;

public class IntakeControl extends ControlModule {
    private Intake intake;
    private ControllerMap.AxisEntry ax_intake_front;
    private ControllerMap.AxisEntry ax_intake_back;

    private double direction;


    public IntakeControl(String name) {
        super(name);
    }


    @Override
    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager) {
        this.intake = robot.intake;
        this.intake.stop();

        ax_intake_front = controllerMap.getAxisMap("intake:intake_front", "gamepad1", "right_trigger");
        ax_intake_back = controllerMap.getAxisMap("intake:intake_back", "gamepad1", "left_trigger");
    }

    @Override
    public void update(Telemetry telemetry) {
        if (intake.freightDetected()){
            direction = -1;
        } else  {
            direction = 1;
        }

        if (ax_intake_front.get() > 0.5){
            intake.setIntakeFront(direction);
        } else {
            intake.setIntakeFront(0);
        }

        if (ax_intake_back.get() > 0.5){
            intake.setIntakeBack(direction);
        } else {
            intake.setIntakeFront(0);
        }
    }
}