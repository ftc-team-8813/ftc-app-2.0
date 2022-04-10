package org.firstinspires.ftc.teamcode.opmodes.teleop;

import android.util.Log;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Intake;
import org.firstinspires.ftc.teamcode.hardware.Lift;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.util.Logger;
import org.firstinspires.ftc.teamcode.util.Storage;

public class LiftControl extends ControlModule {
    private Lift lift;
    private Intake intake;
    private Logger log = new Logger("Lift Control");

    ControllerMap.AxisEntry left_stick_x;
    ControllerMap.AxisEntry right_stick_y;
    ControllerMap.ButtonEntry a;
    ControllerMap.ButtonEntry x;
    ControllerMap.ButtonEntry y;
    ControllerMap.ButtonEntry dpad_down;
    ControllerMap.ButtonEntry dpad_left;
    ControllerMap.ButtonEntry dpad_right;

    private int id = -1;
    private double preset_rotate;
    private double preset_raise;
    private int preset_side = 1;
    private boolean can_pre_raise = false;

    private double PITSTOP;
    private double LOW_RAISE;
    private double LOW_ROTATE;
    private double MID_RAISE;
    private double MID_ROTATE;
    private double HIGH_RAISE;
    private double HIGH_ROTATE;
    private double PIVOT_LIFT_TRIGGER;

    public LiftControl(String name) {
        super(name);
    }

    @Override
    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager) {
        this.lift = robot.lift;
        this.intake = robot.intake;

        left_stick_x = controllerMap.getAxisMap("lift:rotate", "gamepad2", "left_stick_x");
        right_stick_y = controllerMap.getAxisMap("lift:raise", "gamepad2", "right_stick_y");
        a = controllerMap.getButtonMap("lift:low", "gamepad2", "a");
        x = controllerMap.getButtonMap("lift:mid", "gamepad2", "x");
        y = controllerMap.getButtonMap("lift:high", "gamepad2", "y");
        dpad_down = controllerMap.getButtonMap("lift:home", "gamepad2", "dpad_down");
        dpad_left = controllerMap.getButtonMap("lift:left_mode", "gamepad2", "dpad_left");
        dpad_right = controllerMap.getButtonMap("lift:right_mode", "gamepad2", "dpad_right");

        PITSTOP = Storage.getJsonValue("pitstop");
        LOW_RAISE = Storage.getJsonValue("low_raise");
        LOW_ROTATE = Storage.getJsonValue("low_rotate");
        MID_RAISE = Storage.getJsonValue("mid_raise");
        MID_ROTATE = Storage.getJsonValue("mid_rotate");
        HIGH_RAISE = Storage.getJsonValue("high_raise");
        HIGH_ROTATE = Storage.getJsonValue("high_rotate");
        PIVOT_LIFT_TRIGGER = Storage.getJsonValue("pivot_lift_trigger");
    }

    @Override
    public void init_loop(Telemetry telemetry) {
        super.init_loop(telemetry);
        lift.resetLift();
    }

    @Override
    public void update(Telemetry telemetry) {
        if (id == -1) {
            lift.raise(lift.getLiftTarget() + (-right_stick_y.get() * 1000));
            if (lift.getLiftPosition() >= PITSTOP) {
                lift.rotate(lift.getPivotTarget() + (left_stick_x.get() * 1.5));
            }
        } else {
            switch (id) {
                case 0:
                    lift.raise(PITSTOP);
                    if (lift.liftReached()) id += 1;
                    break;
                case 1:
                    lift.rotate(preset_rotate);
                    if (lift.getPivotPosition() > PIVOT_LIFT_TRIGGER && preset_raise > PITSTOP){
                        id += 1;
                    } else if (lift.pivotReached()){
                        Log.i("Pivoted", "pivoted");
                        id += 1;
                    }
                    break;
                case 2:
                    lift.raise(preset_raise);
                    if (lift.liftReached()) id += 1;
                case 3:
                    id = -1;
                    break;
            }
        }

        if (a.get()){
            preset_raise = LOW_RAISE;
            preset_rotate = LOW_ROTATE * preset_side;
            id = 0;
        } else if (x.get()){
            preset_raise = MID_RAISE;
            preset_rotate = MID_ROTATE * preset_side;
            id = 0;
        } else if (y.get()){
            preset_raise = HIGH_RAISE;
            preset_rotate = HIGH_ROTATE * preset_side;
            id = 0;
        } else if (dpad_down.get()){
            preset_rotate = 0;
            preset_raise = 25;
            id = 0;
        }

        if (dpad_left.get()){
            preset_side = -1;
        } else if (dpad_right.get()){
            preset_side = 1;
        }

        if (intake.freightDetected() && lift.getLiftPosition() < PITSTOP && can_pre_raise){
            can_pre_raise = false;
            preset_rotate = 0;
            preset_raise = PITSTOP;
            id = 0;
            log.i("Auto Raise");
        } else if (!intake.freightDetected()){
            can_pre_raise = true;
        }

        log.i("Id: %d", id);
        log.i("Lift Target: %f", lift.getLiftTarget());
        telemetry.addData("Lift Current: ", lift.getLiftPosition());
        telemetry.addData("Lift Target: ", lift.getLiftTarget());
        telemetry.addData("Pivot Current: ", lift.getPivotPosition());
        telemetry.addData("Pivot Target: ", lift.getPivotTarget());
        telemetry.addData("Lift Limit Pressed: ", lift.liftAtBottom());
        telemetry.addData("Lift Integral", lift.print_lift_integral);
        telemetry.addData("Pivot Integral", lift.print_pivot_integral);

        lift.update();
    }
}
