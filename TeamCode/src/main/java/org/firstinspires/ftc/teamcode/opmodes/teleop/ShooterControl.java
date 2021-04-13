package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.REVHub;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.hardware.Shooter;
import org.firstinspires.ftc.teamcode.input.ControllerMap;

public class ShooterControl extends ControlModule
{
    
    public ShooterControl()
    {
        super("Shooter Control");
    }
    
    private Shooter shooter;
    private REVHub controlHub;
    
    private ControllerMap.ButtonEntry btn_shooter;
    private ControllerMap.ButtonEntry btn_shooter_preset;
    private ControllerMap.ButtonEntry btn_shooter_preset2;
    
    @Override
    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager)
    {
        shooter = robot.turret.shooter;
        shooter.setPreset(0);
        controlHub = robot.controlHub;
        
        controlHub.setLEDColor(shooter.getPresetColor());
        
        btn_shooter        = controllerMap.getButtonMap("shooter::shoot",  "gamepad2", "y");
        btn_shooter_preset = controllerMap.getButtonMap("shooter::preset", "gamepad2", "right_bumper");
        btn_shooter_preset2= controllerMap.getButtonMap("shooter::preset2","gamepad2", "left_trigger");
    }
    
    @Override
    public void update(Telemetry telemetry)
    {
        if (btn_shooter.edge() > 0)
        {
            if (!shooter.running()) shooter.start();
            else                    shooter.stop();
        }
        
        if (btn_shooter_preset.edge() > 0)
        {
            shooter.setPreset(shooter.getCurrPreset() + 1);
            controlHub.setLEDColor(shooter.getPresetColor());
        }
        if (btn_shooter_preset2.edge() > 0)
        {
            shooter.setPreset(1);
            controlHub.setLEDColor(shooter.getPresetColor());
        }
    }
    
    @Override
    public void alwaysUpdate(Telemetry telemetry)
    {
        telemetry.addData("Shooter Velocity (ticks/s)", "%.3f", ((DcMotorEx)shooter.motor).getVelocity());
        telemetry.addData("Shooter Power", "%.3f", shooter.motor.getPower());
        telemetry.addData("Shooter Preset", shooter.getCurrPreset());
    }
    
    @Override
    public boolean shouldEnable()
    {
        return btn_shooter.get();
    }
}
