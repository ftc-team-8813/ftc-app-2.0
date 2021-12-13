package org.firstinspires.ftc.teamcode.opmodes.teleop;

import static java.lang.Math.round;

import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Lift;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.util.Logger;
import org.firstinspires.ftc.teamcode.util.Status;

public class LiftControl extends ControlModule{
    private Lift lift;
    private ControllerMap.AxisEntry ax_left_stick_y;
    private ControllerMap.ButtonEntry btn_y;
    private ControllerMap.ButtonEntry btn_a;
    private ControllerMap.ButtonEntry btn_x;
    private ControllerMap.ButtonEntry btn_dpad_down;

    private int height = -1; // 0 = bottom, 1 = low, 2 = mid, 3 = high, 4 = neutral
    private ElapsedTime timer;
    private int id = 0;

    private Logger log;


    public LiftControl(String name){super(name);}

    @Override
    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager) {
        this.lift = robot.lift;
        timer = new ElapsedTime();
        log = new Logger("Lift Control");

        ax_left_stick_y = controllerMap.getAxisMap("lift:adjust", "gamepad2", "left_stick_y");
        btn_y = controllerMap.getButtonMap("lift:extend_high", "gamepad2", "y");
        btn_a = controllerMap.getButtonMap("lift:extend_low", "gamepad2", "a");
        btn_x = controllerMap.getButtonMap("lift:extend_neutral", "gamepad2", "x");
        btn_dpad_down = controllerMap.getButtonMap("lift:reset", "gamepad2", "dpad_down");
    }

    @Override
    public void update(Telemetry telemetry) {
        double delta_extension;
        if (-ax_left_stick_y.get() < -0.1 || -ax_left_stick_y.get() > 0.1){
            delta_extension = -ax_left_stick_y.get() * Status.SENSITIVITY;
        } else {
            delta_extension = 0;
        }

        lift.extend(lift.getTargetLiftPos() + delta_extension, false);

        switch (id) {
            case 0:
                if (btn_dpad_down.get()){
                    height = 0;
                } else if (btn_a.get()){
                    height = 2;
                } else if (btn_y.get()){
                    height = 3;
                } else if (btn_x.get()){
                    height = 4;
                }

                if (btn_dpad_down.get() || btn_a.get() || btn_y.get() || btn_x.get()) {
                    lift.extend(Status.STAGES.get("pitstop"), true);
                    log.i("Set Pitstop");
                }
                if (lift.ifReached(Status.STAGES.get("pitstop"))){
                    log.i("Reached Pitstop");
                    id += 1;
                }
                timer.reset();
                break;
            case 1:
                switch (height){
                    case 0:
                        lift.rotate(Status.ROTATIONS.get("in"));
                        break;
                    case 1:
                        lift.rotate(Status.ROTATIONS.get("low_out"));
                        break;
                    case 2:
                        lift.rotate(Status.ROTATIONS.get("mid_out"));
                        break;
                    case 3:
                        lift.rotate(Status.ROTATIONS.get("high_out"));
                        break;
                    case 4:
                        lift.rotate(Status.ROTATIONS.get("neutral_out"));
                        break;
                }
                if (timer.seconds() > Status.ROTATE_WAIT_TIME){
                    id += 1;
                }
                break;
            case 2:
                switch (height){
                    case 0:
                        lift.extend(0, false);
                        break;
                    case 1:
                        lift.extend(Status.STAGES.get("low"), false);
                        break;
                    case 2:
                        lift.extend(Status.STAGES.get("mid"), false);
                        break;
                    case 3:
                        lift.extend(Status.STAGES.get("high"), false);
                        break;
                    case 4:
                        lift.extend(Status.STAGES.get("neutral"), false);
                        break;
                }
                height = -1;
                id = 0;
                break;
        }

        lift.updateLift();
        telemetry.addData("Lift Timer: ", timer.seconds());
        telemetry.addData("Id: ", id);
        telemetry.addData("Lift Real Pos: ", lift.getCurrentLiftPos());
        telemetry.addData("Lift Target Pos: ", lift.getTargetLiftPos());
    }
}
