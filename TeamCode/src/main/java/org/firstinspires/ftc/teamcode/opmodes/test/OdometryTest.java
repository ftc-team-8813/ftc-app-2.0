package org.firstinspires.ftc.teamcode.opmodes.test;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.hardware.IMU;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.hardware.events.IMUEvent;
import org.firstinspires.ftc.teamcode.hardware.navigation.Odometry;
import org.firstinspires.ftc.teamcode.opmodes.LoggingOpMode;
import org.firstinspires.ftc.teamcode.util.Scheduler;
import org.firstinspires.ftc.teamcode.util.event.EventBus;
import org.firstinspires.ftc.teamcode.util.event.TimerEvent;
import org.firstinspires.ftc.teamcode.util.websocket.Server;

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
    
    @Override
    public void init()
    {
        robot = Robot.initialize(hardwareMap, "Odometry Test");
        server = new Server(19999);
        
        robot.drivetrain.resetEncoders();
        imu = robot.imu;

        evBus = robot.eventBus;
        scheduler = robot.scheduler;
        
        imu.initialize(evBus, scheduler);
        
        odometry = new Odometry(hardwareMap.dcMotor.get("turret"), hardwareMap.dcMotor.get("intake"), imu);
        
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
        scheduler.loop();
        evBus.update();
        loopTelem();
    }
    
    @Override
    public void loop()
    {
        loopTelem();
        odometry.updateDeltas();
        scheduler.loop();
        evBus.update();
    }
    
    @Override
    public void stop()
    {
        server.close();
        super.stop();
    }
}
