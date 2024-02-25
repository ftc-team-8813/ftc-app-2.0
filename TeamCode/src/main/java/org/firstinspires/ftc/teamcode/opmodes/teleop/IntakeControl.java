package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Intake;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;

public class IntakeControl extends ControlModule{

    private Intake intake;
    public ControllerMap.ButtonEntry switchForward;
    public ControllerMap.ButtonEntry stopIntake;
    public ControllerMap.ButtonEntry switchBackward;


    public boolean forward = true;
    public boolean stop = false;
    public double power = 1;

    private ElapsedTime timer;

//    private boolean startOfGame = false;
    public IntakeControl(String name) {
        super(name);
    }

    @Override
    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager) {
        this.intake = robot.intake;
        switchForward = controllerMap.getButtonMap("switchForward", "gamepad1", "x");
        switchBackward = controllerMap.getButtonMap("switchBackward", "gamepad1", "b");
        stopIntake = controllerMap.getButtonMap("stopIntake", "gamepad1", "a");

        forward = false;
        stop = true;
        power =  0;
//        startOfGame = true;

        timer = new ElapsedTime();
    }

    @Override
    public void update(Telemetry telemetry) {

        if (forward && !stop) {
            power = 0.65;
        } else if (!forward && !stop) {
            power = -0.65;
        } else if (stop){
            power = 0;
        }
        intake.setPower(power);

        if (switchForward.edge() == -1) {
            forward = true;
            stop = false;
        } else if (switchBackward.edge() == -1) {
            forward = false;
            stop = false;
        } else if(stopIntake.edge() == -1){
            stop = true;
        }

        if(switchForward.edge() == -1){
            timer.reset();
        }

        telemetry.addData("Time", timer.time());
        telemetry.addData("Timer time", timer.milliseconds());
    }
}
