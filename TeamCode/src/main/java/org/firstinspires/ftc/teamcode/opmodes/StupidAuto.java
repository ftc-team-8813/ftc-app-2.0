package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.hardware.Drivetrain;
import org.firstinspires.ftc.teamcode.hardware.Lift;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.hardware.Turret;
import org.firstinspires.ftc.teamcode.hardware.events.LiftEvent;
import org.firstinspires.ftc.teamcode.util.event.EventBus;

@Autonomous(name="bad autonomous")
public class StupidAuto extends LinearOpMode
{
    private Drivetrain drivetrain;
    private Lift lift;
    private Turret turret;
    private boolean rollComplete = false;
    
    private void goTo(int target)
    {
        drivetrain.telemove(-0.5, -0.5);
        // final int target = 2600;
        while (drivetrain.top_left.getCurrentPosition() < target)
        {
            lift.update(telemetry);
            turret.update();
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
        lift = robot.lift;
        turret = robot.turret;
        lift.connectEventBus(bus);
        
        drivetrain.top_left.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        drivetrain.top_right.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        drivetrain.top_left.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        drivetrain.top_right.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        drivetrain.bottom_left.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        drivetrain.bottom_right.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        
        lift.homeLift();
        lift.hold = true;
        bus.subscribe(LiftEvent.class, (ev, bus1, sub) -> lift.moveGrabberPreset(2), "Move grabber", LiftEvent.LIFT_HOMED);
        bus.subscribe(LiftEvent.class, (ev, bus1, sub) -> rollComplete = true, "Move complete", LiftEvent.LIFT_MOVED);
        
        while (!isStarted() || lift.home_stage != 0 || !rollComplete)
        {
            lift.update(telemetry);
            bus.update();
            sleep(1);
        }
        
        turret.setTransfer("catch");
        turret.setShooter(1);
        
        goTo(1300);
        
        rollComplete = false;
        lift.moveGrabberPreset(-2);
        while (!rollComplete)
        {
            turret.update();
            lift.update(telemetry);
            bus.update();
            sleep(1);
        }
        sleep(250);
        for (int i = 0; i < 2; i++)
        {
            turret.setTransfer("out");
            sleep(500);
            
            drivetrain.telemove(0.5, -0.5);
            sleep(75);
            drivetrain.telemove(0, 0);
            
            turret.setTransfer("catch");
            sleep(1450);
        }
        turret.setTransfer("in");
        turret.setShooter(0);
        sleep(1000);
        
        goTo(2400);
    }
}
