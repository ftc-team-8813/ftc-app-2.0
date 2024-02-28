package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.arcrobotics.ftclib.controller.PIDFController;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Deposit;
import org.firstinspires.ftc.teamcode.hardware.DepositStates;
import org.firstinspires.ftc.teamcode.hardware.DistanceSensors;
import org.firstinspires.ftc.teamcode.hardware.Intake;
import org.firstinspires.ftc.teamcode.hardware.IntakeStates;
import org.firstinspires.ftc.teamcode.hardware.Lift;
import org.firstinspires.ftc.teamcode.hardware.LiftStates;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;

public class RobotControl extends ControlModule {
    public RobotControl(String name) {
        super(name);
    }

    private final double LIFTDOWNPOS = 0;
    private final double LIFTMIDPOS = 0;
    private final double LIFTHIGHPOS = 0;
    private final double LIFTPRE = 0;
    private final double DEPOPRE = 0;
    private final double DEPOMID = 0;
    private final double C1OPEN = 0;
    private final double C2OPEN = 0;
    private final double C1CLOSE = 0;
    private final double C2CLOSE = 0;
    private final double CAGEOPEN = 0;
    private final double CAGECLOSE = 0;
    private double swivelPos; //add swivel stuff
    private double liftTarget;
    private Lift lift;
    private Intake intake;
    private Deposit deposit;
    private DistanceSensors sensors;
    private PIDFController liftPID = new PIDFController(0.02, 0, 0, 0);

    //Driver 1 Buttons
    private ControllerMap.ButtonEntry y_button;
    private ControllerMap.ButtonEntry b_button;
    private ControllerMap.ButtonEntry a_button;
    private ControllerMap.ButtonEntry x_button;
    private ControllerMap.ButtonEntry leftDump;
    private ControllerMap.ButtonEntry rightDump;
    private ControllerMap.ButtonEntry overrideFor1;

    //Driver 2 Buttons
    private ControllerMap.AxisEntry depoAdjust;
    private ControllerMap.AxisEntry liftAdjust;
    private ControllerMap.ButtonEntry hang;
    private ControllerMap.ButtonEntry droneUp;
    private ControllerMap.ButtonEntry shoot;
    private LiftStates stateForLift;
    private DepositStates stateForDeposit;
    private IntakeStates stateForIntake;
    private ElapsedTime timer;
    private boolean resetted;
    private boolean pixelInD1;
    private boolean pixelInD2;
    private double intakePower;
    private int numStage;

