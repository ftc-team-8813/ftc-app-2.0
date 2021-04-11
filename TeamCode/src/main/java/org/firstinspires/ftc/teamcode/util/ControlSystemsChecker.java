package org.firstinspires.ftc.teamcode.util;

import com.sun.tools.javac.comp.Check;

import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.util.event.Event;
import org.firstinspires.ftc.teamcode.util.event.EventBus;
import org.firstinspires.ftc.teamcode.util.event.EventBus.Subscriber;

import java.util.ArrayList;

public class ControlSystemsChecker {
    Robot robot;
    EventBus ev = new EventBus();
    ArrayList<CheckerCallback> callbacks = new ArrayList<>();
    ArrayList<Object> testValues = new ArrayList<>();
    int checkerCallbackId = 0;


    public ControlSystemsChecker(Robot robot){
        this.robot = robot;
    }

    public interface Task{
        void run();
    }

    public interface CheckerCallback{
        void check();
    }

    public void addSystem(CheckerCallback callback, Object testValue){
        callbacks.add(callback);
        testValues.add(testValue);
    }
}
