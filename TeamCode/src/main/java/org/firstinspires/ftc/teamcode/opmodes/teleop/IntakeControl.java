package org.firstinspires.ftc.teamcode.opmodes.teleop;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Intake;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;

public class IntakeControl extends ControlModule
{
    public IntakeControl()
    {
        super("Intake Control");
    }
    
    private Intake intake;
    private ControllerMap.AxisEntry ax_intake;
    private ControllerMap.AxisEntry ax_intake_out;
    
    @Override
    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager)
    {
        this.intake = robot.intake;
        
        ax_intake     = controllerMap.getAxisMap("intake::intake",     "gamepad1", "right_trigger");
        ax_intake_out = controllerMap.getAxisMap("intake::intake_out", "gamepad1", "left_trigger");
    }
    
    @Override
    public void update(Telemetry telemetry)
    {
        intake.run(ax_intake.get() - ax_intake_out.get());
    }
    
    @Override
    public void disable()
    {
        intake.stop();
        super.disable();
    }
    
    @Override
    public boolean shouldEnable()
    {
        return Math.abs(ax_intake.get()) > 0.2 || Math.abs(ax_intake_out.get()) > 0.2;
    }
}