package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Duck;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;

public class DuckControl extends ControlModule{
    private Duck duck;
    private ControllerMap.AxisEntry ax_right_stick_y;
    private ElapsedTime spinner_speed_timer;
    private double spinner_speed = 0.0;
    private boolean inc_spinner_speed = false;

    public DuckControl(String name){
        super(name);
    }

    @Override
    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager) {
        this.duck = robot.duck;
        ax_right_stick_y = controllerMap.getAxisMap("duck:spin", "gamepad2", "right_stick_y");
        spinner_speed_timer = new ElapsedTime();
    }

    @Override
    public void update(Telemetry telemetry) {
        if (ax_right_stick_y.get() > 0.2 || ax_right_stick_y.get() < -0.2) {
            if (spinner_speed_timer.seconds() >= 1) {
                inc_spinner_speed = true;
            }
            else {
                inc_spinner_speed = false;
            }

            if (!inc_spinner_speed) {
                spinner_speed = 0.3;
            }
            if (inc_spinner_speed) {
                spinner_speed = 1.0;
            }
        }

        if (ax_right_stick_y.get() <= 0.2 && ax_right_stick_y.get() >= -0.2) {
            spinner_speed_timer.reset();
            spinner_speed = 0.0;
        }

        duck.spin(spinner_speed);
    }
}
