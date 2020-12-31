package org.firstinspires.ftc.teamcode.opmodes.test;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.hardware.Shooter;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.util.Storage;

@TeleOp(name="Shooter Test")
public class ShooterTest extends OpMode
{
    private Servo pusher;
    private Shooter shooter;
    
    @Override
    public void init()
    {
        pusher = hardwareMap.servo.get("pusher");
        shooter = new Shooter(hardwareMap.dcMotor.get("shooter"), Storage.getFile("shooter.json"));
    }
    
    @Override
    public void loop()
    {
        if (gamepad1.y) shooter.start();
        else shooter.stop();
        if (gamepad1.b) pusher.setPosition(0.5);
        else pusher.setPosition(0.1);
        shooter.update();
    }
}
