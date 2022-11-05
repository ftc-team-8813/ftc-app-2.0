package org.firstinspires.ftc.teamcode.opmodes.teleop;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Claw;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;

public class ClawControl extends ControlModule{
    private Claw claw;
    private ControllerMap.ButtonEntry rb;
//    private ControllerMap.ButtonEntry lb;

    public ClawControl(String name) {
        super(name);
    }

    @Override
    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager) {
        this.claw = robot.claw;

        rb = controllerMap.getButtonMap("rb:claw", "gamepad1", "right_bumper");
//        lb = controllerMap.getButtonMap("lb:claw", "gamepad1", "left_bumper");
    }

    @Override
    public void update(Telemetry telemetry) {
        if(rb.get()){
            if(claw.getStatus() == true){
                claw.setPosition(Claw.closePosition);
            }
            if(claw.getStatus() == false){
                claw.setPosition(Claw.openPosition);
            }
        }
    }
}
