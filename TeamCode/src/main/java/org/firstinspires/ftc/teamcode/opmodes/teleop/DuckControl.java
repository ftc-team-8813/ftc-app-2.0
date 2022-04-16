package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Duck;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;

public class DuckControl extends ControlModule{
    private Duck duck;
    ControllerMap.AxisEntry left_trigger;
    ControllerMap.AxisEntry right_trigger;
    private ElapsedTime spinner_speed_timer;
    private ElapsedTime sweeper_timer;
    private double spinner_speed = 0.0;
    private double time_till_max_speed = 1.2;
    private double max_speed = 0.8;
    private boolean stop_duck_spin = false;
    private boolean redTrigOn = false;
    private boolean leftTrigOn = false;

    public DuckControl(String name){
        super(name);
    }

    @Override
    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager) {
        this.duck = robot.duck;
        left_trigger = controllerMap.getAxisMap("duck:spin_left", "gamepad2", "left_trigger");
        right_trigger = controllerMap.getAxisMap("duck:spin_right", "gamepad2", "right_trigger");
        spinner_speed_timer = new ElapsedTime();
        sweeper_timer = new ElapsedTime();
    }

    @Override
    public void update(Telemetry telemetry) {
        if (right_trigger.get() > 0.05) {

            spinner_speed = (spinner_speed_timer.seconds() / time_till_max_speed) * max_speed;
            redTrigOn = true;
            sweeper_timer.reset();
        }
        if (left_trigger.get() > 0.05) {

            spinner_speed = (-spinner_speed_timer.seconds() / time_till_max_speed) * max_speed;

            leftTrigOn = true;
            sweeper_timer.reset();

        }

        if (left_trigger.get() <= 0.05 && right_trigger.get() <= 0.05) {
            spinner_speed_timer.reset();
            spinner_speed = 0.0;
            redTrigOn = false;
            leftTrigOn = false;
            if (sweeper_timer.milliseconds() >= 0.1 && sweeper_timer.milliseconds() < 0.2){
                duck.sweep(0);
            }else if(sweeper_timer.milliseconds() >= 0.2){
                duck.sweep(1);
            }

            if(redTrigOn || leftTrigOn){
                duck.sweep(0);

                sweeper_timer.reset();
            }
        }

        duck.spin(spinner_speed);
        telemetry.addData("Duck Spinner Speed: ", spinner_speed);
    }
}