package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Blocker;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;

public class BlockerControl extends ControlModule
{
    private ControllerMap.ButtonEntry btn_blocker_up;
    private ControllerMap.ButtonEntry btn_blocker_down;
    private Blocker blocker;
    
    public BlockerControl()
    {
        super("Blocker Control");
    }
    
    @Override
    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager)
    {
        this.blocker = robot.blocker;
        
        btn_blocker_up   = controllerMap.getButtonMap("blocker::up", "gamepad2", "right_stick_y_neg");
        btn_blocker_down = controllerMap.getButtonMap("blocker::down", "gamepad2", "right_stick_y_pos");
        blocker.up();
    }
    
    @Override
    public void update(Telemetry telemetry)
    {
        if (btn_blocker_up.get())
        {
            blocker.up();
        }
        else if (btn_blocker_down.get())
        {
            blocker.down();
        }
    }
    
    @Override
    public void disable()
    {
        blocker.up(); // safety
        super.disable();
    }
    
    @Override
    public boolean shouldEnable()
    {
        return btn_blocker_up.get() || btn_blocker_down.get();
    }
}
