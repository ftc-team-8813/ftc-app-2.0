package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.google.gson.JsonObject;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.REVHub;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.hardware.Shooter;
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
    private Shooter shooter;
    private REVHub controlHub;
    
    private ControllerMap.AxisEntry ax_turret;
    private ControllerMap.ButtonEntry btn_turret_home;
    private ControllerMap.ButtonEntry btn_turret_reverse;
    private ControllerMap.ButtonEntry btn_turret_slow;
    
    private double turretAdjSpeed;
    private boolean manualDrive;
    
    @Override
    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager)
    {
        turret = robot.turret;
        shooter = robot.turret.shooter;
        controlHub = robot.controlHub;
        
        ax_turret = controllerMap.getAxisMap("turret::turret", "gamepad2", "left_stick_x");
        btn_turret_home = controllerMap.getButtonMap("turret::home", "gamepad2", "a");
        btn_turret_reverse = controllerMap.getButtonMap("turret::reverse", "gamepad2", "left_trigger");
        btn_turret_slow = controllerMap.getButtonMap("turret::slow", "gamepad2", "left_stick_button");
        
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
        double power = -ax_turret.get();
        if (power != 0)
        {
            if (!manualDrive)
            {
                manualDrive = true;
            }
            
            double turretSpeed;
            if (btn_turret_slow.get() || shooter.getMaxPower() == shooter.getPower(2))
            {
                turretSpeed = 0.2;
            }
            else
            {
                turretSpeed = 0.325;
            }
            
            double pos = turret.getPosition();
            if (pos <= 0 && power < 0) turret.turret.setPower(0);
            else if (pos >= 1 && power > 0) turret.turret.setPower(0);
            else turret.turret.setPower(power * -turretSpeed);
        }
        else
        {
            if (manualDrive)
            {
                manualDrive = false;
                turret.rotate(turret.getPosition());
            }
        }
        
        if (btn_turret_home.edge() > 0)
        {
            turret.home();
            shooter.setPreset(0);
            controlHub.setLEDColor(shooter.getPresetColor());
            
        }
        
        if (btn_turret_reverse.edge() > 0)
        {
            turret.rotate(turret.getTurretShootPos());
            shooter.setPreset(1);
            controlHub.setLEDColor(shooter.getPresetColor());
        }
    }
    
    @Override
    public void disable()
    {
        super.disable();
        manualDrive = false;
    }
    
    @Override
    public void alwaysUpdate(Telemetry telemetry)
    {
        if (!manualDrive)
            turret.update(telemetry);
    }
    
    @Override
    public boolean shouldEnable()
    {
        return Math.abs(ax_turret.get()) > 0.2 || btn_turret_home.get() || btn_turret_reverse.get();
    }
}
