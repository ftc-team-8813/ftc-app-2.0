package org.firstinspires.ftc.teamcode.opmodes.teleop;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Lift;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.util.Logger;
import org.firstinspires.ftc.teamcode.util.Storage;
import org.slf4j.ILoggerFactory;

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
            lift.raise(lift.getLiftTarget() + (-right_stick_y.get() * 1000));
            if (lift.getLiftPosition() >= PITSTOP) {
                lift.rotate(lift.getPivotTarget() + (left_stick_x.get() * 1.5));
            }
        } else {
            switch (id) {
                case 0:
                    lift.raise(PITSTOP, true);
                    if (lift.inRange(lift.getLiftPosition(), lift.getLiftTarget(), 1000)) id = 1;
                    break;
                case 1:
                    lift.rotate(preset_rotate, true);
                    if (lift.inRange(lift.getPivotPosition(), lift.getPivotTarget(), 1)) id = 3;
                    break;
                case 3:
                    id = -1;
                    manual = true;
                    break;
            }
        }

        if (a.get()){
            preset_rotate = 20;
            preset_extend = 70000;
            id = 0;
            manual = false;
        }else if (dpad_down.get()){
            preset_rotate = 0;
            preset_extend = 250;
            id = 0;
            manual = false;
        }

        telemetry.addData("Lift Current: ", lift.getLiftPosition());
        telemetry.addData("Lift Target: ", lift.getLiftTarget());
        telemetry.addData("Pivot Current: ", lift.getPivotPosition());
        telemetry.addData("Pivot Target: ", lift.getPivotTarget());
        telemetry.addData("Lift Limit Pressed: ", lift.liftAtBottom());

        lift.update();
    }
}
