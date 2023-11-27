package org.firstinspires.ftc.teamcode.opmodes.teleop;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Horizontal;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;

public class HorizControl extends ControlModule{

    private Horizontal horiz;
    public ControllerMap.ButtonEntry goIn;
    public ControllerMap.ButtonEntry goOut;
    private double MAXEXTENDEDHORIZ = 1440;
    private double HORIZRETRACTED = 0;


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
        goIn = controllerMap.getButtonMap("goIn", "gamepad1", "dpad_up");
        goOut = controllerMap.getButtonMap("goOut", "gamepad1", "dpad_down");
        horiz.resetEncoders();
    }


    @Override
    public void update(Telemetry telemetry) {

        if ((Math.abs(horiz.getCurrentPosition() - horiz.getHorizTarget()) < 15)) {
            horiz.setHorizPwr(0);
        }else if(horiz.getHorizTarget() == MAXEXTENDEDHORIZ){
            horiz.setHorizPwr(0.8);
        }else if(horiz.getHorizTarget() == HORIZRETRACTED){
            horiz.setHorizPwr(-0.8);
        }

        if (goIn.edge() == -1) {
            horiz.setHorizTarget(HORIZRETRACTED);
        } else if (goOut.edge() == -1) {
            horiz.setHorizTarget(MAXEXTENDEDHORIZ);
        }

        telemetry.addData("Horiz Power", horiz.getHorizPwr());
        telemetry.addData("Horiz Current", horiz.getCurrentPosition());
        telemetry.addData("Horiz Target", horiz.getHorizTarget());

//        horiz.setHorizPwr(0.6);
    }

}
