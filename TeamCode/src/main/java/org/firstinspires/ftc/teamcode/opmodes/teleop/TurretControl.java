package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.google.gson.JsonObject;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.hardware.Turret;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.util.Persistent;

public class TurretControl extends ControlModule
{
    
    public TurretControl()
    {
        super("Turret Control");
    }
    
    private Turret turret;
    
    private ControllerMap.AxisEntry   ax_turret;
    private ControllerMap.ButtonEntry btn_turret_home;
    private ControllerMap.ButtonEntry btn_turret_reverse;
    
    private double turretAdjSpeed;
    
    @Override
    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager)
    {
        turret = robot.turret;
        
        ax_turret          = controllerMap.getAxisMap  ("turret::turret",  "gamepad2", "left_stick_x");
        btn_turret_home    = controllerMap.getButtonMap("turret::home",    "gamepad2", "a");
        btn_turret_reverse = controllerMap.getButtonMap("turret::reverse", "gamepad2", "left_trigger");
        
        JsonObject config = robot.config.getAsJsonObject("teleop");
        turretAdjSpeed = config.get("turret_adj_rate").getAsDouble();
        
        if (Persistent.get("turret_zero_found") == null)
            robot.turret.startZeroFind();
    }
    
    @Override
    public void init_loop(Telemetry telemetry)
    {
        turret.updateInit(telemetry);
    }
    
    @Override
    public void update(Telemetry telemetry)
    {
        double turret_adj = -ax_turret.get() * turretAdjSpeed;
        turret.rotate(turret.getTarget() + turret_adj);
        
        if (btn_turret_home.edge() > 0)
        {
            turret.rotate(turret.getTurretHome());
        }
        
        if (btn_turret_reverse.edge() > 0)
        {
            turret.rotate(turret.getTurretShootPos());
        }
    }
    
    @Override
    public void alwaysUpdate(Telemetry telemetry)
    {
        turret.update(telemetry);
    }
    
    @Override
    public boolean shouldEnable()
    {
        return Math.abs(ax_turret.get()) > 0.2 || btn_turret_home.get() || btn_turret_reverse.get();
    }
}
