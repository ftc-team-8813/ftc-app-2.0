package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.util.Logger;
import org.firstinspires.ftc.teamcode.util.Scheduler;
import org.firstinspires.ftc.teamcode.util.event.EventBus;
import org.firstinspires.ftc.teamcode.util.event.LifecycleEvent;
import org.opencv.android.OpenCVLoader;


import static org.firstinspires.ftc.teamcode.util.event.LifecycleEvent.START;

// we going to use the event bus system for this so that everything can be done on one thread
@Autonomous(name="Auto")
public class MainAuto extends LoggingOpMode
{
    private Robot robot;
    private Logger log = new Logger("Autonomous");

    private EventBus bus;
    private Scheduler scheduler;
    
    
    static
    {
        OpenCVLoader.initDebug();
    }
    
    @Override
    public void init()
    {
        super.init();
        robot = Robot.initialize(hardwareMap, "Autonomous");
        bus = robot.eventBus;
        scheduler = robot.scheduler;
    }
    
    @Override
    public void init_loop()
    {
        scheduler.loop();
        bus.update();
    }
    
    @Override
    public void start()
    {
        bus.pushEvent(new LifecycleEvent(START));
    }
    
    @Override
    public void loop()
    {

    }
    
    @Override
    public void stop()
    {
        super.stop();
    }
}
