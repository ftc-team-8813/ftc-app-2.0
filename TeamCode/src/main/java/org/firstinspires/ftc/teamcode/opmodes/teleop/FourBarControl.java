package org.firstinspires.ftc.teamcode.opmodes.teleop;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.FourBar;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.util.Status;

public class FourBarControl extends ControlModule{
    private FourBar fourbar;

    private ControllerMap.AxisEntry ax_right_stick_y;


    public FourBarControl(String name){super(name);}


    @Override
    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager) {
        this.fourbar = robot.fourbar;
        ax_right_stick_y = controllerMap.getAxisMap("fourbar:right_y", "gamepad2", "right_stick_y");
    }

    @Override
    public void update(Telemetry telemetry) {
        fourbar.rotate(ax_right_stick_y.get() * Status.DELTA_MULTIPLIER);
    }
}
