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

//    private final PID pid = new PID(FTCDVS.getKPArm(), 0, 0, FTCDVS.getKFArm(), 0, 0);

    private ControllerMap.ButtonEntry dpad_up;
    private ControllerMap.ButtonEntry dpad_right;
    private ControllerMap.ButtonEntry dpad_down;
    private ControllerMap.ButtonEntry dpad_left;

    public ArmControl(String name) {
        super(name);
    }

    @Override
    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager) {
        this.arm = robot.arm;
        this.intake = robot.intake;

        dpad_up = controllerMap.getButtonMap("arm:1","gamepad2","dpad_up");
        dpad_right = controllerMap.getButtonMap("arm:5","gamepad2","dpad_right");
        dpad_down = controllerMap.getButtonMap("amr:-1","gamepad2","dpad_down");
        dpad_left = controllerMap.getButtonMap("amr:-5","gamepad2","dpad_left");
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

        PID pid = new PID(FTCDVS.getKPArm(), 0, 0, FTCDVS.getKFArm(), 0, 0);

        arm.setPower(0);
        if(intake.intaken()) {
            target = 0;
        }

        if(dpad_up.edge() == -1) {
            target += 1;
        }

        if(dpad_right.edge() == -1) {
            target += 20;
        }

        if(dpad_down.edge() == -1) {
            target -= 1;
        }

        if(dpad_left.edge() == -1) {
            target -= 20;
        }


        double power = Range.clip(pid.getOutPut(target, arm.getCurrentPosition(), Math.cos(Math.toRadians(arm.getCurrentPosition() + 0))), -0.6, 0.6);

        arm.setPower(power);

        telemetry.addData("Arm Position", arm.getCurrentPosition());
        telemetry.addData("Arm Power", power);
        telemetry.addData("Target",target);
    }
}
