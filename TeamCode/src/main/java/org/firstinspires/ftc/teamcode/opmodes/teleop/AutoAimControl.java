package org.firstinspires.ftc.teamcode.opmodes.teleop;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.hardware.Turret;
import org.firstinspires.ftc.teamcode.hardware.autoshoot.AutoAim;
import org.firstinspires.ftc.teamcode.input.ControllerMap;

import java.util.List;

public class AutoAimControl extends ControlModule
{
    public AutoAimControl()
    {
        super("Auto Aim Control");
    }
    
    // takes control from TurretControl if necessary
    private TurretControl turretControl;
    
    private Turret turret;
    private AutoAim autoAim;
    
    private ControllerMap.ButtonEntry btn_auto_toggle;
    private boolean auto_aim_enabled = false;
    
    @Override
    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager)
    {
        turret = robot.turret;
        autoAim = new AutoAim(robot.drivetrain.getOdometry(), robot.turret.getTurretHome());
        autoAim.setTarget(30, 0);
        
        btn_auto_toggle = controllerMap.getButtonMap("auto_aim::toggle", "gamepad2", "right_trigger");
    
        List<TurretControl> list = manager.getModules(TurretControl.class);
        if (list.size() > 1) throw new IllegalStateException("Multiple turret control modules?!");
        else if (list.size() == 1) turretControl = list.get(0);
        else turretControl = null;
    }
    
    @Override
    public void update(Telemetry telemetry)
    {
        if (btn_auto_toggle.edge() > 0)
        {
            auto_aim_enabled = !auto_aim_enabled;
            if (turretControl != null)
            {
                if (auto_aim_enabled) turretControl.disable();
                else turretControl.enable();
            }
        }
        
        if (auto_aim_enabled)
        {
            if (turretControl != null && turretControl.shouldEnable())
            {
                auto_aim_enabled = false;
                turretControl.enable();
                return;
            }
            
            telemetry.addData("Auto Aim", "ENGAGED");
            turret.rotate(autoAim.getTurretRotation(telemetry));
        }
        else
        {
            telemetry.addData("Auto Aim", "DISENGAGED");
        }
    }
    
    @Override
    public boolean shouldEnable()
    {
        return btn_auto_toggle.get();
    }
}
