package org.firstinspires.ftc.teamcode.opmodes.test;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.hardware.Drivetrain;
import org.firstinspires.ftc.teamcode.hardware.IMU;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.opmodes.LoggingOpMode;
import org.firstinspires.ftc.teamcode.util.Scheduler;
import org.firstinspires.ftc.teamcode.util.event.EventBus;

@TeleOp(name = "Where Am I")
public class WhereAmI extends LoggingOpMode
{
    private Drivetrain drivetrain;
    private EventBus evBus;
    private Scheduler scheduler;
    private DcMotor odo_l, odo_r;
    private IMU imu;
    
    @Override
    public void init()
    {
        super.init();
        Robot robot = Robot.initialize(hardwareMap, "Where Am I");
        drivetrain = robot.drivetrain;
        imu = robot.imu;
        evBus = new EventBus();
        scheduler = new Scheduler(evBus);
        
        imu.initialize(evBus, scheduler);
        drivetrain.top_right.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        drivetrain.top_left.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        drivetrain.bottom_left.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        drivetrain.bottom_right.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        odo_r = hardwareMap.dcMotor.get("ramp");
        odo_l = hardwareMap.dcMotor.get("intake");
        odo_l.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        odo_r.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }
    
    @Override
    public void loop()
    {
        drivetrain.getOdometry().updateDeltas();
        double x = drivetrain.getOdometry().x;
        double y = drivetrain.getOdometry().y;
        
        telemetry.addData("Front L", drivetrain.top_left.getCurrentPosition());
        telemetry.addData("Front R", drivetrain.top_right.getCurrentPosition());
        telemetry.addData("Back L", drivetrain.bottom_left.getCurrentPosition());
        telemetry.addData("Back R", drivetrain.bottom_right.getCurrentPosition());
        telemetry.addData("Odo L", odo_l.getCurrentPosition());
        telemetry.addData("Odo R", odo_r.getCurrentPosition());
        telemetry.addData("Odo X", "%.3f", x);
        telemetry.addData("Odo Y", "%.3f", y);
        telemetry.addData("Heading", "%.3f", imu.getHeading());
        telemetry.addData("IMU status", imu.getStatus() + " -- " + imu.getDetailStatus());
        
        scheduler.loop();
        evBus.update();
    }
}
