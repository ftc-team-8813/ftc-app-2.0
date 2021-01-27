package org.firstinspires.ftc.teamcode.opmodes.test;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.hardware.Drivetrain;
import org.firstinspires.ftc.teamcode.hardware.IMU;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.hardware.navigation.AngleHold;
import org.firstinspires.ftc.teamcode.opmodes.LoggingOpMode;
import org.firstinspires.ftc.teamcode.util.Scheduler;
import org.firstinspires.ftc.teamcode.util.event.EventBus;

@TeleOp(name="Where Am I")
public class WhereAmI extends LoggingOpMode
{
    private Drivetrain drivetrain;
    private AngleHold hold;
    private EventBus evBus;
    private Scheduler scheduler;
    
    @Override
    public void init()
    {
        Robot robot = new Robot(hardwareMap);
        drivetrain = robot.drivetrain;
        evBus = new EventBus();
        scheduler = new Scheduler(evBus);
        
        hold = new AngleHold(new IMU(robot.bn), evBus, scheduler, robot.config.getAsJsonObject("nav"));
        drivetrain.top_right.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        drivetrain.top_left.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        drivetrain.bottom_left.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        drivetrain.bottom_right.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }
    
    @Override
    public void loop()
    {
        telemetry.addData("Front L", drivetrain.top_left.getCurrentPosition());
        telemetry.addData("Front R", drivetrain.top_right.getCurrentPosition());
        telemetry.addData("Back L", drivetrain.bottom_left.getCurrentPosition());
        telemetry.addData("Back R", drivetrain.bottom_right.getCurrentPosition());
        telemetry.addData("IMU status", hold.getStatus());
        
        scheduler.loop();
        evBus.update();
    }
}
