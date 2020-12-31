package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.hardware.Drivetrain;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.hardware.Turret;
import org.firstinspires.ftc.teamcode.util.event.EventBus;

@Autonomous(name="bad autonomous")
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
        
        while (!isStarted())
        {
            bus.update();
            sleep(1);
        }
        
        
    }
}
