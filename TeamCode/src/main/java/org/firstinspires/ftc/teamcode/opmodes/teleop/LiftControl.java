package org.firstinspires.ftc.teamcode.opmodes.teleop;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Lift;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.util.Status;

public class LiftControl extends ControlModule{
    private Lift lift;

    private int height = 0; // 0 = Lowest, 1 = Low, 2 = Mid, 3 = High
    private int extension = 0; // 0 = Center, 1 = Left, 2 = Right

    private ControllerMap.AxisEntry ax_right_stick_x;
    private ControllerMap.ButtonEntry btn_down_dpad;
    private ControllerMap.ButtonEntry btn_left_dpad;
    private ControllerMap.ButtonEntry btn_up_dpad;
    private ControllerMap.ButtonEntry btn_left_bumper;
    private ControllerMap.ButtonEntry btn_a;
    private ControllerMap.ButtonEntry btn_b;
    private ControllerMap.ButtonEntry btn_y;
    private ControllerMap.ButtonEntry btn_right_bumper;
    private ControllerMap.ButtonEntry btn_x;
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

        if (btn_left_dpad.get()){
            extension = 1;
        } else if (btn_right_dpad.get()){
            extension = 2;
        }

        if (btn_right_bumper.get()){
            switch (extension){
                case 1:
                    lift.extend(Status.DEPOSITS.get("left"));
                case 2:
                    lift.extend(Status.DEPOSITS.get("right"));
            }
            lift.extend(Status.DEPOSITS.get("center"));
        }

        if (btn_down_dpad.get()){
            height = 0;
            extension = 0;
        }

        if (height == 0){
            lift.extend(Status.EXTENSIONS.get("center"));
            if (lift.armReached()){
                lift.raise(0);
            }
        } else if (height > 0){
            if (extension > 0){
                switch (height){
                    case 1:
                        lift.raise(Status.STAGES.get("low"));
                    case 2:
                        lift.raise(Status.STAGES.get("mid"));
                    case 3:
                        lift.raise(Status.STAGES.get("high"));
                }
                if (lift.liftReached()){
                    switch (extension){
                        case 1:
                            lift.extend(Status.EXTENSIONS.get("left"));
                        case 2:
                            lift.extend(Status.EXTENSIONS.get("right"));
                    }
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
