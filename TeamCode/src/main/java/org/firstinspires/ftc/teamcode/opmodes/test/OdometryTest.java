package org.firstinspires.ftc.teamcode.opmodes.test;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.hardware.IMU;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.hardware.events.IMUEvent;
import org.firstinspires.ftc.teamcode.hardware.navigation.Odometry;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.opmodes.LoggingOpMode;
import org.firstinspires.ftc.teamcode.opmodes.teleop.ControlMgr;
import org.firstinspires.ftc.teamcode.opmodes.teleop.DriveControl;
import org.firstinspires.ftc.teamcode.util.Scheduler;
import org.firstinspires.ftc.teamcode.util.event.EventBus;
import org.firstinspires.ftc.teamcode.util.event.TimerEvent;
import org.firstinspires.ftc.teamcode.util.websocket.InetSocketServer;
import org.firstinspires.ftc.teamcode.util.websocket.Server;

import java.io.IOException;
import java.nio.ByteBuffer;

@TeleOp(name="Odometry Test")
public class OdometryTest extends LoggingOpMode
{
    private Robot robot;
    private Server server;
    private IMU imu;
    private EventBus evBus;
    private Scheduler scheduler;
    
    private Odometry odometry;
    
    private ControlMgr controlMgr;
    
    private ControllerMap controllerMap;
    
    @Override
    public void init()
    {
        robot = Robot.initialize(hardwareMap, "Odometry Test");
        try
        {
            server = new Server(new InetSocketServer(19999));
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    
        robot.drivetrain.resetEncoders();
        imu = robot.imu;

        evBus = robot.eventBus;
        scheduler = robot.scheduler;
        
        imu.initialize(evBus, scheduler);
        
        odometry = robot.drivetrain.getOdometry();
        
        controllerMap = new ControllerMap(gamepad1, gamepad2, evBus);
        
        controlMgr = new ControlMgr(robot, controllerMap);
        controlMgr.addModule(new DriveControl());
        
        controlMgr.initModules();
        
        server.registerProcessor(0x1, (cmd, payload, resp) -> {
            // Get data
            ByteBuffer buf = ByteBuffer.allocate(44);
            buf.putDouble(odometry.past_l);
            buf.putDouble(odometry.past_r);
            buf.putDouble(imu.getHeading()); // imu heading
            buf.putDouble(odometry.x);
            buf.putDouble(odometry.y);
            odometry.drawColor.write(buf);
            buf.flip();
            
            resp.respond(buf);
        });
        server.startServer();
    }
    
    private void loopTelem()
    {
        telemetry.addData("IMU status", imu.getStatusString() + " -- " + imu.getDetailStatus());
        telemetry.addData("Server status", server.getStatus());
    }
    
    @Override
    public void init_loop()
    {
        controlMgr.init_loop(telemetry);
        scheduler.loop();
        evBus.update();
        loopTelem();
    }
    
    @Override
    public void loop()
    {
        controllerMap.update();
        controlMgr.loop(telemetry);
        loopTelem();
        telemetry.addData("Odo L", odometry.getCurrentL());
        telemetry.addData("Odo R", odometry.getCurrentR());
        telemetry.addData("Odo Heading", Math.toDegrees(odometry.calc_heading));
        telemetry.addData("IMU Heading", imu.getHeading());
        
        scheduler.loop();
        evBus.update();
    }
    
    @Override
    public void stop()
    {
        controlMgr.stop();
        server.close();
        super.stop();
    }
}
