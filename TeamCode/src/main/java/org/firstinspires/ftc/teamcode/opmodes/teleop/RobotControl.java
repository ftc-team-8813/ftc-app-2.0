package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.StateMachine;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Intake;
import org.firstinspires.ftc.teamcode.hardware.Lift;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.hardware.navigation.IntakeStates;
import org.firstinspires.ftc.teamcode.hardware.navigation.LiftStates;
import org.firstinspires.ftc.teamcode.hardware.navigation.PID;
import org.firstinspires.ftc.teamcode.input.ControllerMap;

import java.util.concurrent.TimeUnit;

public class RobotControl extends ControlModule{

    //add deadbands

    //Game objects
    private Lift lift;
    private Intake intake;
//  private Logger log = new Logger("Robot Control");

    //Timer
    private ElapsedTime timer;

    //PIDs
    private final PID arm_PID = new PID(0, 0, 0, 0, 0, 0);
    private final PID horiz_PID = new PID(0, 0, 0, 0, 0, 0);
    private final PID lift_PID = new PID(0, 0, 0, 0, 0, 0);

    //Controller Maps
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

    private ControllerMap.ButtonEntry switchMode;

    private ControllerMap.AxisEntry ax_lift_left_x;
    private ControllerMap.AxisEntry ax_lift_left_y;
    private ControllerMap.AxisEntry ax_lift_right_y;


    //Positions
    private final double clawOpenPos = 0.5;
    private final double clawClosePos = 0.5;

    //Switch Vars
    private LiftStates stateForLift;
    private IntakeStates stateForIntake;
    private int num;
    
    //Modes
    private boolean circuitMode;

    //Final Vars
    private final double INITLOOPPOWER = 0.3;
    private final double LIFTDOWNPOS = 0;
    private final double LIFTLOWPOS = 150;
    private final double LIFTMIDPOS = 410;
    private final double LIFTHIGHPOS = 660;
    private final double DUMPPOS = 0;
    private final double ORIGINDUMPPOS = 0;
    private final double ARMCOMPLETEDOWNPOS = 160;
    private final double ROTATLOOKINGFORCONE = 0.1;
    private final double MAXEXTENDEDHORIZ = 0;
    private final double ARMHIGHPOS = 0;
    private final double HORIZRETRACTED = 0;
    private final double LIFTTHRESHOLD = 20;
    private final double ROTATTRANSFER = 0.75;
    private final double ARMMIDPOS = 5;

    public RobotControl(String name) {
        super(name);
    }

