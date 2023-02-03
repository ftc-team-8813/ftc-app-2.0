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
    private boolean lift_ready_for_cone;
    private boolean cone_held;
    private final double LIFT_ACCELERATION_CONSTANT = 0.4;
    private ElapsedTime lift_acceleration_timer = new ElapsedTime();
    private ElapsedTime time_till_held = new ElapsedTime();

    private final PID pid = new PID(0.02, 0, 0, 0.015, 0, 0);

    private ControllerMap.AxisEntry ax_lift_right_y;
    private ControllerMap.ButtonEntry y_button;
    private ControllerMap.ButtonEntry b_button;
    private ControllerMap.ButtonEntry a_button;
    private ControllerMap.ButtonEntry some_button;

    private ControllerMap.ButtonEntry drop;

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
//        some_button = controllerMap.getButtonMap("lift:down","gamepad1","x"); //TODO Change
        drop = controllerMap.getButtonMap("dump", "gamepad1", "left_bumper");

        lift.setPower(-0.2);
        lift.setHolderPosition(0.15);
    }

    @Override
    public void init_loop(Telemetry telemetry) {
        super.init_loop(telemetry);

        if (lift.getLimit()) {
            lift.resetEncoders();
            lift.setPower(0);
        }
    }

    @Override
    public void update(Telemetry telemetry) {

        if(intake.intaken() && !lift_ready_for_cone) {
            target = 0;
            lift.setHolderPosition(0.17);
            lift_ready_for_cone = false;
            if (lift.getLiftCurrent() < 10) {
                lift_ready_for_cone = true;
                time_till_held.reset();
            }
        }

        if (lift_ready_for_cone && time_till_held.seconds() > 0.7) {
            cone_held = true;
        }

        if (cone_held) {
            lift.setHolderPosition(0.33);
            lift_ready_for_cone = false;
            cone_held = false;
        }

        if (y_button.edge() == -1) {
            target = 715;
            lift_acceleration_timer.reset();
        }

        if (b_button.edge() == -1) {
            target = 405;
            lift_acceleration_timer.reset();
        }

        if (a_button.edge() == -1) {
            target = 125;
            lift_acceleration_timer.reset();
        }

        if (some_button.edge() == -1) {
            target = 0;
            lift_acceleration_timer.reset();
        }

        if (drop.edge() == -1) {
            target = 0;
            if (target < 450) {
                lift.setHolderPosition(0.4);
            }
            else {
                lift.setHolderPosition(0.33);
            }

        }



        double power = pid.getOutPut(target, lift.getLiftCurrent(), 1) * Math.min(lift_acceleration_timer.seconds() * LIFT_ACCELERATION_CONSTANT, 1);

        lift.setPower(power);
    }
}
