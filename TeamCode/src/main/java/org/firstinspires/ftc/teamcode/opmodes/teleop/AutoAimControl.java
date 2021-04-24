package org.firstinspires.ftc.teamcode.opmodes.teleop;

import android.graphics.Bitmap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.REVHub;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.hardware.Turret;
import org.firstinspires.ftc.teamcode.hardware.autoshoot.AutoAim;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.util.Logger;
import org.firstinspires.ftc.teamcode.util.websocket.InetSocketServer;
import org.firstinspires.ftc.teamcode.util.websocket.Server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

public class AutoAimControl extends ControlModule
{
    public AutoAimControl()
    {
        super("Auto Aim Control");
    }
    
    // takes control from TurretControl if necessary
    private TurretControl turretControl;
    private Logger log;
    
    private Turret turret;
    private AutoAim autoAim;
    private REVHub hub;

    private Bitmap serverFrame;
    
    private ControllerMap.ButtonEntry btn_auto_toggle;
    private ControllerMap.AxisEntry ax_adjust;
    private boolean auto_aim_enabled = false;
    private boolean prevEnabled = false;
    
    @Override
    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager)
    {
        turret = robot.turret;
        log = new Logger("Auto Aim Control");
        hub = robot.controlHub;
        autoAim = new AutoAim();
        
        btn_auto_toggle = controllerMap.getButtonMap("auto_aim::toggle", "gamepad2", "right_trigger");
        ax_adjust = controllerMap.getAxisMap("turret::turret", "gamepad2", "left_stick_x");
        
        List<TurretControl> list = manager.getModules(TurretControl.class);
        if (list.size() > 1) throw new IllegalStateException("Multiple turret control modules?!");
        else if (list.size() == 1) turretControl = list.get(0);
        else turretControl = null;
    }
    
    @Override
    public void update(Telemetry telemetry)
    {
        if(btn_auto_toggle.get()){
            auto_aim_enabled = true;
            if (prevEnabled != auto_aim_enabled){
                turretControl.disable();
                autoAim.detectStage = 1;
            }
            prevEnabled = true;
        } else{
            if (!auto_aim_enabled){
                turretControl.enable();
                prevEnabled = false;
            }
            auto_aim_enabled = false;
        }

        if (auto_aim_enabled != prevEnabled)
        {
            if (auto_aim_enabled) hub.setLEDColor(0xFF0000);
            else hub.setLEDColor(0x00FFFF);
        }

        if (auto_aim_enabled) {
            double turn = autoAim.getTurretRotationVis(telemetry);
            turret.rotate(turret.getPosition() + turn);
        }
    }

    @Override
    public void alwaysUpdate(Telemetry telemetry) {
        if (auto_aim_enabled) {
            turret.update(telemetry);
        }
    }

    @Override
    public boolean shouldEnable()
    {
        return btn_auto_toggle.get();
    }
}
