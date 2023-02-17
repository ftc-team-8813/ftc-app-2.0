package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Arm;
import org.firstinspires.ftc.teamcode.hardware.Horizontal;
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

    private Lift lift;
    private Intake intake;
    private Arm arm;
    private Horizontal horizontal;

    private ElapsedTime intakeTimer;
    private boolean intakeTimerReset = false;
    private ElapsedTime liftTimer;
    private boolean liftTimerReset = false;
    private ElapsedTime loop;

    private ElapsedTime lift_trapezoid;
    private boolean lift_trapezoid_reset = false;
    private double lift_accel = 0.4; //bigger is faster accel
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

    private ControllerMap.ButtonEntry testswitchFast;
    private ControllerMap.ButtonEntry testswitchGround;
    private ControllerMap.ButtonEntry testswitchCircuit;

    private ControllerMap.AxisEntry ax_lift_left_x;

    private ControllerMap.AxisEntry horiz_fwd;
    private ControllerMap.AxisEntry horiz_back;

    private ControllerMap.AxisEntry ax_lift_left_y;
    private ControllerMap.AxisEntry ax_lift_right_y;

    private LiftStates stateForLift;
    private IntakeStates stateForIntake;
    private Modes mode;

    private double LIFTDOWNPOS = 0;
    private double LIFTDOWNPOSFAST = 35;
    private double LIFTLOWPOS = 135;
    private double LIFTMIDPOS = 435;
    private double LIFTHIGHPOS = 740;
    private double LIFTHIGHPOSFAST = 735;

    private double DEPOSITLOW = 0.38;
    private double DEPOSITLOW2 = 0.5;
    private double DEPOSITLOW3 = 0;
    private double DEPOSITMID = 0.40;
    private double DEPOSITHIGH = 0.40;

    private double DEPOSITHIGHFAST = 0.42;

    private double DEPOSITTRANSFER = 0.0975;
    private double DEPOSITTRANSFER2 = 0.13;
    private double DEPOSITTRANSFERFAST = 0.095;
    private double DEPOSITTRANSFERFAST2 = 0.13;

    private double DEPOSITLIFT = 0.38;
    private double DEPOSITLIFTFAST = 0.28;

    private double ARMCOMPLETEDOWNPOS = -120;
    private double ARMMIDPOS = -20;
    private double ARMMIDPOS2 = -28; //used while the horiz slide is retracting
    private double ARMHIGHPOS = 50; //positive to make it dig into the end stop

    public static double ARMLOWGOAL = -55;
    public static double ARMGROUNDGOAL = -105;

    private double WRISTLOOKINGFORCONE = 0.019;
    private double WRISTTRANSFER = 0.678;

    private double MAXEXTENDEDHORIZ = -800;
    private double FASTMODEHORIZ = -410;
    private double ADJUSTHORIZ = 0;
    private double HORIZRETRACTED = 0;

    public static double HORIZ_KP = 0.008;
    public static double HORIZ_KP_FINE = 0.002;

    private double horiz_kp_var = HORIZ_KP; //varies between the two values above

    private double CLAWOPENPOS = 0.3;
    private double CLAWCLOSEPOS = 0.1;

    private PID arm_PID;
    private PID horiz_PID;
    private PID lift_PID;
    private boolean GroundLow;

    public static double ARMCLIPDOWN = 1;
    public static double ARMCLIPDOWNSLOW = 0.2;
    public static double ARMCLIPUP = 1;

    public static double LIFT_KP = 0.015;

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
        this.arm = robot.arm;
        this.horizontal = robot.horizontal;

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

        testswitchFast = controllerMap.getButtonMap("testswitchFast", "gamepad1", "dpad_up");
        testswitchGround = controllerMap.getButtonMap("testswitchGround", "gamepad1", "dpad_down");
        testswitchCircuit = controllerMap.getButtonMap("testswitchCircuit", "gamepad1", "dpad_right");

        ax_lift_left_x = controllerMap.getAxisMap("lift:left_x", "gamepad2", "left_stick_x");//finetuning

        horiz_fwd = controllerMap.getAxisMap("testhoriz:fwd", "gamepad1", "right_trigger");//finetuning
        horiz_back = controllerMap.getAxisMap("testhoriz:back", "gamepad1", "left_trigger");//finetuning

        ax_lift_left_y = controllerMap.getAxisMap("lift:left_y", "gamepad2", "left_stick_y");
        ax_lift_right_y = controllerMap.getAxisMap("lift:right_y", "gamepad2", "right_stick_y");

        stateForLift = LiftStates.LiftDown;
        stateForIntake = IntakeStates.DrivingAround;
        mode = Modes.Circuit;

        lift.setHolderPosition(DEPOSITTRANSFER);

        GroundLow = false;

        arm.setPower(0.5);
        lift.setPower(-0.2);
        horizontal.setPower(0.3);

        //horizontal.setHorizTarget(0); //used for run to position
    }

    @Override
    public void init_loop(Telemetry telemetry) {
        super.init_loop(telemetry);

        if(arm.getLimit()){
            arm.resetEncoders();
            arm.setPower(0);
        }
        if(lift.getLimit()){
            lift.resetEncoders();
            lift.setPower(0);
        }
        if(horizontal.getLimit()){
            horizontal.resetEncoders();
            horizontal.setPower(0);
            //intake.setHorizPow(1); //this scales the speed for run to position; it won't move until you set a target
        }
    }

    @Override
    public void update(Telemetry telemetry) {
        loop.reset();

        arm_PID = new PID(0.009, 0, 0, 0.1, 0, 0);
        horiz_PID = new PID(horiz_kp_var, 0, 0, 0, 0, 0);
        lift_PID = new PID(LIFT_KP, 0, 0, 0.03, 0, 0);

//        intake.update();
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
                    if (lift_last_height == LIFTHIGHPOS || lift_last_height == LIFTMIDPOS || lift_last_height == LIFTHIGHPOSFAST) {
                        if (mode == Modes.Fast) {
                            lift.setHolderPosition(DEPOSITTRANSFERFAST);
                        } else {
                            lift.setHolderPosition(DEPOSITTRANSFER);
                        }
                        dumped = false;
                    }
                }
                liftTimerReset = false;
                if (lift_last_height == LIFTLOWPOS) {
                    if (dumped == true) {
                        if (liftTimer.seconds() > 0.01) {
                            lift.setHolderPosition(DEPOSITLOW3);
                            dumped = false;
                        }
                    }
                    if (liftTimer.seconds() > 2.1 && liftTimer.seconds() < 2.2) {
                        lift.setHolderPosition(DEPOSITTRANSFER);
                    }
                }
                break;

            case LiftUp:
                if (mode == Modes.Fast) {
                    lift.setHolderPosition(DEPOSITLIFTFAST);
                } else {
                    lift.setHolderPosition(DEPOSITLIFT);
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
                    lift.setHolderPosition(DEPOSITMID);
                }

                if (mode == Modes.Circuit) {
                    lift.setHolderPosition(DEPOSITHIGH);
                } else if (mode == Modes.Fast) {
                    lift.setHolderPosition(DEPOSITHIGHFAST);
                }

                if (!liftTimerReset) {
                    liftTimer.reset();
                    liftTimerReset = true;
                }

                lift_last_height = lift.getLiftTarget();

                if (lift.getLiftTarget() == LIFTLOWPOS) {
                    lift.setHolderPosition(DEPOSITLOW);
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
                arm.setArmTarget(ARMCOMPLETEDOWNPOS);
                intake.setWristPosition(WRISTLOOKINGFORCONE);
                intake.setClawPosition(CLAWOPENPOS);
                if (mode == Modes.Fast) {
                    if (!intakeTimerReset) {
                        intakeTimer.reset();
                        intakeTimerReset = true;
                    }
                    if (intakeTimer.seconds() > 0.1) {
                        horizontal.setHorizTarget(FASTMODEHORIZ);
                    }
                }
                if ((intake.getDistance() <= 17 || sense.edge() == -1) && Math.abs(ARMCOMPLETEDOWNPOS - arm.getCurrentPosition()) < 60) {
                    stateForLift = LiftStates.LiftDown;
                    intakeTimerReset = false;
                    if (mode == Modes.Ground) {
                        GroundLow = true;
                        intake.setClawPosition(CLAWCLOSEPOS);
                        stateForIntake = IntakeStates.GroundDrivingAround;
                    } else {
                        stateForIntake = IntakeStates.PickingConeUp;
                    }
                }
                break;

            case PickingConeUp:
                intake.setClawPosition(CLAWCLOSEPOS);
                if (!intakeTimerReset) {
                    intakeTimer.reset();
                    intakeTimerReset = true;
                }
                if (intakeTimer.seconds() > 0.11 && lift.getLiftCurrent() < 40) {
                    intake.setWristPosition(WRISTTRANSFER);
                    if (horizontal.getCurrentPosition() < -50) {
                        arm.setArmTarget(ARMMIDPOS2);
                    } else {
                        arm.setArmTarget(ARMHIGHPOS);
                    }
                    horizontal.setHorizTarget(HORIZRETRACTED);
                    if (mode == Modes.Fast) {
                        lift.setHolderPosition(DEPOSITTRANSFERFAST);
                    } else {
                        lift.setHolderPosition(DEPOSITTRANSFER);
                    }
                }
                if (lift.getLiftCurrent() < 40 && horizontal.getCurrentPosition() > -15) {
                    if ((mode == Modes.Fast && arm.getCurrentPosition() > -55) || (mode == Modes.Circuit && arm.getCurrentPosition() > -45)) {
                        stateForIntake = IntakeStates.Transfer;
                        intakeTimerReset = false;
                    }
                }
                break;

            case Transfer:
                intake.setClawPosition(CLAWOPENPOS);
                if (!intakeTimerReset) {
                    intakeTimer.reset();
                    intakeTimerReset = true;
                }
                if (mode == Modes.Fast) {
                    if (intakeTimer.seconds() > 0.05) { //used to be 0.2 and 0.25
                        lift.setHolderPosition(DEPOSITTRANSFERFAST2);
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
                        lift.setHolderPosition(DEPOSITTRANSFER2);
                    }
                    if (intakeTimer.seconds() > 0.4) {
                        stateForIntake = IntakeStates.DrivingAround;
                        intakeTimerReset = false;
                    }
                }
                break;

            case DrivingAround:
                if (!intakeTimerReset) {
                    intakeTimer.reset();
                    intakeTimerReset = true;
                }
                if (mode == Modes.Circuit) {
                    arm.setArmTarget(0);
                    if (intakeTimer.seconds() > 0.4) {
                        arm.setArmTarget(ARMMIDPOS);
                    }
                } else {
                    arm.setArmTarget(ARMMIDPOS);
                }

                ADJUSTHORIZ = 0;
                if (x_button.edge() == -1) {
                    intakeTimerReset = false;
                    stateForIntake = IntakeStates.LookingForCone;
                }
                break;

            case GroundDrivingAround:
                if (!intakeTimerReset) {
                    intakeTimer.reset();
                    intakeTimerReset = true;
                }
                if (intakeTimer.seconds() > 0.2) {
                    arm.setArmTarget(ARMMIDPOS);
                    stateForIntake = IntakeStates.Ground;
                    intakeTimerReset = false;
                }
                break;

            case Ground:
                if (a_button.edge() == -1 && GroundLow) {
                    arm.setArmTarget(ARMLOWGOAL);
                    GroundLow = false;
                } else if (!GroundLow && a_button.edge() == -1) {
                    arm.setArmTarget(ARMGROUNDGOAL);
                    GroundLow = true;
                }

                if (dump.edge() == -1) {
                    intake.setClawPosition(CLAWOPENPOS);
                    intakeTimer.reset(); //after the cone is dropped, the timer resets. . .
                    intakeTimerReset = true;
                }

                if (intakeTimerReset && intakeTimer.seconds() > 0.1) { //. . . causing the arm to go up
                    arm.setArmTarget(ARMMIDPOS);
                }

                if (x_button.edge() == -1) {
                    stateForIntake = IntakeStates.LookingForCone;
                }
                break;
        }

        if (testswitchFast.edge() == -1) {
            mode = Modes.Fast;
            lift_trapezoid.reset();
        }

        if (testswitchGround.edge() == -1) {
            mode = Modes.Ground;
            lift_trapezoid.reset();
        }

        if (testswitchCircuit.edge() == -1) {
            mode = Modes.Circuit;
            lift_trapezoid.reset();
        }

        if (dump.edge() == -1 && stateForLift == LiftStates.LiftUp) {
            stateForLift = LiftStates.Dump;
        }

