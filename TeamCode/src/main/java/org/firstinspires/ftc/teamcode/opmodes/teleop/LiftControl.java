package org.firstinspires.ftc.teamcode.opmodes.teleop;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Lift;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.util.Logger;
import org.firstinspires.ftc.teamcode.util.Storage;

public class LiftControl extends ControlModule {
    private Lift lift;
    private Logger log = new Logger("Lift Control");

    ControllerMap.AxisEntry left_stick_x;
    ControllerMap.AxisEntry right_stick_y;
    ControllerMap.ButtonEntry a;
    ControllerMap.ButtonEntry dpad_down;

    private double PITSTOP;
    private int id = -1;
    private double preset_rotate;
    private double preset_extend;
    private boolean manual = true;

    public LiftControl(String name) {
        super(name);
    }

    @Override
    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager) {
        this.lift = robot.lift;

        left_stick_x = controllerMap.getAxisMap("lift:rotate", "gamepad2", "left_stick_x");
        right_stick_y = controllerMap.getAxisMap("lift:raise", "gamepad2", "right_stick_y");
        a = controllerMap.getButtonMap("lift:low", "gamepad2", "a");
        dpad_down = controllerMap.getButtonMap("lift:home", "gamepad2", "dpad_down");

        PITSTOP = Storage.getJsonValue("pitstop");
    }

    @Override
    public void init_loop(Telemetry telemetry) {
        super.init_loop(telemetry);
        lift.resetLift();
    }

    @Override
    public void update(Telemetry telemetry) {
        if (manual) {
            lift.raise(lift.lift_target + (-right_stick_y.get() * 1000));
            if (lift.getLiftPosition() >= PITSTOP + 5000) {
                lift.rotate(lift.pivot_target + (left_stick_x.get() * 1.5));
            }
        }

        if (a.get()){
            preset_rotate = 20;
            preset_extend = 70000;
            id = 0;
            manual = false;
        }else if (dpad_down.get()){
            preset_rotate = 0;
            preset_extend = 0;
            id = 0;
            manual = false;
        }

        switch (id) {
            case 0:
                lift.raise(PITSTOP, true);
                if (lift.liftReached()) id += 1;
                break;
            case 1:
                lift.rotate(preset_rotate, true);
                if (lift.pivotReached()) id += 1;
                break;
            case 2:
                lift.raise(preset_extend, true);
                if (lift.liftReached()) id += 1;
                break;
            case 3:
                id = -1;
                manual = true;
                break;
        }

//        log.i("Id: %d", id);
        telemetry.addData("Lifting: ", lift.lifting);
        telemetry.addData("Pivoting: ", lift.pivoting);
        telemetry.addData("Lift Current: ", lift.getLiftPosition());
        telemetry.addData("Lift Target: ", lift.lift_target);
        telemetry.addData("Pivot Current: ", lift.getPivotPosition());
        telemetry.addData("Pivot Target: ", lift.pivot_target);
        telemetry.addData("Lift Limit Pressed: ", lift.liftAtBottom());
        lift.update();
    }
}
