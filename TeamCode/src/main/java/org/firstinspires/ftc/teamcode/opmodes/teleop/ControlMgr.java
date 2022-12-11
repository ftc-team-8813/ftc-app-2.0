package org.firstinspires.ftc.teamcode.opmodes.teleop;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.util.Logger;
import org.firstinspires.ftc.teamcode.util.Time;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ControlMgr
{
    private List<ControlModule> modules;
    private Robot robot;
    private ControllerMap controllerMap;
    private Logger log = new Logger("Control Modules");
    
    public ControlMgr(Robot robot, ControllerMap controllerMap)
    {
        modules = new ArrayList<>();
        this.robot = robot;
        this.controllerMap = controllerMap;
    }
    
    public ControlModule addModule(ControlModule module)
    {
        log.v("Add module: %s (%s)", module.name, module.getClass().getName());
        modules.add(module);
        return module;
    }
    
    public List<ControlModule> getModules()
    {
        return Collections.unmodifiableList(modules);
    }
    
    public <T extends ControlModule> List<T> getModules(Class<T> type)
    {
        log.v("Find modules of type %s", type.getSimpleName());
        List<T> selected = new ArrayList<>();
        for (ControlModule module : modules)
        {
            if (module.getClass().isAssignableFrom(type)) selected.add(type.cast(module));
        }
        return selected;
    }
    
    public void initModules()
    {
        log.d("Initializing modules:");
        for (ControlModule module : modules)
        {
            double start = Time.now();
            module.initialize(robot, controllerMap, this);
            double time = Time.since(start);
            
            log.d("%s: initialized in %.2f ms", module.name, time * 1000.0);
        }
    }
    
    public void init_loop(Telemetry telemetry)
    {
        for (ControlModule module : modules)
        {
            if (!module.disabled) module.init_loop(telemetry);
        }
    }
    
    public void loop(Telemetry telemetry) throws InterruptedException {
        for (ControlModule module : modules)
        {
            if (!module.disabled) module.update(telemetry);
            module.alwaysUpdate(telemetry);
        }
    }
    
    public void stop()
    {
        for (ControlModule module : modules)
        {
            module.stop();
        }
    }
}