//        if (rec_right_bumper.get() && rec_left_bumper.get() && (rec_right_trigger.get() >= 0.3) && (rec_left_trigger.get() >= 0.3)) { // reset encoders
//            lift.resetLiftEncoder();
//        }

        if(stateForIntake == IntakeStates.LookingForCone || stateForIntake == IntakeStates.GroundDrivingAround || stateForIntake == IntakeStates.Ground) {
            horiz_kp_var = HORIZ_KP_FINE;
            if (mode == Modes.Fast) {
                FASTMODEHORIZ -= ((horiz_fwd.get() - horiz_back.get()) * 24);
                if (FASTMODEHORIZ < MAXEXTENDEDHORIZ) FASTMODEHORIZ = MAXEXTENDEDHORIZ;
                if (FASTMODEHORIZ > 0) FASTMODEHORIZ = 0;
                horizontal.setHorizTarget(FASTMODEHORIZ);
            } else {
                ADJUSTHORIZ -= ((horiz_fwd.get() - horiz_back.get()) * 90);
                if (ADJUSTHORIZ < MAXEXTENDEDHORIZ) ADJUSTHORIZ = MAXEXTENDEDHORIZ;
                if (ADJUSTHORIZ > 0) ADJUSTHORIZ = 0;
                horizontal.setHorizTarget(ADJUSTHORIZ);
            }
        } else {
            horiz_kp_var = HORIZ_KP;
        }

        double arm_power = arm_PID.getOutPut(arm.getArmTarget(), arm.getCurrentPosition(), Math.cos(Math.toRadians((arm.getCurrentPosition() * 1.25) + 136.5)));
        if (arm.getCurrentPosition() < -24) {
            arm_power = Range.clip(arm_power, -ARMCLIPDOWNSLOW, ARMCLIPUP);
        } else {
            arm_power = Range.clip(arm_power, -ARMCLIPDOWN, ARMCLIPUP);
        }

        arm.setPower(arm_power);
        lift.setPower(lift_PID.getOutPut(lift.getLiftTarget(), lift.getLiftCurrent(), 1) * Math.min(lift_trapezoid.seconds() * lift_accel, 1));
        horizontal.setPower(horiz_PID.getOutPut(horizontal.getHorizTarget(), horizontal.getCurrentPosition(), 0));

        telemetry.addData("Arm Pow", arm_power);
        telemetry.addData("Lift Pow", lift_PID.getOutPut(lift.getLiftTarget(), lift.getLiftCurrent(), 1)  * Math.min(lift_trapezoid.seconds() * lift_accel, 1));
        telemetry.addData("Horiz Pow", horiz_PID.getOutPut(horizontal.getHorizTarget(), horizontal.getCurrentPosition(), 0));

        telemetry.addData("Arm Encoder", arm.getCurrentPosition());
        telemetry.addData("Lift Encoder", lift.getLiftCurrent());
        telemetry.addData("Horiz Encoder", horizontal.getCurrentPosition());

        telemetry.addData("Arm Target", arm.getArmTarget());
        telemetry.addData("Lift Target", lift.getLiftTarget());
        telemetry.addData("Horiz Target", horizontal.getHorizTarget());

        telemetry.addData("Arm Limit", arm.getLimit());
        telemetry.addData("Horiz Limit", horizontal.getLimit());
        telemetry.addData("Lift Limit", lift.getLimit());

        telemetry.addData("Current Intake State", stateForIntake);
        telemetry.addData("Current Lift State", stateForLift);
        telemetry.addData("Current Mode", mode);

        //telemetry.addData("Sensor Distance", intake.getDistance());
        telemetry.addData("Loop Time", loop.time());

    }
}