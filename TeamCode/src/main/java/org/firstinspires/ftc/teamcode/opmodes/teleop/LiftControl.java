package org.firstinspires.ftc.teamcode.opmodes.teleop;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Lift;
import org.firstinspires.ftc.teamcode.hardware.PID;
import org.firstinspires.ftc.teamcode.hardware.PIDTuneArm;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.util.Logger;

public class LiftControl extends ControlModule {

    private PIDTuneArm arm_upper = new PIDTuneArm();

    private Lift lift;
    private Logger log = new Logger("Lift Control");

    private final double ARM_LOWER_LENGTH = 488.89580;
    private final double ARM_UPPER_LENGTH = 424.15230;

    private double x = 0;
    private double y = 115;

    private double set_x = 0;
    private double set_y = 115;


    private final double AL_DEGREES_PER_TICK = (360.0/(28.0*108.8*32.0/15.0));
    private final double AU_DEGREES_PER_TICK = (360.0/8192.0);
    private final double WRIST_DEGREES_PER_TICK = (360.0/128.0);

    private final PID arm_lower = new PID(0.025,0,0,0,0);
    private final PID wrist = new PID(0.02,0,0,0,0);

    private ControllerMap.AxisEntry ax_lift_left_x;
    private ControllerMap.AxisEntry ax_lift_left_y;

    private ControllerMap.ButtonEntry right_bumper;

    private ControllerMap.ButtonEntry dpad_up;
    private ControllerMap.ButtonEntry dpad_right;
    private ControllerMap.ButtonEntry dpad_down;
    private ControllerMap.ButtonEntry dpad_left;

    private ControllerMap.ButtonEntry left_bumper;

    private ControllerMap.AxisEntry left_trigger;

    private boolean claw_open = false;

    private boolean move_arm = false;

    private ControllerMap.ButtonEntry a;

    public LiftControl(String name) {
        super(name);
    }

    @Override
    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager) {
        this.lift = robot.lift;
        ax_lift_left_x = controllerMap.getAxisMap("lift:left_x", "gamepad2", "left_stick_x");
        ax_lift_left_y = controllerMap.getAxisMap("lift:right_y", "gamepad2", "left_stick_y");

        right_bumper = controllerMap.getButtonMap("lift:claw","gamepad1","right_bumper");

        dpad_up = controllerMap.getButtonMap("lift:high","gamepad2","dpad_up");
        dpad_right = controllerMap.getButtonMap("lift:mid","gamepad2","dpad_right");
        dpad_down = controllerMap.getButtonMap("lift:low","gamepad2","dpad_down");
        dpad_left = controllerMap.getButtonMap("lift:ground","gamepad2","dpad_left");

        left_bumper = controllerMap.getButtonMap("lift:arm_out","gamepad1","left_bumper");

        left_trigger = controllerMap.getAxisMap("lift:arm_in","gamepad1","left_trigger");

        lift.resetLiftEncoder();

        a = controllerMap.getButtonMap("lift:move_arm", "gamepad2","a");
    }

    @Override
    public void update(Telemetry telemetry) {

        if (left_trigger.get() >= 0.3) { // go in
            x = 0;
            y = 115;
            arm_upper.startMotionProfile();
        }

        if (left_bumper.edge() == -1) { // go out
            x = set_x;
            y = set_y;
            arm_upper.startMotionProfile();
        }

        if (dpad_up.edge() == -1) { // high
            set_x = -400;
            set_y = 800;
            arm_upper.startMotionProfile();
        }

        if (dpad_right.edge() == -1) { // mid
            set_x = -400;
            set_y = 550;
            arm_upper.startMotionProfile();
        }

        if (dpad_down.edge() == -1) { // low
            set_x = -400;
            set_y = 295;
            arm_upper.startMotionProfile();
        }

        if (dpad_left.edge() == -1) { // ground
            set_x = (488.89580+424.15230);
            set_y = 0;
            arm_upper.startMotionProfile();
        }



        if (right_bumper.edge() == -1) {
            claw_open = !claw_open;
        }

        if (claw_open) {
            lift.setClaw(0.416);
        }

        else {
            lift.setClaw(0.204);
        }

        x += (ax_lift_left_x.get() * 3);
        y += (-ax_lift_left_y.get() * 3);

        if (y < -100)
        {
            y = -100;
        }

        if ((x > -315.0) && (x < 55) && (y < 100))
        {
            y = 100;
        }

        double[] angles = lift.get_ang(ARM_LOWER_LENGTH, ARM_UPPER_LENGTH, x, y, 90, -90);

        double[] cur_angles = lift.getEncoderValue();
        cur_angles[0] *= AL_DEGREES_PER_TICK;
        cur_angles[1] *= AU_DEGREES_PER_TICK;
        cur_angles[2] *= WRIST_DEGREES_PER_TICK;
        cur_angles[0] += -5.871684611344538;
        cur_angles[1] += 6.6796875;
        cur_angles[2] += 25.5; // or 22.5

        double al_f = Math.cos(Math.toRadians(cur_angles[0])) * 0.2; // make work for negative
        double au_f = Math.cos(Math.toRadians(cur_angles[0]) + Math.toRadians(cur_angles[1]));

        if (Math.sqrt(Math.pow(x,2) + Math.pow(y,2)) >= (488.89580+424.15230-5)) {
            angles[1] = angles[0];
        }


        double al_pow = arm_lower.getOutPut(angles[0], cur_angles[0], al_f);
        double au_pow = arm_upper.getOutPut((-angles[0] + angles[1]), cur_angles[1], au_f);
        double wrist_pow = -1 * wrist.getOutPut(-angles[1], cur_angles[2], 0);

        if (a.edge() == -1) {
            move_arm = !move_arm;
        }

        if (move_arm) {
            lift.setLiftPower(al_pow, au_pow, wrist_pow);
        }

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
