package org.firstinspires.ftc.teamcode.opmodes.teleop;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.FourBar;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.util.Status;

public class FourBarControl extends ControlModule{
    private FourBar fourbar;

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


    public FourBarControl(String name){super(name);}


    @Override
    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager) {
        this.fourbar = robot.fourbar;
        this.fourbar.dropperReset();

        ax_right_stick_x = controllerMap.getAxisMap("fourbar:right_x", "gamepad2", "right_stick_x");

        btn_down_dpad = controllerMap.getButtonMap("fourbar:left_low", "gamepad2", "dpad_down");
        btn_left_dpad = controllerMap.getButtonMap("fourbar:left_mid", "gamepad2", "dpad_left");
        btn_up_dpad = controllerMap.getButtonMap("fourbar:left_high", "gamepad2", "dpad_up");
        btn_left_bumper = controllerMap.getButtonMap("fourbar:left_dropper", "gamepad2", "left_bumper");

        btn_a = controllerMap.getButtonMap("fourbar:right_low", "gamepad2", "a");
        btn_b = controllerMap.getButtonMap("fourbar:right_mid", "gamepad2", "b");
        btn_y = controllerMap.getButtonMap("fourbar:right_high", "gamepad2", "y");
        btn_right_bumper = controllerMap.getButtonMap("fourbar:right_dropper", "gamepad2", "right_bumper");

        btn_x = controllerMap.getButtonMap("fourbar:reset_letter", "gamepad2", "x");
        btn_right_dpad = controllerMap.getButtonMap("fourbar:reset_dpad", "gamepad2", "dpad_right");
    }

    @Override
    public void update(Telemetry telemetry) {
        if (ax_right_stick_x.get() != 0) {
            double target_ticks = fourbar.getCurrentArmPos() + (-ax_right_stick_x.get() * Status.DELTA_MULTIPLIER);
            fourbar.rotate(target_ticks);
            fourbar.manual = true;
        } else {
            fourbar.manual = false;
        }

        if (btn_down_dpad.get()){
            fourbar.rotate(-Status.STAGES.get("low"));
        } else if (btn_left_dpad.get()){
            fourbar.rotate(-Status.STAGES.get("mid"));
        } else if (btn_up_dpad.get()){
            fourbar.rotate(-Status.STAGES.get("high"));
        }
        if (btn_left_bumper.get()){
            fourbar.dropperExtendLeft();
        }

        if (btn_a.get()){
            fourbar.rotate(Status.STAGES.get("low"));
        } else if (btn_b.get()){
            fourbar.rotate(Status.STAGES.get("mid"));
        } else if (btn_y.get()){
            fourbar.rotate(Status.STAGES.get("high"));
        }
        if (btn_right_bumper.get()){
            fourbar.dropperExtendRight();
        }

        if (btn_x.get() || btn_right_dpad.get()){
            fourbar.rotate(0);
            fourbar.dropperReset();
        }


        fourbar.update();
        telemetry.addData("FourBar Real Pos: ", fourbar.getCurrentArmPos());
        telemetry.addData("Four Bar Target Pos: ", fourbar.getTargetArmPos());
        telemetry.addData("Dropper Pos :", fourbar.getCurrentDropperPos());
    }
}
