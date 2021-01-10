package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.hardware.Drivetrain;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.hardware.Turret;
import org.firstinspires.ftc.teamcode.util.Time;
import org.firstinspires.ftc.teamcode.util.event.EventBus;

@Autonomous(name="terrible auto")
@Disabled
public class StupidAuto extends LinearOpMode
{
    private Drivetrain drivetrain;
    private Turret turret;
    private boolean rollComplete = false;
    
    private void goTo(int target)
    {
        drivetrain.telemove(-0.5, -0.5);
        // final int target = 2600;
        while (drivetrain.top_left.getCurrentPosition() < target)
        {
            turret.shooter.update();
            sleep(10);
        }
    
        drivetrain.telemove(0, 0);
    }
    
    @Override
    public void runOpMode() throws InterruptedException
    {
        Robot robot = new Robot(hardwareMap);
        EventBus bus = new EventBus();
        drivetrain = robot.drivetrain;
        turret = robot.turret;
        
        drivetrain.resetEncoders();
        turret.unpush();
        
        while (!isStarted())
        {
            bus.update();
            sleep(1);
        }
        
        turret.shooter.start();
        goTo(2400);
        double t = Time.now();
        while (Time.now() - t < 3)
        {
            turret.shooter.update();
            sleep(10);
        }
        
        for (int i = 0; i < 3; i++)
        {
            turret.push();
            sleep(250);
            turret.unpush();
            sleep(1250);
            robot.drivetrain.telemove(0.5, -0.5);
            sleep(100);
            robot.drivetrain.telemove(0, 0);
            sleep(100);
        }
    }
}
