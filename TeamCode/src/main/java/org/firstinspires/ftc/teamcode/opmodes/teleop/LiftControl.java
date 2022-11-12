package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Intake;
import org.firstinspires.ftc.teamcode.hardware.Lift;
import org.firstinspires.ftc.teamcode.hardware.navigation.PID;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.util.Logger;

public class LiftControl extends ControlModule { // TODO make lift fast

    private Lift lift;
    private Intake intake;
    private Logger log = new Logger("Lift Control");

    private ElapsedTime timer = new ElapsedTime();

    private final double ARM_LOWER_LENGTH = 488.89580;
    private final double ARM_UPPER_LENGTH = 424.15230;

    private double x = 70;
    private double y = 370;

    private double wr_constant = 0;

    private final double AL_DEGREES_PER_TICK = -(360.0/8192.0);
    private final double AU_DEGREES_PER_TICK = (360.0/8192.0);
    private final double WRIST_DEGREES_PER_TICK = (360.0/128.0);

    private final PID arm_lower = new PID(0.023,0.0001,0.00091, 0.2,100,0.8);
    private final PID arm_upper = new PID(0.027,0.00228,0.001,0.14,100,0.8); // 0.029, 0.0022, 0.001 then 0.027, 0.00228
    private final PID wrist = new PID(0.02,0,0,0,0,0);

    private boolean intaken = false;
    private boolean passthrough = true;

    private ControllerMap.AxisEntry ax_lift_left_x;
    private ControllerMap.AxisEntry ax_lift_left_y;

    private ControllerMap.AxisEntry ax_lift_right_y;

    private ControllerMap.ButtonEntry dpad_up;
    private ControllerMap.ButtonEntry dpad_right;

    private ControllerMap.ButtonEntry y_button;
    private ControllerMap.ButtonEntry b_button;
    private ControllerMap.ButtonEntry a_button;
    private ControllerMap.ButtonEntry x_button;

    private ControllerMap.ButtonEntry rec_right_bumper;
    private ControllerMap.AxisEntry rec_right_trigger;
    private ControllerMap.ButtonEntry rec_left_bumper;
    private ControllerMap.AxisEntry rec_left_trigger;

    public LiftControl(String name) {
        super(name);
    }

    @Override
    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager) {
        this.lift = robot.lift;
        this.intake = robot.intake;
        ax_lift_left_x = controllerMap.getAxisMap("lift:left_x", "gamepad2", "left_stick_x");
        ax_lift_left_y = controllerMap.getAxisMap("lift:left_y", "gamepad2", "left_stick_y");
        ax_lift_right_y = controllerMap.getAxisMap("lift:right_y", "gamepad2", "right_stick_y");

        y_button = controllerMap.getButtonMap("lift:high","gamepad1","y");
        b_button = controllerMap.getButtonMap("lift:mid","gamepad1","b");
        a_button = controllerMap.getButtonMap("lift:low","gamepad1","a");
        x_button = controllerMap.getButtonMap("lift:ground","gamepad1","x");

        rec_right_bumper = controllerMap.getButtonMap("lift:reset_encoder_rb","gamepad2","right_bumper");
        rec_right_trigger = controllerMap.getAxisMap("lift:reset_encoder_rt","gamepad2","right_trigger");
        rec_left_bumper = controllerMap.getButtonMap("lift:reset_encoder_lb","gamepad2","left_bumper");
        rec_left_trigger = controllerMap.getAxisMap("lift:reset_encoder_lt","gamepad2","left_trigger");

        dpad_up = controllerMap.getButtonMap("lift:pass_through_up","gamepad2","dpad_up");
        dpad_right = controllerMap.getButtonMap("lift:pass_through_down","gamepad2","dpad_right");
//        lift.resetLiftEncoder();
    }

    @Override
    public void update(Telemetry telemetry) {

        if (rec_right_bumper.get() && rec_left_bumper.get() && (rec_right_trigger.get() >= 0.3) && (rec_left_trigger.get() >= 0.3)) { // reset encoders
            lift.resetLiftEncoder();
        }

        wr_constant -= ax_lift_right_y.get() * 2;


        if (((intake.getClawPosition() == 0.63) && (intake.getDistance() < 20) && (y < 10)) && !intaken) {

            if (timer.seconds() > 0.5) {
                x = 70;
                y = 370;
                intaken = true;
            }
        }
        else {
            timer.reset();
        }

        if(intake.getClawPosition() == 0.11) {
            intaken = false;
        }

        if (dpad_up.edge() == -1) {
            x = -403;
            y = 798;
        }

        if (dpad_right.edge() == -1) {
            x = 465;
            y = -40;
        }

        if (y_button.edge() == -1) { // high
            x = 100;
            y = 870;
        }

        if (b_button.edge() == -1) { // mid
            x = 70;
            y = 580;
        }

        if (a_button.edge() == -1) { // low
            x = 70;
            y = 370;
        }

        if (x_button.edge() == -1) { // ground
            x = 320;
            y = -40;
        }

        x += (ax_lift_left_x.get() * 6);
        y += (-ax_lift_left_y.get() * 6);

        if ((x > -315.0) && (x < 55) && (y < 200))
        {
            y = 200;
        }

        double[] angles = new double[2];

        if (Math.sqrt(Math.pow(x,2) + Math.pow(y,2)) >= (488.89580+424.15230-5)) {
            angles[0] = Math.toDegrees(Math.atan2(y,x));
            angles[1] = angles[0];
        }
        else {
            angles = lift.get_ang(ARM_LOWER_LENGTH, ARM_UPPER_LENGTH, x, y, 90, -90);
        }

        double[] cur_angles = lift.getEncoderValue();
        cur_angles[0] *= AL_DEGREES_PER_TICK;
        cur_angles[1] *= AU_DEGREES_PER_TICK;
        cur_angles[2] *= -WRIST_DEGREES_PER_TICK;
        cur_angles[0] += 0;//149.39559808298318;
        cur_angles[1] += 0;//-165.8935546875;
        cur_angles[2] += 0;//-2.8125;

        double al_f = Math.cos(Math.toRadians(cur_angles[0]));
        double au_f = Math.cos(Math.toRadians(cur_angles[0]) + Math.toRadians(cur_angles[1]));


        double al_pow = arm_lower.getOutPut(angles[0], cur_angles[0], al_f);
        double au_pow = -1 * arm_upper.getOutPut((-angles[0] + angles[1]), cur_angles[1], au_f);
        double wrist_pow = wrist.getOutPut((-angles[1] + wr_constant), cur_angles[2], 0);



        lift.setLiftPower(Range.clip(al_pow,-0.5,0.9), Range.clip(au_pow,-0.7,0.7), wrist_pow);


        telemetry.addData("AL Target Angle",angles[0]);
        telemetry.addData("AU Target Angle",(-1*(angles[0] - angles[1])));
        telemetry.addData("WR Target Angle",-angles[1]);

        telemetry.addData("AL Angle",cur_angles[0]);
        telemetry.addData("AU Angle",cur_angles[1]);
        telemetry.addData("WR Angle",cur_angles[2]);

        telemetry.addData("AL Power",al_pow);
        telemetry.addData("AU Power",au_pow);
        telemetry.addData("WR Power",wrist_pow);


        telemetry.addData("X", x);
        telemetry.addData("Y", y);
    }
}
