package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Intake;
import org.firstinspires.ftc.teamcode.hardware.Lift;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.hardware.navigation.IntakeStates;
import org.firstinspires.ftc.teamcode.hardware.navigation.LiftStates;
import org.firstinspires.ftc.teamcode.hardware.navigation.Modes;
import org.firstinspires.ftc.teamcode.hardware.navigation.PID;
import org.firstinspires.ftc.teamcode.input.ControllerMap;

@Config

public class RobotControl extends ControlModule{

    public static double HORIZ_KP = 0.008;
    private Lift lift;
    private Intake intake;

    private ElapsedTime intakeTimer;
    private boolean intakeTimerReset = false;
    private ElapsedTime liftTimer;
    private boolean liftTimerReset = false;
    private ElapsedTime loop;

    private ElapsedTime lift_trapezoid;
    private boolean lift_trapezoid_reset = false;
    private double lift_accel = 0.4;
    private double lift_last_height;

    private boolean dumped = false;

    private ControllerMap.ButtonEntry y_button;
    private ControllerMap.ButtonEntry b_button;
    private ControllerMap.ButtonEntry a_button;
    private ControllerMap.ButtonEntry x_button;

    private ControllerMap.ButtonEntry sense;
    private ControllerMap.ButtonEntry dump;

    private ControllerMap.ButtonEntry rec_right_bumper;
    private ControllerMap.AxisEntry rec_right_trigger;
    private ControllerMap.ButtonEntry rec_left_bumper;
    private ControllerMap.AxisEntry rec_left_trigger;

    private ControllerMap.ButtonEntry switchFast;
    private ControllerMap.ButtonEntry switchGround;
    private ControllerMap.ButtonEntry switchCircuit;

    private ControllerMap.AxisEntry ax_lift_left_x;
    private ControllerMap.AxisEntry ax_lift_left_y;
    private ControllerMap.AxisEntry ax_lift_right_y;

    private LiftStates stateForLift;
    private IntakeStates stateForIntake;
    private Modes mode;

    private double LIFTDOWNPOS = 0;
    private double LIFTDOWNPOSFAST = 35;
    private double LIFTLOWPOS = 130;
    private double LIFTMIDPOS = 405;
    private double LIFTHIGHPOS = 715;

    private double DEPOSITLOW = 0.4;
    private double DEPOSITLOW2 = 0.58;
    private double DEPOSITLOW3 = 0;
    private double DEPOSITMID = 0.33;
    private double DEPOSITHIGH = 0.33;

    private double DEPOSITHIGHFAST = 0.38;

    private double DEPOSITTRANSFER = 0.145;
    private double DEPOSITTRANSFER2 = 0.2;
    private double DEPOSITTRANSFERFAST = 0.14;
    private double DEPOSITTRANSFERFAST2 = 0.23;

    private double DEPOSITLIFT = 0.33;
    private double DEPOSITLIFTFAST = 0.28;

    private double ARMCOMPLETEDOWNPOS = -120;
    private double ARMMIDPOS = -28;
    private double ARMMIDPOS2 = -28; //used while the horiz slide is retracting
    private double ARMHIGHPOS = 30; //positive to make it dig into the end stop

    private double WRISTLOOKINGFORCONE = 0.019;
    private double WRISTTRANSFER = 0.678;

    private double MAXEXTENDEDHORIZ = -800;
    private double FASTMODEHORIZ = -450;
    private double HORIZRETRACTED = 0;

    private double CLAWOPENPOS = 0.3;
    private double CLAWCLOSEPOS = 0.1;

    private double ARMLOWGOAL = -40;
    private double ARMGROUNDGOAL = -80;

    private PID arm_PID;
    private PID horiz_PID;
    private PID lift_PID;
    private boolean GroundLow;

    public static double ARMCLIPDOWN = 1;
    public static double ARMCLIPDOWNSLOW = 0.2;
    public static double ARMCLIPUP = 1;

    public RobotControl(String name) {
        super(name);
    }

