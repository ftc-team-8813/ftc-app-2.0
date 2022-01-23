package org.firstinspires.ftc.teamcode.opmodes.util;

import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.opmodes.LoggingOpMode;

public class PIDTuner extends LoggingOpMode {
    private Robot robot;
    private ControllerMap controller_map;

    private ControllerMap.ButtonEntry start_move;
    private ControllerMap.ButtonEntry strafe_p;
    private ControllerMap.ButtonEntry forward_p;
    private ControllerMap.ButtonEntry strafe_i;
    private ControllerMap.ButtonEntry forward_i;

    @Override
    public void init() {
        super.init();
        robot = Robot.initialize(hardwareMap, "PID Tuner", 0);
        start_move = controller_map.getButtonMap("start_move", "gamepad1", "a");
        strafe_p = controller_map.getButtonMap("strafe_p", "gamepad1", "left_stick_y");
        forward_p = controller_map.getButtonMap("forward_p", "gamepad1", "right_stick_y");
        strafe_i = controller_map.getButtonMap("strafe_i", "gamepad1", "left_stick_y");
        forward_i = controller_map.getButtonMap("forward_i", "gamepad1", "right_stick_y");
    }

    @Override
    public void loop() {

    }
}
