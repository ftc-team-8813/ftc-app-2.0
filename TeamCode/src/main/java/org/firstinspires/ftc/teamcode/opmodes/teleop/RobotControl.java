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
import org.firstinspires.ftc.teamcode.hardware.navigation.OdometryNav;
import org.firstinspires.ftc.teamcode.hardware.navigation.PID;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.opmodes.util.FTCDVS;

@Config

public class RobotControl extends ControlModule{

    private Lift lift;
    private Intake intake;

    private ElapsedTime intakeTimer;
    private boolean intakeTimerReset = false;
    private ElapsedTime liftTimer;
    private boolean liftTimerReset = false;
    private ElapsedTime loop;

    private ElapsedTime lift_trapezoid;
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
    private Modes stateForMode;

    private double LIFTDOWNPOS = 0;
    private double LIFTLOWPOS = 135;
    private double LIFTMIDPOS = 410;
    private double LIFTHIGHPOS = 720;

    private double DEPOSITLOW = 0.4;
    private double DEPOSITLOW2 = 0.58;
    private double DEPOSITLOW3 = 0;
    private double DEPOSITMID = 0.33;
    private double DEPOSITHIGH = 0.33;

    private double DEPOSITHIGHFAST = 0.38;

    private double DEPOSITTRANSFER = 0.15;
    private double DEPOSITLIFT = 0.33;

    private double ARMCOMPLETEDOWNPOS = -124;
    private double ARMMIDPOS = -35;
    private double ARMHIGHPOS = 0;
    private double ARMLOWGOAL = -40;
    private double ARMGROUNDGOAL = -80;

    private double WRISTLOOKINGFORCONE = 0.019;
    private double WRISTTRANSFER = 0.678;

    private double MAXEXTENDEDHORIZ = -800;
    private double FASTMODEHORIZ = -450;
    private double HORIZRETRACTED = 0;

    private double CLAWOPENPOS = 0.3;
    private double CLAWCLOSEPOS = 0.1;

    private PID arm_PID;
    private PID horiz_PID;
    private PID lift_PID;
    private boolean GroundLow;

    public static double ARMCLIPDOWN = 0.6;
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
        stateForMode = Modes.Circuit;

        lift.setDumper(DEPOSITTRANSFER);

