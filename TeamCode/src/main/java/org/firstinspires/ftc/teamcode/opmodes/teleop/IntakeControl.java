package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Intake;
import org.firstinspires.ftc.teamcode.hardware.Lift;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.util.Status;

public class IntakeControl extends ControlModule{
    private Intake intake;
    private Lift lift;
    private ElapsedTime timer;

    private ControllerMap.AxisEntry left_trigger;
    private ControllerMap.AxisEntry right_trigger;

    private boolean holding_freight;

    public IntakeControl(String name) {
        super(name);
    }

    @Override
    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager) {
        this.intake = robot.intake;
        this.lift = robot.lift;
        this.timer = new ElapsedTime();

        left_trigger = controllerMap.getAxisMap("intake:outtake", "gamepad1", "left_trigger");
        right_trigger = controllerMap.getAxisMap("intake:intake", "gamepad1", "right_trigger");
    }

    @Override
    public void init_loop(Telemetry telemetry) {
        super.init_loop(telemetry);
        intake.setPower(1);
    }

    @Override
    public void update(Telemetry telemetry) {

        if (intake.freightDetected() && intake.getPower() > 0.1){ // TODO Make sure positive power is intaking
            timer.reset();
            holding_freight = true;
        }

        if (timer.seconds() > Status.HOLD_TIME && holding_freight){
            intake.deposit(Status.CLOSE_DEPOSIT);
            holding_freight = false;
        }

        if (!intake.freightDetected() && (lift.getLiftPosition() < 100)){
            intake.setPower(right_trigger.get() - left_trigger.get());
        }
        else{
            intake.setPower(-left_trigger.get());
        }
    }
}
