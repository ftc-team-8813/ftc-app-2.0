package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Arm;
import org.firstinspires.ftc.teamcode.hardware.Intake;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.hardware.navigation.PID;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.opmodes.util.FTCDVS;

public class ArmControl extends ControlModule {

    private Arm arm;
    private Intake intake;
    private double target = 0;

    private final PID pid = new PID(0.0095, 0, 0, 0, 0, 0);

    public ArmControl(String name) {
        super(name);
    }

    @Override
    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager) {
        this.arm = robot.arm;
        this.intake = robot.intake;
    }

    @Override
    public void init_loop(Telemetry telemetry) {
        super.init_loop(telemetry);

        if (!arm.getLimit()) {
            arm.setPower(0.5);
        }

        if (arm.getLimit()) {
            arm.resetEncoders();
        }
    }

    @Override
    public void update(Telemetry telemetry) {
        if(intake.intaken()) {
            target = 0;
        }

        double power = Range.clip(pid.getOutPut(target, arm.getCurrentPosition(), Math.cos(Math.toRadians(arm.getCurrentPosition() + 0))), -0.6, 0.6);

        arm.setPower(power);
    }
}
