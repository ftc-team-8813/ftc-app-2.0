package org.firstinspires.ftc.teamcode.opmodes.test;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.hardware.Drivetrain;
import org.firstinspires.ftc.teamcode.hardware.IMU;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.hardware.navigation.Odometry;
import org.firstinspires.ftc.teamcode.opmodes.LoggingOpMode;
import org.firstinspires.ftc.teamcode.util.Logger;

@TeleOp(name = "Odometry Drive Test")
public class OdometryDriveTest extends LoggingOpMode
{
    
    private Robot robot;
    private Logger log = new Logger("Odometry Drive Test");
    private Odometry odometry;
    private IMU imu;
    private Drivetrain drivetrain;
    
    @Override
    public void init()
    {
        super.init();
        robot = Robot.initialize(hardwareMap, "Odometry Drive Test");
        odometry = robot.drivetrain.getOdometry();
        imu = odometry.getIMU();
        drivetrain = robot.drivetrain;
        imu.initialize(robot.eventBus, robot.scheduler);
    }
    
    @Override
    public void init_loop()
    {
        telemetry.addData("IMU status", imu.getStatusString() + ", " + imu.getDetailStatus());
    }
    
    @Override
    public void loop()
    {
        double fwd = 0.2;
        double turn = odometry.calc_heading * 2;
        
        robot.drivetrain.telemove(fwd, turn);
        
        telemetry.addData("X", odometry.x);
        telemetry.addData("Y", odometry.y);
        telemetry.addData("Heading", odometry.calc_heading);
        
        odometry.updateDeltas();
    }
    
    @Override
    public void stop()
    {
        log.i("Distance: %.4f in", odometry.x);
        log.i("Y: %.4f", odometry.y);
        super.stop();
    }
}
