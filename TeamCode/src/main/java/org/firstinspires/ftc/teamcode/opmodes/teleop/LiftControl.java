package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Intake;
import org.firstinspires.ftc.teamcode.hardware.Lift;
import org.firstinspires.ftc.teamcode.hardware.navigation.PID;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.util.Logger;

public class LiftControl extends ControlModule {

    private Lift lift;
    private Intake intake;
    private Logger log = new Logger("Lift Control");

    private ElapsedTime timer = new ElapsedTime();

    private final double THETA_DEGREES_PER_TICK = (360/(288*1.5));
    private final double LIFT_MM_PER_TICK = 0;

    private final PID liftPID= new PID(0,0,0, 0,0,0);
    private final PID armPID = new PID(0,0,0,0,0,0);

    private boolean intaken;

    private double armCurrent;
    private double liftCurrent;

    private DigitalChannel liftLimit = lift.getLiftLimit();
    private DigitalChannel armLimit = lift.getArmLimit();

    private ControllerMap.AxisEntry ax_lift_left_x;
    private ControllerMap.AxisEntry ax_lift_left_y;

    private ControllerMap.ButtonEntry y_button;
    private ControllerMap.ButtonEntry b_button;
    private ControllerMap.ButtonEntry a_button;
    private ControllerMap.ButtonEntry x_button;

    public LiftControl(String name) {
        super(name);
    }

    @Override
    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager) {
        this.lift = robot.lift;
        this.intake = robot.intake;
        ax_lift_left_x = controllerMap.getAxisMap("lift:left_x", "gamepad2", "left_stick_x");
        ax_lift_left_y = controllerMap.getAxisMap("lift:right_y", "gamepad2", "left_stick_y");

        y_button = controllerMap.getButtonMap("lift:high","gamepad1","y");
        b_button = controllerMap.getButtonMap("lift:mid","gamepad1","b");
        a_button = controllerMap.getButtonMap("lift:low","gamepad1","a");
        x_button = controllerMap.getButtonMap("lift:ground","gamepad1","x");

        liftLimit.setState(true);
        armLimit.setState(false);

        intaken = false;

        armCurrent = 0;
        liftCurrent = 0;
    }

    @Override
    public void update(Telemetry telemetry) {

        armCurrent = (lift.getEncoderValue()[1] * THETA_DEGREES_PER_TICK) + 50;
        liftCurrent = lift.getEncoderValue()[0] * LIFT_MM_PER_TICK;

        if ((intake.getClawPosition() == 0 && intake.getDistance() < 0) && !intaken) { //change positions
            if (timer.seconds() > 0.5) {
                lift.setPosition(0,0);
                intaken = true;
            }
        }
        else {
            timer.reset();
        }

        if(intake.getClawPosition() == 0.11) {
            intaken = false;
        }

        if (y_button.edge() == -1) { // high
            //set positions
        }

        if (b_button.edge() == -1) { // mid
            //set positions
        }

        if (a_button.edge() == -1) { // low
            //set positions
        }

        if (x_button.edge() == -1) { // ground
            //set positions
        }

        if(liftLimit.getState() == true){
            lift.resetLiftEncoder();
        }
        if(armLimit.getState() == true){
            lift.resetArmEncoder();
        }

        double lift_pow = liftPID.getOutPut(lift.getLiftTarget(), liftCurrent, 1);
        double arm_pow = -1 * armPID.getOutPut(lift.getThetaTarget(), armCurrent, Math.cos(Math.toRadians(armCurrent)));

        telemetry.addData("Arm Current", armCurrent);
        telemetry.addData("Lift Current", liftCurrent);

        telemetry.addData("Lift Power", lift_pow);
        telemetry.addData("Arm Power", arm_pow);

        telemetry.addData("Lift Target", lift.getLiftTarget());
        telemetry.addData("Arm Target", lift.getThetaTarget());
    }
}
