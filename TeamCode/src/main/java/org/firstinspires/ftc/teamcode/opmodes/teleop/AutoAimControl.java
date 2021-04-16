package org.firstinspires.ftc.teamcode.opmodes.teleop;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.REVHub;
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
    private REVHub hub;
    
    private ControllerMap.ButtonEntry btn_auto_toggle;
    private ControllerMap.AxisEntry ax_auto_adjust_y;
    private ControllerMap.AxisEntry ax_auto_adjust_x;

    private final double SENS = 0.1;
    private boolean auto_aim_enabled = false;
    
    @Override
    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager)
    {
        turret = robot.turret;
        hub = robot.controlHub;
        autoAim = new AutoAim(robot.drivetrain.getOdometry(), robot.turret.getTurretHome());
        autoAim.setTarget(-140, 0);
        
        btn_auto_toggle = controllerMap.getButtonMap("auto_aim::toggle", "gamepad2", "right_trigger");
        ax_auto_adjust_y = controllerMap.getAxisMap("auto_aim::adjust_y", "gamepad2", "left_stick_x");
        ax_auto_adjust_x = controllerMap.getAxisMap("auto_aim::adjust_x", "gamepad2", "left_stick_y");

    
        List<TurretControl> list = manager.getModules(TurretControl.class);
        if (list.size() > 1) throw new IllegalStateException("Multiple turret control modules?!");
        else if (list.size() == 1) turretControl = list.get(0);
        else turretControl = null;
    }
    
    @Override
    public void update(Telemetry telemetry)
    {
        boolean prevEnabled = auto_aim_enabled;
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
            /*if (turretControl != null && turretControl.shouldEnable())
            {
                auto_aim_enabled = false;
                turretControl.enable();
            }
            else*/
            {
                telemetry.addData("Auto Aim", "ENGAGED");
                autoAim.setTarget(autoAim.getTargetX() + -ax_auto_adjust_x.get() * SENS, autoAim.getTargetY() + ax_auto_adjust_y.get() * SENS);
                turret.rotate(autoAim.getTurretRotation(telemetry));
            }
        }
        else
        {
            telemetry.addData("Auto Aim", "DISENGAGED");
        }
        
        if (auto_aim_enabled != prevEnabled)
        {
            if (auto_aim_enabled) hub.setLEDColor(0xFF0000);
            else hub.setLEDColor(0x00FFFF);
        }
    }
    
    @Override
    public boolean shouldEnable()
    {
        return btn_auto_toggle.get();
    }
}
