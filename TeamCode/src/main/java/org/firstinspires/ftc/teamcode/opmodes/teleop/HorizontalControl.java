package org.firstinspires.ftc.teamcode.opmodes.teleop;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Horizontal;
import org.firstinspires.ftc.teamcode.hardware.Intake;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.hardware.navigation.PID;
import org.firstinspires.ftc.teamcode.input.ControllerMap;

public class HorizontalControl extends ControlModule {

    private Horizontal horizontal;
    private Intake intake;
    private double target = 0;

    private final PID pid = new PID(0.01, 0, 0, 0, 0, 0);

    public HorizontalControl(String name) {
        super(name);
    }

    @Override
    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager) {
        this.horizontal = robot.horizontal;
        this.intake = robot.intake;

        horizontal.setPower(0.3);
    }

    @Override
    public void init_loop(Telemetry telemetry) {
        super.init_loop(telemetry);

        if (horizontal.getLimit()) {
            horizontal.resetEncoders();
            horizontal.setPower(0);
        }
    }

    @Override
    public void update(Telemetry telemetry) {

        if(intake.intaken()) {
            target = 0;
        }

        double power = pid.getOutPut(target,horizontal.getCurrentPosition(),0);

        horizontal.setPower(power);
    }
}
