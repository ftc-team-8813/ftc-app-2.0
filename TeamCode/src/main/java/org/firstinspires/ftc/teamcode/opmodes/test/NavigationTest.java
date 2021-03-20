package org.firstinspires.ftc.teamcode.opmodes.test;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.hardware.IMU;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.hardware.navigation.Navigator;
import org.firstinspires.ftc.teamcode.hardware.navigation.Odometry;
import org.firstinspires.ftc.teamcode.opmodes.LoggingOpMode;
import org.firstinspires.ftc.teamcode.util.Scheduler;
import org.firstinspires.ftc.teamcode.util.event.EventBus;
import org.firstinspires.ftc.teamcode.util.websocket.InetSocketServer;
import org.firstinspires.ftc.teamcode.util.websocket.Server;

import java.io.IOException;
import java.nio.ByteBuffer;

@TeleOp(name="Navigator Test")
public class NavigationTest extends LoggingOpMode
{
    private Robot robot;
    private Server server;
    private IMU imu;
    private EventBus evBus;
    private Scheduler scheduler;
    private Odometry odometry;
    
    private Navigator nav;
    
    private int state = 0; // 0 = initialized, 1 = running, 2 = done
    
    @Override
    public void init()
    {
        robot = Robot.initialize(hardwareMap, "Navigation Test");
        try
        {
            server = new Server(new InetSocketServer(19998));
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
        
        nav = new Navigator(robot.drivetrain, odometry, evBus);
        nav.connectEventBus(evBus);
        state = 0;
        
        /*
        evBus.subscribe(NavMoveEvent.class, (ev, bus, sub) -> {
            state = 2;
            nav.turnAbs(0);
        }, "Nav finished", NavMoveEvent.MOVE_COMPLETE);
         */
    
        server.registerProcessor(0x1, (cmd, payload, resp) -> {
            // Get data
            ByteBuffer buf = ByteBuffer.allocate(64);
            buf.putFloat((float)odometry.x); // 4
            buf.putFloat((float)odometry.y); // 8
            buf.putFloat((float)imu.getHeading()); // 12
            buf.putFloat((float)0); // calculated odometry heading -- 16
            buf.putInt(robot.drivetrain.top_left.getCurrentPosition()); // 20
            buf.putInt(robot.drivetrain.top_right.getCurrentPosition()); // 24
            buf.putFloat((float)odometry.past_l); // 28
            buf.putFloat((float)odometry.past_r); // 32
            buf.putFloat((float)nav.getTargetX()); // 36
            buf.putFloat((float)nav.getTargetY()); // 40
            buf.putFloat((float)nav.getTargetHeading()); // 44
            buf.putFloat((float)nav.getTargetDistance()); // 48
            buf.putFloat((float)nav.getFwdPower()); // 52
            buf.putFloat((float)nav.getTurnPower()); // 56
            buf.put((byte)(nav.navigating() ? 1 : 0)); // 57
            buf.flip();
        
            resp.respond(buf);
        });
        server.registerProcessor(0x2, (cmd, payload, resp) -> {
            byte status = (byte)0;
            if (payload.limit() < 8) status = (byte)1;
            else if (state < 1) status = (byte)2;
            else
            {
                float x = payload.getFloat();
                float y = payload.getFloat();
                nav.goTo(x, y);
            }
            ByteBuffer buf = ByteBuffer.allocate(1);
            buf.put(status);
            buf.flip();
            resp.respond(buf);
        });
        server.registerProcessor(0x3, (cmd, payload, resp) -> {
            byte status = (byte)0;
            if (payload.limit() < 24) status = (byte)1;
            else if (state < 1) status = (byte)2;
            else
            {
                float fspeed = payload.getFloat();
                float tspeed = payload.getFloat();
                float fkp = payload.getFloat();
                float fki = payload.getFloat();
                float tkp = payload.getFloat();
                float tki = payload.getFloat();
                nav.setForwardSpeed(fspeed);
                nav.setTurnSpeed(tspeed);
                nav.forwardKp = fkp;
                nav.forwardKi = fki;
                nav.turnKp = tkp;
                nav.turnKi = tki;
            }
            ByteBuffer buf = ByteBuffer.allocate(1);
            buf.put(status);
            buf.flip();
            resp.respond(buf);
        });
        server.registerProcessor(0x4, (cmd, paylod, resp) -> {
            requestOpModeStop();
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
        odometry.updateDeltas();
        scheduler.loop();
        evBus.update();
        loopTelem();
    }
    
    @Override
    public void start()
    {
        state = 1;
    }
    
    @Override
    public void loop()
    {
        odometry.updateDeltas();
        nav.update(telemetry);
        scheduler.loop();
        evBus.update();
        loopTelem();
    }
    
    @Override
    public void stop()
    {
        server.close();
        super.stop();
    }
}
