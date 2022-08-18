package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Duck;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;

public class DuckControl extends ControlModule{
    private Duck duck;
    ControllerMap.AxisEntry left_trigger;
    ControllerMap.AxisEntry right_trigger;
    private ElapsedTime spinner_speed_timer;
    private double spinner_speed = 0.0;
    private final double spin_time = 8;

    public DuckControl(String name){
        super(name);
    }

    @Override
    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager) {
        this.duck = robot.duck;
        left_trigger = controllerMap.getAxisMap("duck:spin_left", "gamepad2", "left_trigger");
        right_trigger = controllerMap.getAxisMap("duck:spin_right", "gamepad2", "right_trigger");
        spinner_speed_timer = new ElapsedTime();
    }

    @Override
    public void update(Telemetry telemetry) {
        if (spinner_speed_timer.seconds() > 1.75) {
            if (right_trigger.get() > 0.05) {
                spinner_speed = 0.9;
            } else if (left_trigger.get() > 0.05) {
                spinner_speed = -0.9;
            }
        } else if (spinner_speed_timer.seconds() > 0.1){
            if (right_trigger.get() > 0.05) {
                spinner_speed = spinner_speed_timer.seconds() / spin_time;
            } else if (left_trigger.get() > 0.05) {
                spinner_speed = -spinner_speed_timer.seconds() / spin_time;
            }
        }

        if (left_trigger.get() <= 0.05 && right_trigger.get() <= 0.05) {
            spinner_speed_timer.reset();
            spinner_speed = 0.0;
        }

        duck.spin(spinner_speed);
        telemetry.addData("Duck Spinner Speed: ", spinner_speed);
    }
}