package org.firstinspires.ftc.teamcode.opmodes.teleop;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.hardware.Wobble;
import org.firstinspires.ftc.teamcode.input.ControllerMap;

public class WobbleControl extends ControlModule
{
    
    public WobbleControl()
    {
        super("Wobble Control");
    }
    
    private Wobble wobble;
    
    private ControllerMap.ButtonEntry btn_wobble_up;
    private ControllerMap.ButtonEntry btn_wobble_down;
    private ControllerMap.ButtonEntry btn_wobble_open;
    private ControllerMap.ButtonEntry btn_wobble_close;
    private ControllerMap.ButtonEntry btn_wobble_mid;
    
    @Override
    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager)
    {
        wobble = robot.wobble;
        wobble.up();
        
        btn_wobble_up    = controllerMap.getButtonMap("wobble::up",    "gamepad2", "dpad_up");
        btn_wobble_down  = controllerMap.getButtonMap("wobble::down",  "gamepad2", "dpad_down");
        btn_wobble_open  = controllerMap.getButtonMap("wobble::open",  "gamepad2", "dpad_left");
        btn_wobble_close = controllerMap.getButtonMap("wobble::close", "gamepad2", "dpad_right");
        btn_wobble_mid   = controllerMap.getButtonMap("wobble::mid",   "gamepad2", "left_bumper");
    }
    
    @Override
    public void update(Telemetry telemetry)
    {
        if (btn_wobble_up.get())    wobble.up();
        if (btn_wobble_down.get())  wobble.down();
        if (btn_wobble_open.get())  wobble.open();
        if (btn_wobble_close.get()) wobble.close();
        if (btn_wobble_mid.get())   wobble.middle();
    }
    
    @Override
    public boolean shouldEnable()
    {
        return btn_wobble_up.get()
                || btn_wobble_down.get()
                || btn_wobble_open.get()
                || btn_wobble_close.get()
                || btn_wobble_mid.get();
    }
}
