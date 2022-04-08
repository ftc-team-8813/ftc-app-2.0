package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.hardware.Intake;
import org.firstinspires.ftc.teamcode.hardware.Lift;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.util.Storage;

public class IntakeControl extends ControlModule{
    private Intake intake;
    private Lift lift;
    private ElapsedTime timer;

    private ControllerMap.AxisEntry left_trigger;
    private ControllerMap.AxisEntry right_trigger;
    private ControllerMap.ButtonEntry right_bumper;

    private double HOLD_TIME;
    private double CLOSE_CLAW_FREIGHT;
    private double OPEN_CLAW;
    private double PITSTOP;
    private boolean holding_freight;

    public IntakeControl(String name) {
        super(name);
    }

    @Override
    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager) {
        this.intake = robot.intake;
        this.lift = robot.lift;
        this.timer = new ElapsedTime();

        left_trigger = controllerMap.getAxisMap("intake:outtake", "gamepad1", "left_trigger");
        right_trigger = controllerMap.getAxisMap("intake:intake", "gamepad1", "right_trigger");
        right_bumper = controllerMap.getButtonMap("intake:deposit", "gamepad2", "right_bumper");

        HOLD_TIME = Storage.getJsonValue("hold_time");
        CLOSE_CLAW_FREIGHT = Storage.getJsonValue("close_claw_freight");
        OPEN_CLAW = Storage.getJsonValue("open_claw");
        PITSTOP = Storage.getJsonValue("pitstop");
    }

    @Override
    public void init_loop(Telemetry telemetry) {
        super.init_loop(telemetry);
    }

    @Override
    public void update(Telemetry telemetry) {
        if (intake.freightDetected()){
            intake.setPower(-left_trigger.get() * 0.3);
        } else {
            intake.setPower(right_trigger.get() - left_trigger.get());
        }

        // Starts timer for moving claw
        if ((right_bumper.get() && lift.getLiftPosition() > PITSTOP) || intake.getPower() > 0){
            intake.deposit(OPEN_CLAW);
        } else {
            intake.deposit(CLOSE_CLAW_FREIGHT);
        }

        telemetry.addData("Freight Distance: ", intake.freight_checker.getDistance(DistanceUnit.CM));
    }
}
