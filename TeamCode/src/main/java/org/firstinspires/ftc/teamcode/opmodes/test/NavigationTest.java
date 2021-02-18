package org.firstinspires.ftc.teamcode.opmodes.test;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.hardware.IMU;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.hardware.events.IMUEvent;
import org.firstinspires.ftc.teamcode.hardware.events.NavMoveEvent;
import org.firstinspires.ftc.teamcode.hardware.navigation.Navigator;
import org.firstinspires.ftc.teamcode.hardware.navigation.Odometry;
import org.firstinspires.ftc.teamcode.opmodes.LoggingOpMode;
import org.firstinspires.ftc.teamcode.util.Scheduler;
import org.firstinspires.ftc.teamcode.util.event.EventBus;
import org.firstinspires.ftc.teamcode.util.event.TimerEvent;
import org.firstinspires.ftc.teamcode.util.websocket.Server;

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
        robot = new Robot(hardwareMap);
        server = new Server(19998);
    
        robot.drivetrain.resetEncoders();
        imu = robot.imu;
    
        evBus = new EventBus();
        scheduler = new Scheduler(evBus);
    
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
            buf.put((byte)state); // 57
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
        odometry.updateDeltas();
        scheduler.loop();
        evBus.update();
        loopTelem();
    }
    
    @Override
    public void start()
    {
        nav.setForwardSpeed(0.3);
        nav.setTurnSpeed(0.5);
        nav.goTo(48, 0, true);
        // nav.goTo(0, -48);
        // nav.turnAbs(360);
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
