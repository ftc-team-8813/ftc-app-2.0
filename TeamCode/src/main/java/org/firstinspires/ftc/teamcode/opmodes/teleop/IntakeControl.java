package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Intake;
import org.firstinspires.ftc.teamcode.hardware.Lift;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;

public class IntakeControl extends ControlModule {

    private Intake intake;

    private boolean claw_open = true;
    private boolean let_cone_go = false;

    private ElapsedTime timer = new ElapsedTime();

    private ControllerMap.ButtonEntry right_bumper;

    public IntakeControl(String name) {
        super(name);
    }

    @Override
    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager) {
        this.intake = robot.intake;

        right_bumper = controllerMap.getButtonMap("intake:claw","gamepad1","right_bumper");
    }

    @Override
    public void update(Telemetry telemetry) {

        if(claw_open) {
            intake.setClawPosition(0.23);
        }
        else {
            intake.setClawPosition(0.37);
        }

        if (right_bumper.edge() == -1) {
            claw_open = !claw_open;
        }

        if (intake.getDistance() < 0.17){
            claw_open = false;
        }

        if (intake.intaken() && !let_cone_go) {
            intake.setWristPosition(0.678);
            let_cone_go = true;
            timer.reset();
        }

        if(let_cone_go && timer.seconds() > 0.7) {
            claw_open = true;
            let_cone_go = false;
        }
    }
}