        GroundLow = false;
    }

    @Override
    public void init_loop(Telemetry telemetry) {
        super.init_loop(telemetry);

        if(!intake.getArmLimit()){
            intake.setArmPow(0.5);
        }
        if(!lift.getLift_limit()){
            lift.setLiftPower(-0.2);
        }
        if(!intake.getHorizLimit()){
            intake.setHorizPow(0.3);
        }

        if(intake.getArmLimit()){
            intake.resetArmEncoder();
        }
        if(lift.getLift_limit()){
            lift.resetLiftEncoder();
        }
        if(intake.getHorizLimit()){
            intake.resetHorizEncoder();
        }
    }

    @Override
    public void update(Telemetry telemetry) {
        loop.reset();

        arm_PID = new PID(0.000342, 0, 0, 0, 0, 0);
        horiz_PID = new PID(0.01, 0, 0, 0, 0, 0);
        lift_PID = new PID(0.02, 0, 0, 0.015, 0, 0);


        if (stateForMode == Modes.Circuit) {
            if ((a_button.edge() == -1 || b_button.edge() == -1 || y_button.edge() == -1) && stateForIntake != IntakeStates.Transfer && stateForIntake != IntakeStates.PickingConeUp) {
                stateForLift = LiftStates.LiftUp;
            }
        }
        //lift
        switch (stateForLift) {
            case LiftDown:
                lift.setLiftTarget(LIFTDOWNPOS);
                if (lift.getEncoderVal() < 200 && dumped == true && (stateForIntake == IntakeStates.DrivingAround || stateForIntake == IntakeStates.LookingForCone)) {
                    if (lift_last_height == LIFTHIGHPOS || lift_last_height == LIFTMIDPOS) {
                        lift.setDumper(DEPOSITTRANSFER);
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
                lift.setDumper(DEPOSITLIFT);
                if (stateForMode == Modes.Circuit) {
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
                } else if (stateForMode == Modes.Fast){
                    if (stateForLift == LiftStates.LiftDown) {
                        lift_trapezoid.reset();
                    }
                    lift.setLiftTarget(LIFTHIGHPOS);
                    if (Math.abs(lift.getEncoderVal() - lift.getLiftTarget()) <= 30) {
                        stateForLift = LiftStates.Dump;
                    }
                }
                break;

            case Dump:

                if (lift.getLiftTarget() == LIFTMIDPOS) {
                    lift.setDumper(DEPOSITMID);
                }

                if (stateForMode == Modes.Circuit) {
                    lift.setDumper(DEPOSITHIGH);
                } else if (stateForMode == Modes.Fast) {
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
                if (stateForMode == Modes.Fast) {
                    if (!intakeTimerReset) {
                        intakeTimer.reset();
                        intakeTimerReset = true;
                    }
                    if (intakeTimer.seconds() > 0.3) {
                        intake.setHorizTarget(FASTMODEHORIZ);
                    }
                }
                if ((intake.getDistance() <= 16 || sense.edge() == -1) && Math.abs(ARMCOMPLETEDOWNPOS - intake.getArmCurrent()) < 80) {
                    stateForLift = LiftStates.LiftDown;
                    intakeTimerReset = false;
                    if (stateForMode == Modes.Ground) {
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
                if (intakeTimer.seconds() > 0.35 && lift.getEncoderVal() < 10) {
                    intake.setWrist(WRISTTRANSFER);
                    intake.setArmTarget(ARMHIGHPOS);
                    intake.setHorizTarget(HORIZRETRACTED);
                    lift.setDumper(DEPOSITTRANSFER);
                }
                if (lift.getLift_limit() && (intake.getHorizCurrent() > -10) && intake.getArmCurrent() > -30) {
                    stateForIntake = IntakeStates.Transfer;
                    intakeTimerReset = false;
                }
                break;

            case Transfer:
                intake.setClaw(CLAWOPENPOS);
                if (!intakeTimerReset) {
                    intakeTimer.reset();
                    intakeTimerReset = true;
                }
                if (intakeTimer.seconds() >= 0.5) {
                    if (stateForMode == Modes.Circuit) {
                        stateForIntake = IntakeStates.DrivingAround;
                    } else if (stateForMode == Modes.Fast) {
                        lift_trapezoid.reset();
                        stateForLift = LiftStates.LiftUp;
                        stateForIntake = IntakeStates.LookingForCone;
                    }
                    intakeTimerReset = false;
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
            stateForMode = Modes.Fast;
        }

        if (switchGround.edge() == -1) {
            stateForMode = Modes.Ground;
        }

        if (switchCircuit.edge() == -1) {
            stateForMode = Modes.Circuit;
        }

        if (dump.edge() == -1 && stateForLift == LiftStates.LiftUp) {
            stateForLift = LiftStates.Dump;
        }

        if (rec_right_bumper.get() && rec_left_bumper.get() && (rec_right_trigger.get() >= 0.3) && (rec_left_trigger.get() >= 0.3)) { // reset encoders
            lift.resetLiftEncoder();
        }

//            if (lift.getLiftTarget() < 814 && lift.getLiftTarget() > 0) {
//                lift.setLiftTarget(lift.getLiftTarget() + (ax_lift_left_y.get() * 12));
//            }
//            if (intake.getArmTarget() < 0 && intake.getArmTarget() > -124) {
//                intake.setArmTarget(intake.getArmTarget() + (ax_lift_right_y.get() * 12));
//            }
        if(intake.getHorizTarget() < 0 && intake.getHorizTarget() > MAXEXTENDEDHORIZ) {
            FASTMODEHORIZ = intake.getHorizTarget() + (ax_lift_left_x.get() * 16);
        }


        double arm_power = Range.clip(arm_PID.getOutPut(intake.getArmTarget(), intake.getArmCurrent(), Math.cos(Math.toRadians(intake.getArmCurrent() + 0))), -ARMCLIPDOWN, ARMCLIPUP);
//
//        if(arm_power >= -0.3 && arm_power < 0 && intake.getArmCurrent() < -60) {
//            intake.setArmPow(0);
//        } else {
//            intake.setArmPow(arm_power);
//        }
        intake.setArmPow(arm_power);
        lift.setLiftPower(lift_PID.getOutPut(lift.getLiftTarget(), lift.getEncoderVal(), 1) * Math.min(lift_trapezoid.seconds() * lift_accel, 1));
        intake.setHorizPow(horiz_PID.getOutPut(intake.getHorizTarget(), intake.getHorizCurrent(), 0));


        telemetry.addData("Arm Pow", arm_power);
        telemetry.addData("Lift Pow", lift_PID.getOutPut(lift.getLiftTarget(), lift.getEncoderVal(), 1)  * Math.min(lift_trapezoid.seconds() * lift_accel, 1));
        telemetry.addData("Horiz Pow", horiz_PID.getOutPut(intake.getHorizTarget(), intake.getHorizCurrent(), 0));

        telemetry.addData("Arm Encoder", intake.getArmCurrent());
        telemetry.addData("Lift Encoder", lift.getEncoderVal());
        telemetry.addData("Horiz Encoder", intake.getHorizCurrent());

        telemetry.addData("Arm Limit", intake.getArmLimit());
        telemetry.addData("Horiz Limit", intake.getHorizLimit());
        telemetry.addData("Lift Limit", lift.getLift_limit());

        telemetry.addData("Current Intake State", stateForIntake);
        telemetry.addData("Current Lift State", stateForLift);
        telemetry.addData("Current Mode", stateForMode);

        telemetry.addData("Sensor Distance", intake.getDistance());
        telemetry.addData("Time", loop.time());
        telemetry.addData("Sensor Distance", intake.getDistance());

    }
}
