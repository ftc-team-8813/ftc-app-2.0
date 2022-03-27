package org.firstinspires.ftc.teamcode.opmodes.teleop;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;

public abstract class ControlModule
{
    public final String name;
    protected boolean disabled = false;

    public ControlModule(String name)
    {
        this.name = name;
    }

    public abstract void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager);

    public void init_loop(Telemetry telemetry) {}

    public abstract void update(Telemetry telemetry);

    public void alwaysUpdate(Telemetry telemetry) {}

    public void stop() {}

    public void disable()
    {
        disabled = true;
    }

    public void enable()
    {
        disabled = false;
    }

    public boolean disabled()
    {
        return disabled;
    }

    public boolean shouldEnable()
    {
        return false;
    }
}
