package org.firstinspires.ftc.teamcode.opmodes.util;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.hardware.LineFinder;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.opmodes.LoggingOpMode;
import org.firstinspires.ftc.teamcode.opmodes.teleop.ControlMgr;
import org.firstinspires.ftc.teamcode.opmodes.teleop.DriveControl;
import org.firstinspires.ftc.teamcode.opmodes.teleop.LiftControl;
import org.firstinspires.ftc.teamcode.opmodes.teleop.ServerControl;
import org.firstinspires.ftc.teamcode.util.Logger;
import org.firstinspires.ftc.teamcode.util.Persistent;
import org.firstinspires.ftc.teamcode.util.Scheduler;
import org.firstinspires.ftc.teamcode.util.Status;
import org.firstinspires.ftc.teamcode.util.Storage;
import org.firstinspires.ftc.teamcode.util.event.EventBus;
import org.firstinspires.ftc.teamcode.util.webserver.WebHost;
import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.android.OpenCVLoader;

import java.io.File;

@TeleOp(name = "PIDTuner")
@Disabled
public class PIDTuner extends LoggingOpMode
{
    // Robot and Controller Vars
    private Robot robot;
    private ControllerMap controllerMap;
    private ControlMgr controlMgr;

    private EventBus evBus;
    private Scheduler scheduler;
    static
    {
        OpenCVLoader.initDebug();
    }

    @Override
    public void init()
    {
        super.init();
        robot = Robot.initialize(hardwareMap);
        evBus = robot.eventBus;
        scheduler = robot.scheduler;

        controllerMap = new ControllerMap(gamepad1, gamepad2, evBus);

        controlMgr = new ControlMgr(robot, controllerMap);

        // Controller Modules
        controlMgr.addModule(new LiftControl("Lift Control"));

        controlMgr.initModules();
    }

    @Override
    public void init_loop()
    {
        controlMgr.init_loop(telemetry);
    }

    @Override
    public void start()
    {
        File pid_values = Storage.getFile("pid_values.json");
        try {
            JSONObject reader = new JSONObject(pid_values.toString());
            Status.LIFT_KP = (double) reader.get("lift_kp");
            Status.PIVOT_KP = (double) reader.get("pivot_kp");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Persistent.clear();
    }

    @Override
    public void loop()
    {
        // Loop Updaters
        controllerMap.update();
        controlMgr.loop(telemetry);
        scheduler.loop();
        evBus.update();
        telemetry.update();
    }

    @Override
    public void stop()
    {
        controlMgr.stop();
        super.stop();
    }
}
