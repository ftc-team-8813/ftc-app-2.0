package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Intake;
import org.firstinspires.ftc.teamcode.hardware.Lift;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.hardware.navigation.PID;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.util.Logger;

public class RobotControl extends ControlModule{

    //Game objects
    private Lift lift;
    private Intake intake;
//    private Logger log = new Logger("Robot Control");

    //Timer
    private ElapsedTime timer = new ElapsedTime();

    //PIDs
    private final PID arm_PID = new PID();
    private final PID horiz_PID = new PID();
    private final PID lift_PID = new PID();

    //States
    private boolean ARM_RAISED;
    private boolean CLAW_CLOSED;
    private boolean CONE_SENSED; //maybe we don't need this one
    private boolean ARM_MID_POS;
    private boolean HORIZ_EXT;
    private boolean LIFT_RAISED;
    private boolean DUMP_FLIPPED;

    //Controller Maps
    private ControllerMap.ButtonEntry y_button;
    private ControllerMap.ButtonEntry b_button;
    private ControllerMap.ButtonEntry a_button;
    private ControllerMap.ButtonEntry x_button;

    private ControllerMap.ButtonEntry rec_right_bumper;
    private ControllerMap.AxisEntry rec_right_trigger;
    private ControllerMap.ButtonEntry rec_left_bumper;
    private ControllerMap.AxisEntry rec_left_trigger;

//    private ControllerMap.AxisEntry ax_lift_left_x; //Ask John about finetuning
//    private ControllerMap.AxisEntry ax_lift_left_y;

    //Switch Vars
    private int num;

    public RobotControl(String name) {
        super(name);
    }

    @Override
    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager) {
        this.lift = robot.lift;
        this.intake = robot.intake;

        ARM_RAISED = false;
        CLAW_CLOSED = false; //I think, double check with John
        CONE_SENSED = false;
        ARM_MID_POS = false;
        HORIZ_EXT = true;
        LIFT_RAISED = false;
        DUMP_FLIPPED = false;

        y_button = controllerMap.getButtonMap("lift:high","gamepad1","y");
        b_button = controllerMap.getButtonMap("lift:mid","gamepad1","b");
        a_button = controllerMap.getButtonMap("lift:low","gamepad1","a");
        x_button = controllerMap.getButtonMap("lift:ground","gamepad1","x");

        rec_right_bumper = controllerMap.getButtonMap("lift:reset_encoder_rb","gamepad2","right_bumper");
        rec_right_trigger = controllerMap.getAxisMap("lift:reset_encoder_rt","gamepad2","right_trigger");
        rec_left_bumper = controllerMap.getButtonMap("lift:reset_encoder_lb","gamepad2","left_bumper");
        rec_left_trigger = controllerMap.getAxisMap("lift:reset_encoder_lt","gamepad2","left_trigger");

//        ax_lift_left_x = controllerMap.getAxisMap("lift:left_x", "gamepad2", "left_stick_x"); //finetuning
//        ax_lift_left_y = controllerMap.getAxisMap("lift:right_y", "gamepad2", "left_stick_y");

        lift.resetLiftEncoder();
        intake.resetIntakeEncoders();

        num = 0;
    }

    @Override
    public void update(Telemetry telemetry) {
        switch (num){
            case 1:
                intake.setArmPow(arm_PID.getOutPut(intake.setArmTarget(), intake.getEncoderVals()[0],0)); //ask john what to put for feed forward, and ask if my code is right
                if(intake.getArmTarget() == intake.getEncoderVals()[0]){  //add error band
                    ARM_RAISED = false; //double check
                }
            case 2:
        }


    }
}
