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
    private ControllerMap.AxisEntry ax_left_trigger;
    private ControllerMap.AxisEntry ax_right_trigger;

    private int height = -1; // 0 = bottom, 1 = low, 2 = mid, 3 = high, 4 = neutral, 5 = high2 (goal tipped), 6 = really high (duck cycling)
    private ElapsedTime timer;
    private int id = 0;
    private int id2 = 0;
    private final boolean was_reset = false;
    private boolean left_trigger_was_pressed = false;
    private final double var_pit_stop_wait = Status.PITSTOP_WAIT_TIME;

    private Logger log;


    public LiftControl(String name){super(name);}

    @Override
    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager) {
        this.lift = robot.lift;
        this.intake = robot.intake;
        timer = new ElapsedTime();
        log = new Logger("Lift Control");

        ax_left_stick_y = controllerMap.getAxisMap("lift:adjust", "gamepad2", "left_stick_y");
        btn_y = controllerMap.getButtonMap("lift:extend_high", "gamepad2", "y");
        btn_a = controllerMap.getButtonMap("lift:extend_low", "gamepad2", "a");
        btn_x = controllerMap.getButtonMap("lift:extend_neutral", "gamepad2", "x");
        btn_dpad_down = controllerMap.getButtonMap("lift:home", "gamepad2", "dpad_down");
        ax_left_trigger = controllerMap.getAxisMap("lift:reset", "gamepad2", "left_trigger");
        ax_right_trigger = controllerMap.getAxisMap("lift:extend_really_high", "gamepad2", "right_trigger");
        btn_dpad_up = controllerMap.getButtonMap("Lift:extend_high2", "gamepad2", "dpad_up");

        lift.rotate(Status.ROTATIONS.get("in"));
    }

    @Override
    public void update(Telemetry telemetry) {
        double delta_extension;

        if (-ax_left_stick_y.get() < -0.1 || -ax_left_stick_y.get() > 0.1){
            if (lift.getLiftTargetPos() < Status.STAGES.get("neutral") + 20000) {
                delta_extension = -ax_left_stick_y.get()*Status.NEUTRAL_SENSITIVITY;
            } else {
                delta_extension = -ax_left_stick_y.get() * Status.SENSITIVITY;
            }
        } else {
            delta_extension = 0;
        }

        lift.extend(lift.getLiftTargetPos() + delta_extension, false);

        if (ax_left_trigger.get() > 0.8){
            //lift.resetLitTarget();
            if (!left_trigger_was_pressed){
               id2+=1;
            }
            if(id2 > 1){ id2 = 0; }
            switch (id2) {
                case 0:
                    lift.moveOutrigger(Status.OUTRIGGERS.get("up"));
                    break;
                case 1:
                    lift.moveOutrigger(Status.OUTRIGGERS.get("down"));
                    break;
            }
            left_trigger_was_pressed = true;
        }
        else { left_trigger_was_pressed = false; }

        switch (id) {
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
                    log.i("Carrying Deposit");
                    id += 1;
                }
                break;
            case 1:
                lift.extend(Status.STAGES.get("pitstop"), true);
                if (lift.ifReached(Status.STAGES.get("pitstop"))){
                    log.i("Reached Pitstop");
                    id += 1;
                }

                timer.reset();
                break;
            case 2:
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
                if (height == 0) {
                    if (timer.seconds() > Status.PITSTOP_WAIT_TIME) {
                        id += 1;
                        log.i("Rotated Arm");
                    }
                } else {
                    if (timer.seconds() > Status.PITSTOP_WAIT_TIME_OUT) {
                        id += 1;
                        log.i("Rotated Arm");
                    }
                }
                break;
            case 3:
                double target_height = lift.getLiftCurrentPos();
                switch (height){
                    case 0:
                        target_height = 0;
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
                        break;
                }
                lift.extend(target_height, true);
                if (lift.ifReached(target_height)){
                    log.i("Reached Height");
                    id += 1;
                }
                break;
            case 4:
                height = -1;
                id = 0;
                break;
        }

        lift.updateLift();



        telemetry.addData("Lift Real Pos: ", lift.getLiftCurrentPos());
        telemetry.addData("Lift Target Pos: ", lift.getLiftTargetPos());
        //telemetry.addData("Lift Id: ", id);

        telemetry.addData("Was Reset", was_reset);
        telemetry.addData("Lift Limit: ", lift.limitPressed());
    }
}