    @Override
    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager) {

        intakeTimer = new ElapsedTime();
        liftTimer = new ElapsedTime();
        loop = new ElapsedTime();

        lift_trapezoid = new ElapsedTime();

        this.lift = robot.lift;
        this.intake = robot.intake;

        sense = controllerMap.getButtonMap("senseCone", "gamepad1", "right_bumper");
        dump = controllerMap.getButtonMap("dump", "gamepad1", "left_bumper");

        y_button = controllerMap.getButtonMap("lift:high","gamepad1","y");
        b_button = controllerMap.getButtonMap("lift:mid","gamepad1","b");
        a_button = controllerMap.getButtonMap("lift:low","gamepad1","a");
        x_button = controllerMap.getButtonMap("lift:ground","gamepad1","x");

        rec_right_bumper = controllerMap.getButtonMap("lift:reset_encoder_rb","gamepad2","right_bumper");
        rec_right_trigger = controllerMap.getAxisMap("lift:reset_encoder_rt","gamepad2","right_trigger");
        rec_left_bumper = controllerMap.getButtonMap("lift:reset_encoder_lb","gamepad2","left_bumper");
        rec_left_trigger = controllerMap.getAxisMap("lift:reset_encoder_lt","gamepad2","left_trigger");

        switchFast = controllerMap.getButtonMap("switchFast", "gamepad2", "dpad_up");
        switchGround = controllerMap.getButtonMap("switchGround", "gamepad2", "dpad_down");
        switchCircuit = controllerMap.getButtonMap("switchCircuit", "gamepad2", "dpad_right");

        ax_lift_left_x = controllerMap.getAxisMap("lift:left_x", "gamepad2", "left_stick_x"); //finetuning
        ax_lift_left_y = controllerMap.getAxisMap("lift:left_y", "gamepad2", "left_stick_y");
        ax_lift_right_y = controllerMap.getAxisMap("lift:right_y", "gamepad2", "right_stick_y");

        stateForLift = LiftStates.LiftDown;
        stateForIntake = IntakeStates.DrivingAround;
        mode = Modes.Circuit;

        lift.setDumper(DEPOSITTRANSFER);

        GroundLow = false;

        intake.setArmPow(0.5);
        lift.setLiftPower(-0.2);
        intake.setHorizPow(0.3);

        //intake.setHorizTarget(0); //used for run to position
    }

    @Override
    public void init_loop(Telemetry telemetry) {
        super.init_loop(telemetry);

        if(intake.getArmLimit()){
            intake.resetArmEncoder();
            intake.setArmPow(0);
        }
        if(lift.getLift_limit()){
            lift.resetLiftEncoder();
            lift.setLiftPower(0);
        }
        if(intake.getHorizLimit()){
            intake.resetHorizEncoder();
            intake.setHorizPow(0);
            //intake.setHorizPow(1); //this scales the speed for run to position; it won't move until you set a target
        }
    }

    @Override
    public void update(Telemetry telemetry) {
        loop.reset();

        arm_PID = new PID(0.009, 0, 0, 0.1, 0, 0);
        horiz_PID = new PID(HORIZ_KP, 0, 0, 0, 0, 0);
        lift_PID = new PID(0.02, 0, 0, 0.015, 0, 0);

        intake.update();
        lift.update();

        if (mode == Modes.Circuit) {
            if ((a_button.edge() == -1 || b_button.edge() == -1 || y_button.edge() == -1) && stateForIntake != IntakeStates.Transfer && stateForIntake != IntakeStates.PickingConeUp) {
                stateForLift = LiftStates.LiftUp;
            }
        }
        //lift
        switch (stateForLift) {
            case LiftDown:
                if (mode == Modes.Fast) {
                    lift.setLiftTarget(LIFTDOWNPOSFAST);
                } else {
                    lift.setLiftTarget(LIFTDOWNPOS);
                }
                if (lift.getLiftCurrent() < 200 && dumped == true && (stateForIntake == IntakeStates.DrivingAround || stateForIntake == IntakeStates.LookingForCone)) {
                    if (lift_last_height == LIFTHIGHPOS || lift_last_height == LIFTMIDPOS) {
                        if (mode == Modes.Fast) {
                            lift.setDumper(DEPOSITTRANSFERFAST);
                        } else {
                            lift.setDumper(DEPOSITTRANSFER);
                        }
                        dumped = false;
                    }
                }
                liftTimerReset = false;
                if (lift_last_height == LIFTLOWPOS) {
                    if (dumped == true) {
                        if (liftTimer.seconds() > 0.6) {
                            lift.setDumper(DEPOSITLOW3);
                            dumped = false;
                        } else if (liftTimer.seconds() > 0.3) {
                            lift.setDumper(DEPOSITLOW2);
                        }
                    }
                    if (liftTimer.seconds() > 3) {
                        lift.setDumper(DEPOSITTRANSFER);
                    }
                }
                break;

            case LiftUp:
                if (mode == Modes.Fast) {
                    lift.setDumper(DEPOSITLIFTFAST);
                } else {
                    lift.setDumper(DEPOSITLIFT);
                }

                if (mode == Modes.Circuit) {
                    if (a_button.edge() == -1) {
                        lift.setLiftTarget(LIFTLOWPOS);
                        lift_trapezoid.reset();
                    }

                    if (b_button.edge() == -1) {
                        lift.setLiftTarget(LIFTMIDPOS);
                        lift_trapezoid.reset();
                    }

                    if (y_button.edge() == -1) {
                        lift.setLiftTarget(LIFTHIGHPOS);
                        lift_trapezoid.reset();
                    }
                } else if (mode == Modes.Fast){
                    lift.setLiftTarget(LIFTHIGHPOS);
                    if (Math.abs(lift.getLiftCurrent() - lift.getLiftTarget()) <= 30) {
                        stateForLift = LiftStates.Dump;
                    }
                }
                break;

            case Dump:
                lift_trapezoid_reset = false;

                if (lift.getLiftTarget() == LIFTMIDPOS) {
                    lift.setDumper(DEPOSITMID);
                }

                if (mode == Modes.Circuit) {
                    lift.setDumper(DEPOSITHIGH);
                } else if (mode == Modes.Fast) {
                    lift.setDumper(DEPOSITHIGHFAST);
                }

                if (!liftTimerReset) {
                    liftTimer.reset();
                    liftTimerReset = true;
                }

                lift_last_height = lift.getLiftTarget();

                if (lift.getLiftTarget() == LIFTLOWPOS) {
                    lift.setDumper(DEPOSITLOW);
                    if (liftTimer.seconds() > 0.2) {
                        dumped = true;
                        liftTimer.reset();
                        stateForLift = LiftStates.LiftDown;
                    }
                } else {
                    dumped = true;
                    stateForLift = LiftStates.LiftDown;
                }
                break;
        }
        //intake
        switch (stateForIntake) {
            case LookingForCone:
                intake.setArmTarget(ARMCOMPLETEDOWNPOS);
                intake.setWrist(WRISTLOOKINGFORCONE);
                intake.setClaw(CLAWOPENPOS);
                if (mode == Modes.Fast) {
                    if (!intakeTimerReset) {
                        intakeTimer.reset();
                        intakeTimerReset = true;
                    }
                    if (intakeTimer.seconds() > 0.1) {
                        intake.setHorizTarget(FASTMODEHORIZ);
                    }
                }
                if ((intake.getDistance() <= 16 || sense.edge() == -1) && Math.abs(ARMCOMPLETEDOWNPOS - intake.getArmCurrent()) < 60) {
                    stateForLift = LiftStates.LiftDown;
                    intakeTimerReset = false;
                    if (mode == Modes.Ground) {
                        GroundLow = true;
                        intake.setClaw(CLAWCLOSEPOS);
                        stateForIntake = IntakeStates.GroundDrivingAround;
                    } else {
                        stateForIntake = IntakeStates.PickingConeUp;
                    }
                }
                break;

            case PickingConeUp:
                intake.setClaw(CLAWCLOSEPOS);
                if (!intakeTimerReset) {
                    intakeTimer.reset();
                    intakeTimerReset = true;
                }
                if (intakeTimer.seconds() > 0.11 && lift.getLiftCurrent() < 40) {
                    intake.setWrist(WRISTTRANSFER);
                    if (intake.getHorizCurrent() < -50) {
                        intake.setArmTarget(ARMMIDPOS2);
                    } else {
                        intake.setArmTarget(ARMHIGHPOS);
                    }
                    intake.setHorizTarget(HORIZRETRACTED);
                    if (mode == Modes.Fast) {
                        lift.setDumper(DEPOSITTRANSFERFAST);
                    } else {
                        lift.setDumper(DEPOSITTRANSFER);
                    }
                }
                if (lift.getLiftCurrent() < 40 && intake.getHorizCurrent() > -15) {
                    if ((mode == Modes.Fast && intake.getArmCurrent() > -55) || (mode == Modes.Circuit && intake.getArmCurrent() > -35)) {
                        stateForIntake = IntakeStates.Transfer;
                        intakeTimerReset = false;
                    }
                }
                break;

            case Transfer:
                intake.setClaw(CLAWOPENPOS);
                if (!intakeTimerReset) {
                    intakeTimer.reset();
                    intakeTimerReset = true;
                }
                if (mode == Modes.Fast) {
                    if (intakeTimer.seconds() > 0.05) { //used to be 0.2 and 0.25
                        lift.setDumper(DEPOSITTRANSFERFAST2);
                    }
                    if (intakeTimer.seconds() > 0.15) {
                        if (!lift_trapezoid_reset) {
                            lift_trapezoid.reset();
                            lift_trapezoid_reset = true;
                        }
                        stateForLift = LiftStates.LiftUp;
                    }
                    if (intakeTimer.seconds() > 0.25) {
                        stateForIntake = IntakeStates.LookingForCone;
                        intakeTimerReset = false;
                    }
                }
                if (mode == Modes.Circuit) {
                    if (intakeTimer.seconds() > 0.3) {
                        lift.setDumper(DEPOSITTRANSFER2);
                    }
                    if (intakeTimer.seconds() > 0.4) {
                        stateForIntake = IntakeStates.DrivingAround;
                        intakeTimerReset = false;
                    }
                }
                break;

            case DrivingAround:
                intake.setArmTarget(ARMMIDPOS);
                if (x_button.edge() == -1) {
                    stateForIntake = IntakeStates.LookingForCone;
                }
                break;

            case GroundDrivingAround:
                if (!intakeTimerReset) {
                    intakeTimer.reset();
                    intakeTimerReset = true;
                }
                if (intakeTimer.seconds() > 0.5) {
                    intake.setArmTarget(ARMMIDPOS);
                    stateForIntake = IntakeStates.Ground;
                    intakeTimerReset = false;
                }
                break;

            case Ground:
                if (a_button.edge() == -1 && GroundLow) {
                    intake.setArmTarget(ARMLOWGOAL);
                    GroundLow = false;
                } else if (!GroundLow && a_button.edge() == -1) {
                    intake.setArmTarget(ARMGROUNDGOAL);
                    GroundLow = true;
                }

                if (dump.edge() == -1) {
                    intake.setClaw(CLAWOPENPOS);
                    intakeTimer.reset();
                    intakeTimerReset = true;
                }

                if (intakeTimerReset && intakeTimer.seconds() > 0.5) {
                    intake.setArmTarget(ARMMIDPOS);
                }

                if (x_button.edge() == -1) {
                    stateForIntake = IntakeStates.LookingForCone;
                }
                break;
        }

        if (switchFast.edge() == -1) {
            mode = Modes.Fast;
            lift_trapezoid.reset();
        }

        if (switchGround.edge() == -1) {
            mode = Modes.Ground;
            lift_trapezoid.reset();
        }

        if (switchCircuit.edge() == -1) {
            mode = Modes.Circuit;
            lift_trapezoid.reset();
        }

        if (dump.edge() == -1 && stateForLift == LiftStates.LiftUp) {
            stateForLift = LiftStates.Dump;
        }

//        if (rec_right_bumper.get() && rec_left_bumper.get() && (rec_right_trigger.get() >= 0.3) && (rec_left_trigger.get() >= 0.3)) { // reset encoders
//            lift.resetLiftEncoder();
//        }

        if(mode == Modes.Fast && stateForIntake == IntakeStates.LookingForCone) {
            FASTMODEHORIZ -= (ax_lift_left_x.get() * 24);
            if (FASTMODEHORIZ < MAXEXTENDEDHORIZ) FASTMODEHORIZ = MAXEXTENDEDHORIZ;
            if (FASTMODEHORIZ > 0) FASTMODEHORIZ = 0;
            intake.setHorizTarget(FASTMODEHORIZ);
        }

        double arm_power = arm_PID.getOutPut(intake.getArmTarget(), intake.getArmCurrent(), Math.cos(Math.toRadians((intake.getArmCurrent() * 1.25) + 136.5)));
        if (intake.getArmCurrent() < -24) {
            arm_power = Range.clip(arm_power, -ARMCLIPDOWNSLOW, ARMCLIPUP);
        } else {
            arm_power = Range.clip(arm_power, -ARMCLIPDOWN, ARMCLIPUP);
        }

        intake.setArmPow(arm_power);
        lift.setLiftPower(lift_PID.getOutPut(lift.getLiftTarget(), lift.getLiftCurrent(), 1) * Math.min(lift_trapezoid.seconds() * lift_accel, 1));
        intake.setHorizPow(horiz_PID.getOutPut(intake.getHorizTarget(), intake.getHorizCurrent(), 0));

        telemetry.addData("Arm Pow", arm_power);
        telemetry.addData("Lift Pow", lift_PID.getOutPut(lift.getLiftTarget(), lift.getLiftCurrent(), 1)  * Math.min(lift_trapezoid.seconds() * lift_accel, 1));
        telemetry.addData("Horiz Pow", horiz_PID.getOutPut(intake.getHorizTarget(), intake.getHorizCurrent(), 0));

        telemetry.addData("Arm Encoder", intake.getArmCurrent());
        telemetry.addData("Lift Encoder", lift.getLiftCurrent());
        telemetry.addData("Horiz Encoder", intake.getHorizCurrent());

        telemetry.addData("Arm Target", intake.getArmTarget());
        telemetry.addData("Lift Target", lift.getLiftTarget());
        telemetry.addData("Horiz Target", intake.getHorizTarget());

        telemetry.addData("Arm Limit", intake.getArmLimit());
        telemetry.addData("Horiz Limit", intake.getHorizLimit());
        telemetry.addData("Lift Limit", lift.getLift_limit());

        telemetry.addData("Current Intake State", stateForIntake);
        telemetry.addData("Current Lift State", stateForLift);
        telemetry.addData("Current Mode", mode);

        //telemetry.addData("Sensor Distance", intake.getDistance());
        telemetry.addData("Loop Time", loop.time());

    }
}
