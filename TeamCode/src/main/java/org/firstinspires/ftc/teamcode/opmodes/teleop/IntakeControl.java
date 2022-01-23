package org.firstinspires.ftc.teamcode.opmodes.teleop;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Intake;
import org.firstinspires.ftc.teamcode.hardware.Lift;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.util.Status;

public class IntakeControl extends ControlModule {
    private Intake intake;
    private Lift lift;
    private ControllerMap.AxisEntry ax_right_trigger;
    private ControllerMap.AxisEntry ax_left_trigger;
    private ControllerMap.ButtonEntry btn_left_bumper;
    private ControllerMap.ButtonEntry btn_right_bumper;

    private double direction = 1; // positive = Intake, negative = Outtake
    private double side = 3; // 1 = Front, 0 = Center, -1 = Back, 2 = Dump
    private double side_was = 3; // position of bucket on last loop cycle
    private boolean carrying = true;

    private long current_time = 0;
    private long target_time = Status.TIME_BEFORE_INTAKING;


    public IntakeControl(String name) {
        super(name);
    }

    @Override
    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager) {
        this.intake = robot.intake;
        this.lift = robot.lift;
        this.intake.stop();

        ax_right_trigger = controllerMap.getAxisMap("intake:intake_front", "gamepad1", "right_trigger");
        ax_left_trigger = controllerMap.getAxisMap("intake:intake_back", "gamepad1", "left_trigger");
        btn_left_bumper = controllerMap.getButtonMap("intake:outtake_override", "gamepad1", "left_bumper");
        btn_right_bumper = controllerMap.getButtonMap("lift:deposit", "gamepad2", "right_bumper");

        intake.deposit(Status.DEPOSITS.get("carry"));
    }

    @Override
    public void update(Telemetry telemetry) {
        current_time = System.nanoTime();
        if (intake.getFreightDistance() < Status.FREIGHT_DETECTION){
            direction = -0.6;
            side = 0;
            carrying = true;
        } else  {
            direction = 0.9;
            carrying = false;
        }

        if (ax_right_trigger.get() > 0.5){
            if (!carrying){
                side = 1;
            }
            if (side == side_was && current_time >= target_time){
                intake.setIntakeFront(direction);
            }
        } else {
            intake.setIntakeFront(0);
        }

        if (ax_left_trigger.get() > 0.5){
            if (!carrying){
                side = -1;
            }
            if (side == side_was && current_time >= target_time) {
                intake.setIntakeBack(direction);
            }
        } else {
            intake.setIntakeBack(0);
        }

        if (btn_left_bumper.get()){
            intake.setIntakeFront(-1);
            intake.setIntakeBack(-1);
        }

        if (lift.getLiftTargetPos() > 1000) {
            if (btn_right_bumper.get()) {
                intake.deposit(Status.DEPOSITS.get("dump"));
                side = 2;
            } else {
                side = 0;
            }
        }

        if (side == 0){
            if (lift.getLiftTargetPos() == Status.STAGES.get("pitstop") || lift.getLiftTargetPos() == 0 || lift.getLiftTargetPos() > Status.STAGES.get("neutral") + 20000) {
                intake.deposit(Status.DEPOSITS.get("carry"));
            } else {
                intake.deposit(Status.DEPOSITS.get("tilt"));
            }
        } else if (side == 1){
            if (lift.getLiftCurrentPos() < 2000) {
                intake.deposit(Status.DEPOSITS.get("front"));
            }
        } else if (side == -1){
            if (lift.getLiftCurrentPos() < 2000) {
                intake.deposit(Status.DEPOSITS.get("back"));
            }
        }

        if (side != side_was){
            target_time = current_time+Status.TIME_BEFORE_INTAKING;
        }

        side_was=side;

        telemetry.addData("Freight Distance: ", intake.getFreightDistance());
    }
}