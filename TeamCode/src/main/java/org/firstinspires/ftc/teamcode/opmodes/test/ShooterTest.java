package org.firstinspires.ftc.teamcode.opmodes.test;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.hardware.Turret;
import org.firstinspires.ftc.teamcode.opmodes.LoggingOpMode;
import org.firstinspires.ftc.teamcode.util.websocket.InetSocketServer;
import org.firstinspires.ftc.teamcode.util.websocket.Server;

import java.io.IOException;
import java.nio.ByteBuffer;

@TeleOp(name="Shooter Test")
public class ShooterTest extends LoggingOpMode
{
    private Turret turret;
    
    private Server server;
    
    @Override
    public void init()
    {
        Robot robot = Robot.initialize(hardwareMap, "Shooter Test");
        this.turret = robot.turret;
    
        try
        {
            server = new Server(new InetSocketServer(17777));
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        server.registerProcessor(0x01, (cmd, payload, resp) -> {
            ByteBuffer out = ByteBuffer.allocate(16);
            out.putFloat((float)((DcMotorEx)turret.shooter.motor).getVelocity(AngleUnit.RADIANS));
            out.putFloat((float)0);
            out.put((byte)(turret.shooter.running() ? 1 : 0));
            
            out.flip();
            resp.respond(out);
        });
        server.startServer();
    }
    
    @Override
    public void loop()
    {
        if (gamepad1.y) turret.shooter.start();
        else turret.shooter.stop();
        // TODO new numbers, put them in the official config file
        if (gamepad1.b) turret.push();
        else turret.unpush();
        turret.shooter.update(telemetry);
    }
    
    @Override
    public void stop()
    {
        server.close();
        super.stop();
    }
}
