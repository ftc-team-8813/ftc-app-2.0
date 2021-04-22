package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.hardware.lynx.LynxController;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.hardware.lynx.LynxNackException;
import com.qualcomm.hardware.lynx.commands.core.LynxGetBulkInputDataCommand;
import com.qualcomm.hardware.lynx.commands.core.LynxGetBulkInputDataResponse;
import com.qualcomm.robotcore.hardware.Blinker;

import org.firstinspires.ftc.teamcode.util.Logger;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class REVHub
{
    private LynxModule hub;
    private LynxGetBulkInputDataResponse bulkData;
    private Logger log;
    private List<LynxController> hubControllers = new ArrayList<>();
    private int currentColor;
    
    public REVHub(LynxModule hub)
    {
        this.hub = hub;
        log = new Logger(String.format("REVHub [%s]", hub.getConnectionInfo()));
        
        try
        {
            Field f = hub.getClass().getDeclaredField("controllers");
            f.setAccessible(true);
            hubControllers = (List<LynxController>) (f.get(hub));
        }
        catch (NoSuchFieldException | IllegalAccessException e)
        {
            log.e(e);
        }
    }
    
    public int getAddress()
    {
        return hub.getModuleAddress();
    }
    
    public void setLEDColor(int color)
    {
        hub.setConstant(color);
        currentColor = color;
    }
    
    public int getLEDColor()
    {
        return currentColor;
    }
    
    public void setLEDPattern(List<Blinker.Step> steps)
    {
        hub.setPattern(steps);
    }
    
    public void updateBulkData()
    {
        LynxGetBulkInputDataCommand cmd = new LynxGetBulkInputDataCommand(hub);
        try
        {
            bulkData = cmd.sendReceive();
        }
        catch (InterruptedException | LynxNackException e)
        {
            log.w(e.getClass().getName() + ":");
            log.w(e.getMessage());
        }
    }
    
    public boolean hasController(LynxController controller)
    {
        for (LynxController c : hubControllers)
        {
            if (c == controller) return true;
        }
        return false;
    }
    
    public LynxGetBulkInputDataResponse getBulkData()
    {
        return bulkData;
    }
}
