package org.firstinspires.ftc.teamcode.opmodes.teleop;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Horizontal;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;

public class HorizControl extends ControlModule{

    private Horizontal horiz;
    private final double maxPos = 0.29; //setHorizPositions
    private final double minPos = 0;
    private double currentPos;

    private ControllerMap.AxisEntry fineAdjust;

    public HorizControl(String name) {
        super(name);
    }

    @Override
    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager) {
        this.horiz = robot.horiz;
        fineAdjust = controllerMap.getAxisMap("horiz:left_x", "gamepad2", "left_stick_x");
        horiz.setHorizPos(minPos);
        currentPos = 0;
    }

    @Override
    public void update(Telemetry telemetry) {
        currentPos = horiz.getHorizPos();

        if(horiz.getHorizPos() >= minPos && horiz.getHorizPos() <= maxPos){
            horiz.setHorizPos(currentPos + (fineAdjust.get() * 0.01));
        }else if(horiz.getHorizPos() < minPos){
            horiz.setHorizPos(minPos);
        }else if(horiz.getHorizPos() > maxPos){
            horiz.setHorizPos(maxPos);
        }
    }
}
