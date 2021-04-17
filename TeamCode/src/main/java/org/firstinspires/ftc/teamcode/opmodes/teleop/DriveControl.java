package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Drivetrain;
import org.firstinspires.ftc.teamcode.hardware.IMU;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.hardware.navigation.Odometry;
import org.firstinspires.ftc.teamcode.input.ControllerMap;

public class DriveControl extends ControlModule
{
    public DriveControl()
    {
        super("Drive Control");
    }
    
    private Drivetrain drivetrain;
    private double[] speeds;
    private int speedSetting;
    
    private ControllerMap.AxisEntry ax_drive_l;
    private ControllerMap.AxisEntry ax_drive_r;
    private ControllerMap.ButtonEntry btn_slow;
    private ControllerMap.ButtonEntry btn_slow2;
    
    private IMU imu;
    private double lastHeadingTarget;
    public boolean enableHeadingLock = false;
    
    private double headingLockKp = 0.005; // TODO make config variable
    
    @Override
    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager)
    {
        this.drivetrain = robot.drivetrain;
        
        JsonObject config = robot.config.getAsJsonObject("teleop");
        JsonArray driveSpeeds = config.getAsJsonArray("drive_speeds");
        speeds = new double[driveSpeeds.size()];
        for (int i = 0; i < driveSpeeds.size(); i++)
        {
            speeds[i] = driveSpeeds.get(i).getAsDouble();
        }
        
        speedSetting = 2;
        
        ax_drive_l = controllerMap.getAxisMap(  "drive::left",  "gamepad1", "left_stick_y");
        ax_drive_r = controllerMap.getAxisMap(  "drive::right", "gamepad1", "right_stick_y");
        btn_slow   = controllerMap.getButtonMap("drive::slow",  "gamepad1", "left_bumper");
        btn_slow2  = controllerMap.getButtonMap("drive::slow2", "gamepad1", "right_bumper");
        
        imu = robot.imu;
        lastHeadingTarget = imu.getHeading();
    }
    
    @Override
    public void update(Telemetry telemetry)
    {
        double speed = speeds[speedSetting];

        double turn = ax_drive_l.get() * speed;
        
        if (Math.abs(ax_drive_l.get()) < 0.001 && enableHeadingLock)
        {
            double err = imu.getHeading() - lastHeadingTarget;
            turn = err * headingLockKp;
        }
        else
        {
            lastHeadingTarget = imu.getHeading();
        }
        
        drivetrain.telemove(ax_drive_r.get() * speed,
                turn * 0.7);
        
        if (btn_slow.edge() > 0)
        {
            if (speedSetting == 0) speedSetting = 1;
            else speedSetting = 0;
        }
        if (btn_slow2.edge() > 0)
        {
            if (speedSetting == 0) speedSetting = 2;
            else speedSetting = 0;
        }
    }
    
    @Override
    public void alwaysUpdate(Telemetry telemetry)
    {
        drivetrain.getOdometry().updateDeltas();
        
        telemetry.addData("Odo X", drivetrain.getOdometry().x);
        telemetry.addData("Odo Y", drivetrain.getOdometry().y);
        telemetry.addData("Robot Heading", drivetrain.getOdometry().calc_heading);
    }
    
    @Override
    public void disable()
    {
        drivetrain.telemove(0, 0);
        super.disable();
    }
    
    @Override
    public boolean shouldEnable()
    {
        return Math.abs(ax_drive_l.get()) > 0.2 || Math.abs(ax_drive_r.get()) > 0.2;
    }
}
