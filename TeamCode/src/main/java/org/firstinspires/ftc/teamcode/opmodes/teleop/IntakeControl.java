package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Intake;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;

public class IntakeControl extends ControlModule {

    private Intake intake;
    private boolean claw_open;
    private boolean cone_detected = false;
//    private boolean first_close = false;
    private ElapsedTime timer = new ElapsedTime();
    private boolean wait_till_close = true;

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

        if (right_bumper.edge() == -1) {
            claw_open = !claw_open;
        }

        if (claw_open) {
            intake.setClaw(0.11); //figure out claw positions
            if (wait_till_close) {
                timer.reset();
                wait_till_close = false;
            }
            if (timer.seconds() > 1) {
                cone_detected = false;
            }
        }
        if (!claw_open) {
            intake.setClaw(0.63); // figure out claw positions
        }

        if(intake.getDistance() < 20.0 && !cone_detected) {
            claw_open = false;
            cone_detected = true;
            wait_till_close = true;
        }


        telemetry.addData("claw sensor dist", intake.getDistance());
    }
}
