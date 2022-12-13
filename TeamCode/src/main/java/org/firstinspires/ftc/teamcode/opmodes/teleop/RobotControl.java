package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Intake;
import org.firstinspires.ftc.teamcode.hardware.Lift;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.hardware.navigation.IntakeStates;
import org.firstinspires.ftc.teamcode.hardware.navigation.LiftStates;
import org.firstinspires.ftc.teamcode.hardware.navigation.PID;
import org.firstinspires.ftc.teamcode.input.ControllerMap;

public class RobotControl extends ControlModule{

    //PID stuff

    //Game objects
    private Lift lift;
    private Intake intake;
//  private Logger log = new Logger("Robot Control");

    //Timer
    private ElapsedTime timer;
    private boolean timerReset = false;

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

    //Switch Vars
    private LiftStates stateForLift;
    private IntakeStates stateForIntake;
    
    //Modes
    private boolean circuitMode;

    //Final Vars
    private final double INITLOOPPOWER = 0.3; //check

    private final double LIFTDOWNPOS = 0;
    private final double LIFTLOWPOS = 150;
    private final double LIFTMIDPOS = 410;
    private final double LIFTHIGHPOS = 660;

    private final double DEPOSITLOW = 0.39;
    private final double DEPOSITMID = 0.33;
    private final double DEPOSITHIGH = 0.32;

    private final double DEPOSITTRANSFER = 0.17;
    private final double DEPOSITLIFT = 0.25;

    private final double ARMCOMPLETEDOWNPOS = -118;
    private final double ARMMIDPOS = -60;
    private final double ARMHIGHPOS = 0;

    private final double WRISTLOOKINGFORCONE = 0.019;
    private final double WRISTTRANSFER = 0.678;

    private final double MAXEXTENDEDHORIZ = -800;
    private final double HORIZRETRACTED = 0;

    private final double CLAWOPENPOS = 0.23;
    private final double CLAWCLOSEPOS = 0.37;


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

        lift.setDumper(DEPOSITTRANSFER);
    }

    @Override
    public void init_loop(Telemetry telemetry) {
        super.init_loop(telemetry);

        intake.setArmPow(0.3);
        lift.setLiftPower(-0.2);
        intake.setHorizPow(-0.3);

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
                lift.setLiftTarget(LIFTDOWNPOS);
                break;

            case LiftUp:
                lift.setDumper(DEPOSITLIFT);
                if (circuitMode) {
                    if (a_button.edge() == -1) {
                        lift.setLiftTarget(LIFTLOWPOS);
                    }

                    if (b_button.edge() == -1) {
                        lift.setLiftTarget(LIFTMIDPOS);
                    }

                    if (y_button.edge() == -1) {
                        lift.setLiftTarget(LIFTHIGHPOS);
                    }
                } else {
                    lift.setLiftTarget(LIFTHIGHPOS);
                    if (Math.abs(lift.getEncoderVal() - lift.getLiftTarget()) <= 30){
                        stateForLift = LiftStates.Dump;
                    }
                }
                break;

            case Dump:
                if(lift.getLiftTarget() == LIFTLOWPOS){
                    lift.setDumper(DEPOSITLOW);
                }
                if(lift.getLiftTarget() == LIFTMIDPOS){
                    lift.setDumper(DEPOSITMID);
                }
                if(lift.getLiftTarget() == LIFTHIGHPOS){
                    lift.setDumper(DEPOSITHIGH);
                }
                if (!timerReset) {
                    timer.reset();
                    timerReset = true;
                }

                if (timer.seconds() > 0.4) {
                    stateForLift = LiftStates.LiftDown;
                    timerReset = false;
                }
                break;
        }
        //intake
        switch (stateForIntake) {
            case LookingForCone:
                intake.setArmTarget(ARMCOMPLETEDOWNPOS);
                intake.setWrist(WRISTLOOKINGFORCONE);
                intake.setClaw(CLAWOPENPOS);
                if (!circuitMode) {
                    intake.setHorizTarget(MAXEXTENDEDHORIZ);
                }
                if(intake.getDistance() <= 20 || sense.edge() == -1){
                    stateForIntake = IntakeStates.PickingConeUp;
                }
                break;

            case PickingConeUp:
                intake.setClaw(CLAWCLOSEPOS);
                if (!timerReset) {
                    timer.reset();
                    timerReset = true;
                }
                if (timer.seconds() > 0.5 && stateForLift == LiftStates.LiftDown) {
                    intake.setWrist(WRISTTRANSFER);
                    intake.setArmTarget(ARMHIGHPOS);
                    intake.setHorizTarget(HORIZRETRACTED);
                    lift.setDumper(DEPOSITTRANSFER);
                }
                if(lift.getLift_limit() && intake.getHorizLimit() && intake.getArmLimit()){
                    stateForIntake = IntakeStates.Transfer;
                    timerReset = false;
                }
                break;

            case Transfer:
                intake.setClaw(CLAWOPENPOS);
                if (!timerReset) {
                    timer.reset();
                    timerReset = true;
                }
                if (timer.seconds() >= 0.5) {
                    if (circuitMode) {
                        stateForIntake = IntakeStates.DrivingAround;
                    } else {
                        stateForIntake = IntakeStates.LookingForCone;
                    }
                    stateForLift = LiftStates.LiftUp;
                    timerReset = false;
                }
                break;

            case DrivingAround:
                intake.setArmTarget(ARMMIDPOS);
                if (x_button.edge() == -1) {
                    stateForIntake = IntakeStates.LookingForCone;
                }
                break;
        }

        if(switchMode.edge() == -1) {
            circuitMode = !circuitMode;
        }

        if(dump.edge() == -1 && stateForLift == LiftStates.LiftUp){
            stateForLift = LiftStates.Dump;
        }

        if (rec_right_bumper.get() && rec_left_bumper.get() && (rec_right_trigger.get() >= 0.3) && (rec_left_trigger.get() >= 0.3)) { // reset encoders
            lift.resetLiftEncoder();
        }

        if(lift.getEncoderVal() < 814 && lift.getEncoderVal() > 0){
            lift.setLiftTarget(lift.getLiftTarget() + (ax_lift_left_y.get() * 16));
        }
        if(intake.getArmCurrent() > 0 && intake.getArmCurrent() < -124){
            intake.setArmTarget(intake.getArmTarget() + (ax_lift_right_y.get() * 16));
        }
        if(intake.getHorizCurrent() > 0 && intake.getHorizCurrent() < -800){
            intake.setHorizTarget(intake.getHorizTarget() + (ax_lift_left_x.get() * 16));
        }

        intake.setArmPow(arm_PID.getOutPut(intake.getArmTarget(), intake.getArmCurrent(), Math.cos(Math.toRadians(intake.getArmCurrent() + 0))));
        lift.setLiftPower(lift_PID.getOutPut(lift.getLiftTarget(), lift.getEncoderVal(), 1));
        intake.setHorizPow(horiz_PID.getOutPut(intake.getHorizTarget(), intake.getHorizCurrent(), 0));
        }
    }