package org.firstinspires.ftc.teamcode.opmodes.teleop;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.FourBar;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.util.Status;

public class FourBarControl extends ControlModule{
    private FourBar fourbar;

    private ControllerMap.AxisEntry ax_right_stick_y;
    private ControllerMap.ButtonEntry btn_left_dpad;
    private ControllerMap.ButtonEntry btn_down_dpad;
    private ControllerMap.ButtonEntry btn_right_dpad;


    public FourBarControl(String name){super(name);}


    @Override
    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager) {
        this.fourbar = robot.fourbar;
        ax_right_stick_y = controllerMap.getAxisMap("fourbar:right_y", "gamepad2", "right_stick_y");
        btn_left_dpad = controllerMap.getButtonMap("fourbar:left_dropper", "gamepad2", "dpad_left");
        btn_down_dpad = controllerMap.getButtonMap("fourbar:down_dropper", "gamepad2", "dpad_down");
        btn_right_dpad = controllerMap.getButtonMap("fourbar:right_dropper", "gamepad2", "dpad_right");
    }

    @Override
    public void update(Telemetry telemetry) {
        fourbar.rotate(ax_right_stick_y.get() * Status.DELTA_MULTIPLIER);

        if (btn_left_dpad.get()){
            fourbar.dropperExtendLeft();
        } else if (btn_right_dpad.get()){
            fourbar.dropperExtendRight();
        } else {
            fourbar.dropperRetract();
        }

        telemetry.addData("FourBar Pos: ", fourbar.getCurrentArmPos());
        telemetry.addData("Dropper Pos :", fourbar.getCurrentDropperPos());
    }
}
