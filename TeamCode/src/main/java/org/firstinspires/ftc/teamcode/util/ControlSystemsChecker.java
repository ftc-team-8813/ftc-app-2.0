package org.firstinspires.ftc.teamcode.util;

import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.util.event.EventBus;

import java.util.ArrayList;

public class ControlSystemsChecker
{
    Robot robot;
    EventBus ev = new EventBus();
    ArrayList<CheckerCallback> callbacks = new ArrayList<>();
    ArrayList<Object> testValues = new ArrayList<>();
    int checkerCallbackId = 0;
    
    
    public ControlSystemsChecker(Robot robot)
    {
        this.robot = robot;
    }
    
    public interface Task
    {
        void run();
    }
    
    public interface CheckerCallback
    {
        void check();
    }
    
    public void addSystem(CheckerCallback callback, Object testValue)
    {
        callbacks.add(callback);
        testValues.add(testValue);
    }
}
