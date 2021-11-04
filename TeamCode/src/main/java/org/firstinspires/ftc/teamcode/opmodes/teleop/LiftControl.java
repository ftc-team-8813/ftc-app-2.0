package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Lift;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.util.Status;

public class LiftControl extends ControlModule{
    private Lift lift;
    private final ElapsedTime timer = new ElapsedTime();

    private int bottom = 0; // 0 = Bottom, 1 = Going to Bottom
    private int height = 0; // 1 = Low, 2 = Mid, 3 = High
    private int extension = 0; // 0 = Center, 1 = Left, 2 = Right
    private final double arm_wait_time = 1.3;
    private boolean moving = false;

    private ControllerMap.ButtonEntry btn_down_dpad;
    private ControllerMap.ButtonEntry btn_left_dpad;
    private ControllerMap.ButtonEntry btn_a;
    private ControllerMap.ButtonEntry btn_b;
    private ControllerMap.ButtonEntry btn_y;
    private ControllerMap.ButtonEntry btn_right_bumper;
    private ControllerMap.ButtonEntry btn_right_dpad;


    public LiftControl(String name){super(name);}


    @Override
    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager) {
        this.lift = robot.lift;
        btn_down_dpad = controllerMap.getButtonMap("lift:reset", "gamepad2", "dpad_down");
        btn_left_dpad = controllerMap.getButtonMap("lift:extend_left", "gamepad2", "dpad_left");
        btn_right_dpad = controllerMap.getButtonMap("lift:extend_right", "gamepad2", "dpad_right");

        btn_a = controllerMap.getButtonMap("lift:low", "gamepad2", "a");
        btn_b = controllerMap.getButtonMap("lift:mid", "gamepad2", "b");
        btn_y = controllerMap.getButtonMap("lift:high", "gamepad2", "y");

        btn_right_bumper = controllerMap.getButtonMap("lift:deposit", "gamepad2", "right_bumper");
    }

    @Override
    public void update(Telemetry telemetry) {
        if (btn_a.get()){
            height = 1;
        } else if (btn_b.get()){
            height = 2;
        } else if (btn_y.get()){
            height = 3;
        }

        if (height > 0) {
            if (btn_left_dpad.get()) {
                extension = 1;
            } else if (btn_right_dpad.get()) {
                extension = 2;
            }
        }

        if (btn_right_bumper.get()){
            switch (extension){
                case 1:
                    lift.deposit(Status.DEPOSITS.get("left"));
                case 2:
                    lift.deposit(Status.DEPOSITS.get("right"));
            }
        } else {
            lift.deposit(Status.DEPOSITS.get("center"));
        }

        if (btn_down_dpad.get()){
            bottom = 1;
            extension = 0;
        }

        if (bottom == 1){
            if (moving){
                if (timer.seconds() > arm_wait_time){
                    lift.raise(0);
                    bottom = 0;
                    moving = false;
                }
            } else {
                lift.extend(Status.EXTENSIONS.get("center"));
                timer.reset();
                moving = true;
            }
        }

        if (extension > 0){
            switch (height){
                case 1:
                    lift.raise(Status.STAGES.get("low"));
                    break;
                case 2:
                    lift.raise(Status.STAGES.get("mid"));
                    break;
                case 3:
                    lift.raise(Status.STAGES.get("high"));
                    break;
            }
            if (lift.liftReached()){
                switch (extension){
                    case 1:
                        lift.extend(Status.EXTENSIONS.get("left"));
                        break;
                    case 2:
                        lift.extend(Status.EXTENSIONS.get("right"));
                        break;
                }
            }
        }

        lift.updateLift();
        telemetry.addData("Height: ", height);
        telemetry.addData("Extension: ", extension);
        telemetry.addData("Lift Real Pos: ", lift.getCurrentLiftPos());
        telemetry.addData("Lift Target Pos: ", lift.getTargetLiftPos());
        telemetry.addData("Arm Pos :", lift.getCurrentArmPos());
    }
}
