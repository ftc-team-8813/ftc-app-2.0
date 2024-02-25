package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.arcrobotics.ftclib.controller.PIDFController;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Horizontal;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;

public class HorizControl extends ControlModule{

    private Horizontal horiz;
    private Robot robot;
    public ControllerMap.ButtonEntry forAndBack;
    public ControllerMap.ButtonEntry stop;

    public boolean forward = false;
    public boolean start = false;
    public double power = 0;

    PIDFController horizPID = new PIDFController(0.03, 0, 0, 0);

    public HorizControl(String name) {
        super(name);
    }

    @Override
    public void init_loop(Telemetry telemetry) {
        super.init_loop(telemetry);
    }

    @Override
    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager) {
        this.horiz = robot.horiz;
        this.robot = robot;
//
        stop = controllerMap.getButtonMap("goIn", "gamepad2", "dpad_down");
        forAndBack = controllerMap.getButtonMap("goOut", "gamepad2", "dpad_up");
//        stopButton = controllerMap.getButtonMap("stop", "gamepad2", "dpad_right");
//        rb = controllerMap.getButtonMap("forwardandback", "gamepad2", "right_bumper");

//        power = 0;
        forward = false;
//        start = false;

//        stop = true;

        horiz.resetEncoders();

    }

    @Override
    public void update(Telemetry telemetry) {
    horiz.update();
//        if (forward && !stop) {
//            power = 0.8;
//        } else if (!forward && !stop) {
//            power = -0.8;
//        } else if (stop){
//            power = 0;
//        }
//
//        horiz.setHorizPwr(1);
//
//        if (goOut.edge() == -1) {
//            forward = true;
//            stop = false;
//        } else if (goIn.edge() == -1) {
//            forward = false;
//            stop = false;
//        } else if(stopButton.edge() == -1){
//            stop = true;
//        }

        if(forAndBack.edge() == -1 && forward){
            horiz.setHorizTarget(1440);
            forward = false;
        }else if(forAndBack.edge() == -1 && !forward){
            horiz.setHorizTarget(0);
            forward = true;
        }else if(stop.edge() == -1){
            horiz.setHorizTarget(horiz.getCurrentPosition());
        }

        horiz.setHorizPwr(horizPID.calculate(horiz.getCurrentPosition(), horiz.getHorizTarget()));

        telemetry.addData("Horiz Power", horiz.getHorizPwr());
        telemetry.addData("Horiz Current", horiz.getCurrentPosition());
        telemetry.addData("Horiz Target", horiz.getHorizTarget());


    }

}
