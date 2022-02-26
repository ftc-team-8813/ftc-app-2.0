package org.firstinspires.ftc.teamcode.opmodes.teleop;

import static java.lang.Math.round;

import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Intake;
import org.firstinspires.ftc.teamcode.hardware.Lift;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.util.Logger;
import org.firstinspires.ftc.teamcode.util.Status;

public class LiftControl extends ControlModule{
    private Lift lift;
    private Intake intake;
    private ControllerMap.AxisEntry ax_left_stick_y;
    private ControllerMap.ButtonEntry btn_y;
    private ControllerMap.ButtonEntry btn_a;
    private ControllerMap.ButtonEntry btn_x;
    private ControllerMap.ButtonEntry btn_dpad_down;
    private ControllerMap.ButtonEntry btn_dpad_up;
    private ControllerMap.ButtonEntry btn_right_bumper;
    private ControllerMap.AxisEntry ax_left_trigger;
    private ControllerMap.AxisEntry ax_right_trigger;

    private int height = -1; // 0 = bottom, 1 = low, 2 = mid, 3 = high, 4 = neutral, 5 = high2 (goal tipped), 6 = really high (duck cycling)
    private final ElapsedTime timer = new ElapsedTime();
    private int id_lift = 0;
    private int id_outrigger = 0;
    private final boolean was_reset = false;
    private boolean outrigger_triggered = false;
    private boolean drop_outrigger = false;
    private final double var_pit_stop_wait = Status.PITSTOP_WAIT_TIME;
    public boolean duck_cycle_flag = false;

    public boolean just_deposited = false;
    private final ElapsedTime deposit_timer = new ElapsedTime();
    private boolean timer_counting = false;

    private Logger log;


    public LiftControl(String name){super(name);}

    @Override
    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager) {
        this.lift = robot.lift;
        this.intake = robot.intake;
        log = new Logger("Lift Control");

        ax_left_stick_y = controllerMap.getAxisMap("lift:adjust", "gamepad2", "left_stick_y");
        btn_y = controllerMap.getButtonMap("lift:extend_high", "gamepad2", "y");
        btn_a = controllerMap.getButtonMap("lift:extend_low", "gamepad2", "a");
        btn_x = controllerMap.getButtonMap("lift:extend_neutral", "gamepad2", "x");
        btn_dpad_down = controllerMap.getButtonMap("lift:home", "gamepad2", "dpad_down");
        ax_left_trigger = controllerMap.getAxisMap("lift:reset", "gamepad2", "left_trigger");
        ax_right_trigger = controllerMap.getAxisMap("lift:extend_really_high", "gamepad2", "right_trigger");
        btn_dpad_up = controllerMap.getButtonMap("Lift:extend_high2", "gamepad2", "dpad_up");
        btn_right_bumper = controllerMap.getButtonMap("lift:deposit", "gamepad2", "right_bumper");

        lift.rotate(Status.ROTATIONS.get("in"));
        robot.lift.moveOutrigger(Status.OUTRIGGERS.get("up"));
    }

    @Override
    public void update(Telemetry telemetry) {
        double delta_extension;

        if (-ax_left_stick_y.get() < -0.1 || -ax_left_stick_y.get() > 0.1){
            if (lift.getLiftTargetPos() < Status.STAGES.get("neutral") + 20000) {
                delta_extension = -ax_left_stick_y.get() * Status.NEUTRAL_SENSITIVITY;
            } else {
                delta_extension = -ax_left_stick_y.get() * Status.SENSITIVITY;
            }
        } else {
            delta_extension = 0;
        }

        lift.extend(lift.getLiftTargetPos() + delta_extension, false);

        if (ax_left_trigger.get() > 0.8){
            outrigger_triggered = false;
        } else {
            outrigger_triggered = true;
        }

        if (!outrigger_triggered){
            drop_outrigger = true;
            outrigger_triggered = true;
        }

        switch (id_lift) {
            case 0:
                if (btn_dpad_down.get()){
                    height = 0;
                } else if (btn_a.get()){
                    height = 2;
                } else if (btn_y.get()){
                    height = 3;
                } else if (btn_x.get()) {
                    height = 4;
                } else if (btn_dpad_up.get()) {
                    height = 5;
                } else if (ax_right_trigger.get() > 0.8){
                    height = 6;
                }

                if (height != -1){
                    intake.deposit(Status.DEPOSITS.get("carry"));
                } else {
                    timer.reset();
                }

                if (timer.seconds() > Status.BUCKET_WAIT_TIME){
                    id_lift += 1;
                }
                break;
            case 1:
                switch (height){
                    case 0:
                        lift.rotate(Status.ROTATIONS.get("in"));
                        break;
                    case 1:
                        lift.rotate(Status.ROTATIONS.get("low_out"));
                        break;
                    case 2:
                        lift.rotate(Status.ROTATIONS.get("mid_out"));
                        break;
                    case 3:
                        lift.rotate(Status.ROTATIONS.get("high_out"));
                        break;
                    case 4:
                        lift.rotate(Status.ROTATIONS.get("neutral_out"));
                        break;
                    case 5:
                        lift.rotate(Status.ROTATIONS.get("high_out2"));
                        break;
                    case 6:
                        lift.rotate(Status.ROTATIONS.get("high_out"));
                        lift.moveOutrigger(Status.OUTRIGGERS.get("down"));
                        break;
                }
            case 2:
                double target_height = lift.getLiftCurrentPos();
                switch (height){
                    case 0:
                        target_height = 0;
                        duck_cycle_flag = false;
                        break;
                    case 1:
                        target_height = Status.STAGES.get("low");
                        break;
                    case 2:
                        target_height = Status.STAGES.get("mid");
                        break;
                    case 3:
                        target_height = Status.STAGES.get("high");
                        break;
                    case 4:
                        target_height = Status.STAGES.get("neutral");
                        break;
                    case 5:
                        target_height = Status.STAGES.get("high2");
                        break;
                    case 6:
                        target_height = Status.STAGES.get("really high");
                        duck_cycle_flag = true;
                        break;
                }
                lift.extend(target_height, true);
                if (lift.ifReached(target_height)){
                    id_lift += 1;
                }
                break;
            case 3:
                height = -1;
                id_lift = 0;
                break;
        }

        if (btn_right_bumper.edge() == -1) { //when the bucket flips in, wait for a delay and then retract
            deposit_timer.reset();
            timer_counting = true;
        }

        if (timer_counting && deposit_timer.seconds() > Status.DEPOSIT_TIMER) {
            if (!intake.freightDetected()) {
                height = 0;
                timer_counting = false;
            }
        }

        lift.updateLift();
        lift.duck_cycle_flag = duck_cycle_flag;

        telemetry.addData("Lift Real Pos: ", lift.getLiftCurrentPos());
        telemetry.addData("Lift Target Pos: ", lift.getLiftTargetPos());
        //telemetry.addData("Lift Id: ", id);

        telemetry.addData("Was Reset", was_reset);
        telemetry.addData("Lift Limit: ", lift.limitPressed());
    }
}
