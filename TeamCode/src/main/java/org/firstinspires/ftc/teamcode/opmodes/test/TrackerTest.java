package org.firstinspires.ftc.teamcode.opmodes.test;

import com.google.gson.JsonObject;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.hardware.IMU;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.hardware.autoshoot.AutoAim;
import org.firstinspires.ftc.teamcode.opmodes.LoggingOpMode;
import org.firstinspires.ftc.teamcode.util.Scheduler;
import org.firstinspires.ftc.teamcode.util.event.EventBus;
import org.firstinspires.ftc.teamcode.util.websocket.Server;

import java.nio.ByteBuffer;

@TeleOp(name="Tracker Test")
public class TrackerTest extends LoggingOpMode
{
    private Robot robot;
    private IMU imu;
    private AutoAim autoAim;
    private EventBus ev;
    private Scheduler scheduler;
    
    private Server server;

    @Override
    public void init() {
        robot = Robot.initialize(hardwareMap, "Tracker Test");
        
        JsonObject trackerConf = robot.config.getAsJsonObject("tracker");
        double off = trackerConf.get("offset").getAsDouble();
        double target_x = trackerConf.get("target_x").getAsDouble();
        double target_y = trackerConf.get("target_y").getAsDouble();
        robot.drivetrain.getOdometry().setPosition(0, 0);
        
        autoAim = new AutoAim(robot.drivetrain.getOdometry(), robot.turret.getTurretHome());
        autoAim.setTarget(target_x, target_y);
        ev = new EventBus();
        scheduler = new Scheduler(ev);
        imu = robot.drivetrain.getOdometry().getIMU();

        imu.initialize(ev, scheduler);
        robot.turret.startZeroFind();
        
        server = new Server(19997);
        
        server.registerProcessor(0x01, (cmd, payload, resp) -> {
            ByteBuffer buf = ByteBuffer.allocate(16);
            buf.putFloat((float)robot.drivetrain.getOdometry().x);
            buf.putFloat((float)robot.drivetrain.getOdometry().y);
            buf.putFloat((float)robot.imu.getHeading());
            buf.putFloat((float)robot.turret.getHeading());
            
            buf.flip();
            resp.respond(buf);
        });
        
        server.registerProcessor(0x02, (cmd, payload, resp) -> {
            ByteBuffer buf = ByteBuffer.allocate(12);
            buf.putFloat((float)target_x);
            buf.putFloat((float)target_y);
            buf.putFloat((float)off);
            
            buf.flip();
            resp.respond(buf);
        });
        
        server.startServer();
    }
    
    @Override
    public void init_loop()
    {
        robot.turret.updateInit(telemetry);
        scheduler.loop();
        ev.update();
    }

    @Override
    public void loop() {
        robot.drivetrain.telemove(-gamepad1.left_stick_y * 0.3,
                                 -gamepad1.right_stick_y * 0.3);
        robot.drivetrain.getOdometry().updateDeltas();

        telemetry.addData("Odo X", robot.drivetrain.getOdometry().x);
        telemetry.addData("Odo Y", robot.drivetrain.getOdometry().y);
        telemetry.addData("Odo L", robot.drivetrain.getOdometry().past_l);
        telemetry.addData("Odo R", robot.drivetrain.getOdometry().past_r);
        robot.turret.rotate(autoAim.getTurretRotation(telemetry));
        robot.turret.update(telemetry);
        scheduler.loop();
        ev.update();
    }
    
    @Override
    public void stop()
    {
        super.stop();
        server.close();
    }
}
