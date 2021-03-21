package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.hardware.lynx.LynxController;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.hardware.Blinker;

import java.util.List;

public class REVHub
{
    private LynxModule hub;
    
    public REVHub(LynxModule hub)
    {
        this.hub = hub;
    }
    
    public int getAddress()
    {
        return hub.getModuleAddress();
    }
    
    public void setLEDColor(int color)
    {
        hub.setConstant(color);
    }
    
    public void setLEDPattern(List<Blinker.Step> steps)
    {
        hub.setPattern(steps);
    }
}