    @Override
    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager) {
        this.lift = robot.lift;
        this.intake = robot.intake;
        this.deposit = robot.deposit;
        this.sensors = robot.sensors;

        y_button = controllerMap.getButtonMap("lift:high", "gamepad1", "y");
        b_button = controllerMap.getButtonMap("lift:mid", "gamepad1", "b");
        a_button = controllerMap.getButtonMap("lift:low", "gamepad1", "a");
        x_button = controllerMap.getButtonMap("transfer", "gamepad1", "x");
        overrideFor1 = controllerMap.getButtonMap("override", "gamepad1", "dpad_up"); //i don't remember
        leftDump = controllerMap.getButtonMap("leftDump", "gamepad1", "left_bumper");
        rightDump = controllerMap.getButtonMap("rightDump", "gamepad1", "right_bumper");


        depoAdjust = controllerMap.getAxisMap("depoAdjust", "gamepad2", "right_stick_x");
        liftAdjust = controllerMap.getAxisMap("liftAdjust", "gamepad2", "left_stick_y");

        hang = controllerMap.getButtonMap("hang", "gamepad2", "y");
        shoot = controllerMap.getButtonMap("shoot", "gamepad2", "a");
        droneUp = controllerMap.getButtonMap("goUp", "gamepad2", "b");

        stateForLift = LiftStates.Down;
        stateForIntake = IntakeStates.Stop;
        stateForDeposit = DepositStates.Pre;

        timer = new ElapsedTime();
        resetted = false;
        intakePower = 0;
        numStage = 0;
        swivelPos = 0;
        liftTarget = 0;
    }

    @Override
    public void init_loop(Telemetry telemetry) {
        super.init_loop(telemetry);
    }

    @Override
    public void update(Telemetry telemetry) {
        lift.update();

        switch (stateForLift) {
            case Down:
                liftTarget = LIFTDOWNPOS;
                break;

            case Up:
                liftTarget = LIFTHIGHPOS;
                break;

            case Mid:
                liftTarget = LIFTMIDPOS;

                break;

            case Pre:
                liftTarget = LIFTPRE;
                break;

            case FineAdjust:
                liftTarget += liftAdjust.get() * 90; //some value, do math
                if (liftTarget > LIFTHIGHPOS){
                    liftTarget = LIFTHIGHPOS;
                }
                if (liftTarget < LIFTPRE){ //maybe change this to low
                    liftTarget = LIFTPRE;
                }
                break;
        }

        switch (stateForIntake){
            case In:
                intake.setPower(0.75);
                intake.setRol(0);
                break;
            case Out:
                intake.setPower(-0.45); //change for a slower outspeed honestly
                intake.setRol(1);
                break;
            case Stop:
                intake.setPower(0);
                intake.setRol(0.5);
                break;
        }

        switch (stateForDeposit){
            case Pre:
                deposit.setPivot(DEPOPRE);
                break;
            case Mid:
                deposit.setPivot(DEPOMID);
                break;
            case FineAdjust:
                swivelPos += depoAdjust.get() * 0.5; //some value, do math
                if (swivelPos > 1){
                    swivelPos = 1;
                }
                if (swivelPos < 0){
                    swivelPos = 0;
                }
                deposit.setSwivel(swivelPos);
                break;
        }

        if(x_button.edge() == -1 || numStage == 1){
            numStage = 1;
            intake.setCage(CAGEOPEN);
            deposit.setSwivel(swivelPos);

            stateForIntake = IntakeStates.In;
            stateForDeposit = DepositStates.Pre;
            stateForLift = LiftStates.Pre;

            if(closeToPosition(DEPOMID, deposit.getPivotCurrent(), 0.1) && closeToPosition(lift.getLiftTarget(), lift.getCurrentPosition(), 10)){ //add threshold
                deposit.setC1(C1OPEN);
                deposit.setC2(C2OPEN);
            }

            if(sensors.getLeft()){ //states might be inversed
                pixelInD1 = true;
            }
            if(sensors.getRight()){ //states might be inversed
                pixelInD2 = true;
            }

            if(pixelInD2 && pixelInD1){
                numStage = 2;
            }

            if(overrideFor1.edge() == -1){
                numStage = 2;
            }
        }

        if(numStage == 2){
            intake.setCage(CAGECLOSE);
            deposit.setC1(C1CLOSE);
            deposit.setC2(C2CLOSE);
            deposit.setSwivel(swivelPos);

            if(!resetted){
                timer.reset();
                resetted = true;
            }
            if(timer.seconds() > 3){
                stateForIntake = IntakeStates.Out;
            }
            if(y_button.edge() == -1){
                stateForLift = LiftStates.Up;
            }else if(b_button.edge() == -1){
                stateForLift = LiftStates.Mid;
            }else if(a_button.edge() == -1){
                stateForLift = LiftStates.Down;
            }

            if(closeToPosition(lift.getLiftTarget(), lift.getCurrentPosition(), 10)){//change threshold
                deposit.setPivot(DEPOMID);
            }

            if(closeToPosition(DEPOMID, deposit.getPivotCurrent(), 0.1)) { //change threshold
                numStage = 3;
                resetted = false;
            }
        }

        if(numStage == 3){
            stateForLift = LiftStates.FineAdjust;
            stateForIntake = IntakeStates.Stop;
            stateForDeposit = DepositStates.FineAdjust;

            if(leftDump.edge() == -1){
                deposit.setC1(C1OPEN); //switch if necessary
            }
            if(rightDump.edge() == -1){
                deposit.setC1(C2OPEN); //switch if necessary
            }

            if(!sensors.getRight() && !sensors.getLeft()){
                if(!resetted){
                    timer.reset();
                    resetted = false;
                }
                if(timer.seconds() > 1){
                    numStage = 1;
                    swivelPos = 0.5; //default value
                    resetted = false;
                }
            }
        }

        lift.setLiftsPower(liftPID.calculate(lift.getCurrentPosition(), liftTarget));
        intake.setPower(intakePower); //make this negative
    }

    public boolean closeToPosition(double target, double current, double deadband) {
        if ((Math.abs(target - current) < deadband)){
            return true;
        } else {
            return false;
        }
    }
}
