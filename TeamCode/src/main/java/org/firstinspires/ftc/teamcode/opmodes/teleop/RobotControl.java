package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.arcrobotics.ftclib.controller.PIDFController;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Deposit;
import org.firstinspires.ftc.teamcode.hardware.DepositStates;
import org.firstinspires.ftc.teamcode.hardware.Horizontal;
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
    private final double PIVOTUP = 0.261;
    private final double LIFTSERVSUP = 0.33;
    private final double LIFTSERVPRE = 0.51;
    private final double PIVOTPRE = 0.03;
    private final double LIFTSERVFULL = 0.62;
//    0.63
    private final double PIVOTFULL = 0.107;
    private final double PIVOTINIT = 0.95;
    private final double LIFTSERVINIT = 0.710;
    private final double MICROCLOSED = 0.5;
    private final double MICROOPENED = 0;
    private final double INTAKELOCKOPENED = 0.3;
    private final double INTAKELOCKClOSED = 0.9;
    private final double LIFTDOWNPOS = 0;
    private final double LIFTMIDPOS = 190;
    private final double LIFTHIGHPOS = 310;
    private Lift lift;
    private Horizontal horizontal;
    private Intake intake;
    private Deposit deposit;
    private PIDFController horizPID = new PIDFController(0.025, 0, 0, 0);
    private PIDFController liftPID = new PIDFController(0.02, 0, 0, 0);
    private ControllerMap.ButtonEntry y_button;
    private ControllerMap.ButtonEntry b_button;
    private ControllerMap.ButtonEntry a_button;
    private ControllerMap.ButtonEntry dump;
    private ControllerMap.ButtonEntry takeInIntake;
    private ControllerMap.ButtonEntry x_button;
    private ControllerMap.ButtonEntry inHoriz;
    private LiftStates stateForLift;
    private DepositStates stateForDeposit;
    private IntakeStates stateForIntake;
    private ElapsedTime timer;
    private ElapsedTime timer2;
    private ElapsedTime timer3;
    private ElapsedTime timer4;


    private ElapsedTime dumpTimer;


    private boolean resetted;
    private boolean resetted2;
    private boolean resetted3;
    private boolean resetted4;



    private boolean transferred;
    private boolean in;
    public boolean forward;
    private double intakePower;
    @Override
    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager) {
        this.lift = robot.lift;
        this.horizontal = robot.horiz;
        this.intake = robot.intake;
        this.deposit = robot.deposit;

        y_button = controllerMap.getButtonMap("lift:high", "gamepad1", "y");
        b_button = controllerMap.getButtonMap("lift:mid", "gamepad1", "b");
        a_button = controllerMap.getButtonMap("lift:low", "gamepad1", "a");
        x_button = controllerMap.getButtonMap("transfer", "gamepad1", "x");

        dump = controllerMap.getButtonMap("dump", "gamepad1", "left_bumper");
        takeInIntake = controllerMap.getButtonMap("takeInIntake", "gamepad1", "right_bumper");
        inHoriz = controllerMap.getButtonMap("inHoriz", "gamepad2", "left_bumper");

        stateForLift = LiftStates.LiftDown;
        stateForIntake = IntakeStates.DrivingAround;
        stateForDeposit = DepositStates.Init;

        timer = new ElapsedTime();
        timer2 = new ElapsedTime();
        timer3 = new ElapsedTime();
        timer4 = new ElapsedTime();

        resetted = false;
        resetted2 = false;
        resetted3 = false;
        resetted4 = false;

        transferred = false;
        in = true;
        forward = true;
        intakePower =  0;

    }

    @Override
    public void init_loop(Telemetry telemetry) {
        super.init_loop(telemetry);

    }

    @Override
    public void update(Telemetry telemetry) {
        lift.update();
        horizontal.update();

        switch (stateForLift) {
            case LiftDown:
                lift.setLiftTarget(LIFTDOWNPOS);
                break;

            case LiftUp:
                deposit.setDepoLock(MICROCLOSED);
                if (y_button.edge() == -1) {
                    lift.setLiftTarget(LIFTHIGHPOS);
                }
                if (b_button.edge() == -1) {
                    lift.setLiftTarget(LIFTMIDPOS);
                }
                if (transferred && lift.getCurrentPosition() > 170) {
                    stateForDeposit = DepositStates.Up;
                    stateForLift = LiftStates.Dump;
                }
                if(!transferred && lift.getCurrentPosition() > 170){
                    stateForDeposit = DepositStates.Pre;
                }
                break;

            case Dump:
                if(dump.edge() == -1) {
                    deposit.setDepoLock(MICROOPENED);
                    transferred = false;
                    if(!resetted){
                        timer.reset();
                        resetted = true;
                    }
                }
                if(timer.seconds() > 1){
                    stateForDeposit = DepositStates.Pre;
//                    if(!resetted4){
//                        timer4.reset();
//                        resetted4 = true;
//                    }
//                    if(timer4.seconds() > 0.4){
//                        resetted = false;
//                        resetted2 = false;
//                        resetted3 = false;
//                        resetted4 = false;
//                        stateForLift = LiftStates.LiftDown;
//                        stateForIntake = IntakeStates.DrivingAround;
//                    }

                }
                break;
        }

        switch (stateForIntake) {
            case DrivingAround:
                if(in){
                    horizontal.setHorizTarget(0);
                    intakePower = 0;
                } else {
                    horizontal.setHorizTarget(1440);
                    if (forward) {
                        intakePower = 0.9;
                    } else {
                        intakePower = -0.9;
                    }
                }

                intake.setLock(INTAKELOCKClOSED);
                deposit.setDepoLock(MICROCLOSED);
                break;

            case Transfer:
                deposit.setDepoLock(MICROOPENED);
                horizontal.setHorizTarget(0);
//
                if(!transferred){
                    intakePower = 0;
                }

                if (horizontal.getCurrentPosition() < 50) {
                    if(stateForLift != LiftStates.LiftUp && stateForLift != LiftStates.Dump){
                        stateForDeposit = DepositStates.Full;
                        if(!resetted2){
                            timer.reset();
                            resetted2 = true;
                        }
                        if(timer2.seconds() > 0.5){
                            intake.setLock(INTAKELOCKOPENED); //this is weird
                            if(!resetted3){
                                timer.reset();
                                resetted3 = true;
                            }
                            if(timer3.seconds() > 1){
                                transferred = true;
                                intakePower = 1;
                                stateForLift = LiftStates.LiftUp;
                            }
                        }
                    }else if(stateForLift == LiftStates.LiftUp || stateForLift == LiftStates.Dump){
                        if (transferred && lift.getCurrentPosition() > 170) {
                            stateForDeposit = DepositStates.Up;
                            stateForLift = LiftStates.Dump;
                        }
                    }

                    break;
                }
            }

        switch (stateForDeposit){
            case Pre:
                deposit.setDepoPivot(PIVOTPRE);
                deposit.setLiftDepos(LIFTSERVPRE); //change later
                break;

            case Full:
                deposit.setDepoPivot(PIVOTFULL);
                deposit.setLiftDepos(LIFTSERVFULL);
                break;

            case Init:
                deposit.setDepoPivot(PIVOTINIT);
                deposit.setLiftDepos(LIFTSERVINIT);
                stateForLift = LiftStates.LiftUp;
                break;

            case Up:
                deposit.setDepoPivot(PIVOTUP);
                deposit.setLiftDepos(LIFTSERVSUP);
                break;

            }

        if(a_button.edge() == -1 && (stateForLift == LiftStates.LiftUp || stateForLift == LiftStates.Dump) && stateForDeposit == DepositStates.Pre){
            resetted = false;
            resetted2 = false;
            resetted3 = false;
//            resetted4 = false;
            stateForLift = LiftStates.LiftDown;
            stateForIntake = IntakeStates.DrivingAround;
        }

        if(x_button.edge() == -1 && stateForLift == LiftStates.LiftDown && stateForDeposit == DepositStates.Pre){
            stateForIntake = IntakeStates.Transfer;
        }

        if(takeInIntake.edge() == -1 && forward && stateForIntake == IntakeStates.DrivingAround){
            forward = false;
        }else if(takeInIntake.edge() == -1 && !forward && stateForIntake == IntakeStates.DrivingAround){
            forward = true;
        }

        if(inHoriz.edge() == -1 && in && stateForIntake == IntakeStates.DrivingAround){
            in = false;
        }else if(inHoriz.edge() == -1 && !in && stateForIntake == IntakeStates.DrivingAround){
            in = true;
        }

        horizontal.setHorizPwr(horizPID.calculate(horizontal.getCurrentPosition(), horizontal.getHorizTarget()));
        lift.setLiftsPower(liftPID.calculate(lift.getCurrentPosition(), lift.getLiftTarget()));
        intake.setPower(intakePower);


        telemetry.addData("Deposit State", stateForDeposit);
        telemetry.addData("Intake State", stateForIntake);
        telemetry.addData("Lift State", stateForLift);
        telemetry.addData("Horiz Target", horizontal.getHorizTarget());
        telemetry.addData("In Value", in);
        telemetry.addData("Transferred Value", transferred);
        telemetry.addData("Intake Power", intakePower);
        telemetry.addData("Lift Current", lift.getCurrentPosition());
        telemetry.addData("Intake Lock Position", intake.getLock());
        telemetry.addData("Horiz Current", horizontal.getCurrentPosition());


    }
}