    @Override
    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager) {
        circuitMode = true;

        timer = new ElapsedTime();

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

        switchMode = controllerMap.getButtonMap("switchMode", "gamepad2", "dpad_up");

        ax_lift_left_x = controllerMap.getAxisMap("lift:left_x", "gamepad2", "left_stick_x"); //finetuning
        ax_lift_left_y = controllerMap.getAxisMap("lift:left_y", "gamepad2", "left_stick_y");
        ax_lift_right_y = controllerMap.getAxisMap("lift:right_y", "gamepad2", "right_stick_y");


        stateForLift = LiftStates.LiftDown;
        stateForIntake = IntakeStates.LookingForCone;

        num = 1;
        timer.reset();
    }

    @Override
    public void init_loop(Telemetry telemetry) {
        super.init_loop(telemetry); // Do in AUTO End MAYBE

        intake.setArmPow(INITLOOPPOWER);
        lift.setLiftPower(INITLOOPPOWER);
        intake.setHorizPow(INITLOOPPOWER);

        if(intake.getArmLimit() && intake.getHorizLimit() && lift.getLift_limit()){
            lift.resetLiftEncoder();
            intake.resetIntakeEncoders();
        }

    }

    @Override
    public void update(Telemetry telemetry) {

        //lift
        switch (stateForLift) {
            case LiftDown:
                lift.setLiftTarget(LIFTDOWNPOS); //lift down
                break;

            case LiftUp:
                if (circuitMode) {
                    if (a_button.edge() == -1) {
                        lift.setLiftTarget(LIFTLOWPOS); //low pos
                    }

                    if (b_button.edge() == -1) { //change keybinds
                        lift.setLiftTarget(LIFTMIDPOS); //mid pos
                    }

                    if (y_button.edge() == -1) {
                        lift.setLiftTarget(LIFTHIGHPOS); //high pos
                    }
                } else {
                    lift.setLiftTarget(LIFTHIGHPOS); //high pos
                    stateForLift = LiftStates.Dump;
                }
                break;

            case Dump:
                lift.setDumper(DUMPPOS); //dump pos
                timer.reset();
                if (timer.time(TimeUnit.MILLISECONDS) == 1000) {
                    lift.setDumper(ORIGINDUMPPOS); //go back to normal pos, flip dump
                }
                break;
        }
        //intake
        switch (stateForIntake) {
            case LookingForCone:
                stateForLift = LiftStates.LiftDown;
                intake.setArmTarget(ARMCOMPLETEDOWNPOS); //armCompleteDown pos
                intake.setRotater(ROTATLOOKINGFORCONE);
                intake.setClaw(clawOpenPos);
                if (!circuitMode) {
                    intake.setHorizTarget(MAXEXTENDEDHORIZ); //max extended horiz, 264mm
                }
                if(intake.getDistance() <= 20 || sense.edge() == -1){
                    stateForIntake = IntakeStates.PickingConeUp;
                }
                break;

            case PickingConeUp:
                switch (num) {
                    case 1:
                        intake.setClaw(clawClosePos);
                        if (timer.seconds() == 1) {
                            intake.setArmTarget(ARMHIGHPOS); // arm High pos
                            intake.setHorizTarget(HORIZRETRACTED); //Horiz retracted pos
                        }
                        break;
                    case 2:
                        if (timer.seconds() == 1) {
                            if (lift.getEncoderVal() <= LIFTTHRESHOLD) { //threshold
                                num = 1;
                                timer.reset();
                            }
                        }
                        break;
                }
                break;

            case Transfer:
                intake.setClaw(clawOpenPos);
                intake.setRotater(ROTATTRANSFER);
                if (timer.seconds() == 1) { // ask john about time
                    if (circuitMode) {
                        intake.setArmTarget(ARMMIDPOS); //mid pos
                    } else {
                        stateForIntake = IntakeStates.LookingForCone;
                    }
                }
                if (timer.seconds() == 2.2) {
                    stateForLift = LiftStates.LiftUp;
                }
                break;
        }

        if(switchMode.edge() == -1) {
            circuitMode = !circuitMode;
            if(circuitMode){
                num = 2;
                timer.reset();
            }else{
                num = 1;
                timer.reset();
            }
        }


        if(lift.getLift_limit() && intake.getHorizLimit() && intake.getArmLimit()){
            stateForIntake = IntakeStates.Transfer;
            timer.reset();
        }

        if(dump.edge() == -1){
            stateForLift = LiftStates.Dump;
        }

        if (rec_right_bumper.get() && rec_left_bumper.get() && (rec_right_trigger.get() >= 0.3) && (rec_left_trigger.get() >= 0.3)) { // reset encoders
            lift.resetLiftEncoder();
        }

        intake.setHorizTarget(intake.getHorizTarget() + (ax_lift_left_x.get() * 16));
        lift.setLiftTarget(lift.getLiftTarget() + (ax_lift_left_y.get() * 16));
        intake.setArmTarget(intake.getArmTarget() + (ax_lift_right_y.get() * 16));

        intake.setArmPow(arm_PID.getOutPut(intake.getArmTarget(), intake.getArmCurrent(), Math.cos(Math.toRadians(intake.getArmCurrent() + 0))));
        lift.setLiftPower(lift_PID.getOutPut(lift.getLiftTarget(), lift.getEncoderVal(), 1));
        intake.setHorizPow(horiz_PID.getOutPut(intake.getHorizTarget(), intake.getHorizCurrent(), 0));
        }
    }

