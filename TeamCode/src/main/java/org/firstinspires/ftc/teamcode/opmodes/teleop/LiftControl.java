package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Intake;
import org.firstinspires.ftc.teamcode.hardware.Lift;
import org.firstinspires.ftc.teamcode.hardware.navigation.PID;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.opmodes.util.FTCDVS;
import org.firstinspires.ftc.teamcode.util.Logger;

public class LiftControl extends ControlModule {

    private Lift lift;
    private Intake intake;
    private double target = 0;
    private ElapsedTime timer = new ElapsedTime();

    private final PID pid = new PID(0.02, 0, 0, 0.015, 0, 0);

    private ControllerMap.AxisEntry ax_lift_right_y;
    private ControllerMap.ButtonEntry y_button;
    private ControllerMap.ButtonEntry b_button;
    private ControllerMap.ButtonEntry a_button;

    public LiftControl(String name) {
        super(name);
    }

    @Override
    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager) {
        this.lift = robot.lift;
        this.intake = robot.intake;

        ax_lift_right_y = controllerMap.getAxisMap("lift:manual", "gamepad2", "right_stick_y");
        y_button = controllerMap.getButtonMap("lift:high","gamepad1","y");
        b_button = controllerMap.getButtonMap("lift:mid","gamepad1","b");
        a_button = controllerMap.getButtonMap("lift:low","gamepad1","a");
    }

    @Override
    public void init_loop(Telemetry telemetry) {
        super.init_loop(telemetry);

        if (!lift.getLimit()) {
            lift.setPower(-0.2);
        }

        if (lift.getLimit()) {
            lift.resetEncoders();
        }

        lift.setHolderPosition(0.3);
    }

    @Override
    public void update(Telemetry telemetry) {
        if(intake.intaken()) {
            target = 0;
            lift.setHolderPosition(0.17);
        }

        double power = pid.getOutPut(target, lift.getCurrentPosition(), 1) * Math.min(timer.seconds() * 0.4, 1);

        lift.setPower(power);
    }
}
