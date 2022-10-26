//package org.firstinspires.ftc.teamcode.opmodes.test;
//
//import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
//import com.qualcomm.robotcore.hardware.DcMotor;
//import com.qualcomm.robotcore.hardware.DcMotorSimple;
//
//import org.firstinspires.ftc.teamcode.hardware.Lift;
//import org.firstinspires.ftc.teamcode.hardware.PID;
//import org.firstinspires.ftc.teamcode.hardware.Robot;
//import org.firstinspires.ftc.teamcode.opmodes.LoggingOpMode;
//import org.firstinspires.ftc.teamcode.util.Logger;
//
//@TeleOp(name="ArmMotorsTest")
//public class ArmMotorsTest extends LoggingOpMode {
//
//    private DcMotor arm_lower;
//    private DcMotor arm_upper;
//    private DcMotor wrist;
//
//    private Lift lift;
//
//    private double[] past_angles = new double[2];
//
//    private Logger log = new Logger("Lift Control");
//
//    private final double ARM_LOWER_LENGTH = 488.89580;
//    private final double ARM_UPPER_LENGTH = 424.15230;
//
//    private double x = 0;
//    private double y = 115;
//
//    private final double AL_DEGREES_PER_TICK = (360.0/(28.0*108.8*32.0/15.0));
//    private final double AU_DEGREES_PER_TICK = (360.0/8192.0);
//    private final double WRIST_DEGREES_PER_TICK = (360.0/128.0);
//
//    private double al_kp = 0.025;
//    private double al_ki = 0;
//    private double al_kd = 0;
//    private double al_mxis = 0;
//    private double al_a = 0;
//    private double[] al_tune = {al_kp,al_ki,al_kd,al_mxis,al_a};
//
//    private double au_kp = 0.015;
//    private double au_ki = 0;
//    private double au_kd = 0;
//    private double au_mxis = 0;
//    private double au_a = 0;
//    private double[] au_tune = {au_kp,au_ki,au_kd,au_mxis,au_a};
//
//    private double wr_kp = 0.003;
//    private double wr_ki = 0;
//    private double wr_kd = 0;
//    private double wr_mxis = 0;
//    private double wr_a = 0;
//    private double[] wr_tune = {wr_kp,wr_ki,wr_kd,wr_mxis,wr_a};
//
//    private double[][] tune = {al_tune,au_tune,wr_tune};
//
//    private final PID arm_lower_pid = new PID(tune[0][0],tune[0][1],tune[0][2],tune[0][3],tune[0][4]);
//    private final PID arm_upper_pid = new PID(tune[1][0],tune[1][1],tune[1][2],tune[1][3],tune[1][4]);
//    private final PID wrist_pid = new PID(tune[2][0],tune[2][1],tune[2][2],tune[2][3],tune[2][4]);
//
////    private final PID arm_lower_pid = new PID(0.025,0,0,0,0);
////    private final PID arm_upper_pid = new PID(0.015,0,0,0,0);
////    private final PID wrist_pid = new PID(0.003,0,0,0,0);
//    private int cur_tune_part = 0;
//    private int cur_tune = 0;
//    private String cur_tune_part_name = "";
//    private String cur_tune_name = "";
//
//    @Override
//    public void init() {
//
//        arm_lower = hardwareMap.get(DcMotor.class, "arm lower");
//        arm_upper = hardwareMap.get(DcMotor.class, "arm upper");
//        wrist = hardwareMap.get(DcMotor.class, "wrist");
//
////        arm_upper.setDirection(DcMotorSimple.Direction.REVERSE);
//
//        this.lift = new Lift(arm_lower, arm_upper, wrist);
//
//        super.init();
//
////        arm_lower = hardwareMap.get(DcMotor.class, "arm lower");
////        arm_upper = hardwareMap.get(DcMotor.class, "arm upper");
////        wrist = hardwareMap.get(DcMotor.class, "wrist");
////        ax_lift_left_x = controllerMap.getAxisMap("lift:left_x", "gamepad2", "left_stick_x");
////        ax_lift_left_y = controllerMap.getAxisMap("lift:right_y", "gamepad2", "left_stick_y");
//
//
//        lift.resetLiftEncoder();
//    }
//
//    @Override
//    public void loop() {
//
//        x += (gamepad1.left_stick_x * 2);
//        y += (-gamepad1.left_stick_y * 2);
//
//        if (y < -100)
//        {
//            y = -100;
//        }
//
//        if ((x > -315.0) && (x < 55) && (y < 100))
//        {
//            y = 100;
//        }
//
//
//
//
//        double[] angles = lift.get_ang(ARM_LOWER_LENGTH, ARM_UPPER_LENGTH, x, y, 90, -90);
//
////        if ((angles[0] > 150.0) || (angles[0] < -10))
////        {
////            angles = past_angles;
////        }
//
//        double[] cur_angles = lift.getEncoderValue();
//        cur_angles[0] *= AL_DEGREES_PER_TICK;
//        cur_angles[1] *= AU_DEGREES_PER_TICK;
//        cur_angles[2] *= WRIST_DEGREES_PER_TICK;
//
//        double al_f = Math.cos(Math.toRadians(cur_angles[0])) * 0.2;
//
//
//
//        double al_pow = arm_lower_pid.getOutPut(angles[0],cur_angles[0],al_f);
//        double au_pow = arm_upper_pid.getOutPut((-1*(angles[0] - angles[1])),cur_angles[1],0);
//        double wrist_pow = -1 * wrist_pid.getOutPut((-1 *angles[1]),cur_angles[2],0);
//
//
//
////        double al_pow = arm_lower_pid.getOutPut(90,cur_angles[0], al_f);
////        double au_pow = arm_upper_pid.getOutPut(-90,cur_angles[1], au_f);
////        double wrist_pow = -1 * wrist_pid.getOutPut(0,cur_angles[2],wr_f);
//
//        lift.setLiftPower(al_pow,au_pow,wrist_pow);
//
//        past_angles = angles;
//        telemetry.addData("AL Target Angle",angles[0]);
//        telemetry.addData("AU Target Angle",(-1*(angles[0] - angles[1])));
//        telemetry.addData("WR Target Angle",(-1 *angles[1]));
//
//        telemetry.addData("AL Angle",cur_angles[0]);
//        telemetry.addData("AU Angle",cur_angles[1]);
//        telemetry.addData("WR Angle",cur_angles[2]);
//
//        telemetry.addData("Arm Lower Power",al_pow);
//        telemetry.addData("Arm Upper Power",au_pow);
//        telemetry.addData("Wrist Power",wrist_pow);
//
//
//        telemetry.addData("X", x);
//        telemetry.addData("Y", y);
//
//
//        if (gamepad2.left_bumper && (cur_tune_part != 0)) {
//            cur_tune_part--;
//            cur_tune = 0;
//        }
//        if (gamepad2.right_bumper && (cur_tune_part != 3)) {
//            cur_tune_part++;
//            cur_tune = 0;
//        }
//
//        if ((gamepad2.left_trigger > 0.1)  && (cur_tune != 0)) {
//            cur_tune--;
//        }
//        if ((gamepad2.right_trigger > 0.1) && (cur_tune != 4)) {
//            cur_tune++;
//        }
//
//        if (gamepad2.x) {
//            tune[cur_tune_part][cur_tune] += 0.1;
//        }
//        if (gamepad2.y) {
//            tune[cur_tune_part][cur_tune] += 0.01;
//        }
//        if (gamepad2.b) {
//            tune[cur_tune_part][cur_tune] += 0.001;
//        }
//
//        if (gamepad2.dpad_left) {
//            tune[cur_tune_part][cur_tune] -= 0.1;
//        }
//        if (gamepad2.dpad_up) {
//            tune[cur_tune_part][cur_tune] -= 0.01;
//        }
//        if (gamepad2.dpad_right) {
//            tune[cur_tune_part][cur_tune] -= 0.001;
//        }
//
//
//        if (cur_tune_part == 0) {
//            cur_tune_part_name = "Arm Lower";
//        }
//        else if (cur_tune_part == 1) {
//            cur_tune_part_name = "Arm Upper";
//        }
//        else {
//            cur_tune_part_name = "Wrist";
//        }
//
//        if (cur_tune == 0) {
//            cur_tune_name = "KP";
//        }
//        else if (cur_tune == 1) {
//            cur_tune_name = "KI";
//        }
//        else if (cur_tune == 2) {
//            cur_tune_name = "KD";
//        }
//        else if (cur_tune == 3) {
//            cur_tune_name = "Max Integral Sum";
//        }
//        else {
//            cur_tune_name = "A";
//        }
//
//        telemetry.addData("Tuning Part", cur_tune_part_name);
//        telemetry.addData("Tuning", cur_tune_name);
//        telemetry.addData("Tune Value",tune[cur_tune_part][cur_tune]);
//        System.out.println(tune);
//
//    }
//}
