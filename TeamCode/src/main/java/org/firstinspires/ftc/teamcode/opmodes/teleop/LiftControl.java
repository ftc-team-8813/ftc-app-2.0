package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.arcrobotics.ftclib.controller.PIDFController;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Lift;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;

public class LiftControl extends ControlModule {

    private Lift lift;
    private ControllerMap.ButtonEntry y_button;
    private ControllerMap.ButtonEntry b_button;
    private ControllerMap.ButtonEntry a_button;
    private ControllerMap.ButtonEntry x_button;



    private final double LIFTDOWNPOS = 0; //setPos
    private double LIFTLOWPOS = 790;
    private double LIFTMIDPOS = 1120;
    private double LIFTHIGHPOS = 1270;


    private final PIDFController liftPID = new PIDFController(0, 0, 0,0); //setPIDVals

    public LiftControl(String name) {
        super(name);
    }

    @Override
    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager) {
        this.lift = robot.lift;

        y_button = controllerMap.getButtonMap("lift:high","gamepad1","y");
        b_button = controllerMap.getButtonMap("lift:mid","gamepad1","b");
        a_button = controllerMap.getButtonMap("lift:low","gamepad1","a");
        x_button = controllerMap.getButtonMap("lift:default","gamepad1","x");
    }

    @Override
    public void init_loop(Telemetry telemetry) {
        super.init_loop(telemetry);
        //reset encoders here maybe
        lift.setLiftsPower(-0.2);

        lift.update();
        lift.resetEncoders();
    }

    @Override
    public void update(Telemetry telemetry) {
        lift.update();

        if(y_button.edge() == -1){
            lift.setLiftTarget(LIFTHIGHPOS);
        }
        if(b_button.edge() == -1){
            lift.setLiftTarget(LIFTMIDPOS);
        }
        if(a_button.edge() == -1){
            lift.setLiftTarget(LIFTLOWPOS);
        }
        if(x_button.edge() == -1){
            lift.setLiftTarget(LIFTDOWNPOS);
        }

        lift.setLiftsPower(liftPID.calculate(lift.getCurrentPosition(), lift.getLiftTarget()));

        telemetry.addData("Lift Power", lift.getLiftPower());
    }
}
