//package org.firstinspires.ftc.teamcode.opmodes.test;
//
//import com.acmerobotics.dashboard.FtcDashboard;
//import com.acmerobotics.dashboard.config.Config;
//import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
//import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
//import com.qualcomm.robotcore.hardware.DcMotor;
//import com.qualcomm.robotcore.hardware.Gamepad;
//import com.qualcomm.robotcore.util.ElapsedTime;
//
//import org.firstinspires.ftc.robotcore.external.Telemetry;
//import org.firstinspires.ftc.teamcode.hardware.Lift;
//import org.firstinspires.ftc.teamcode.hardware.PID;
//import org.firstinspires.ftc.teamcode.hardware.PIDTuneArm;
//import org.firstinspires.ftc.teamcode.opmodes.LoggingOpMode;
//import org.firstinspires.ftc.teamcode.util.Logger;
//
//import java.lang.reflect.Field;
//import java.lang.reflect.Modifier;
//import java.util.ArrayList;
//
//
//@TeleOp(name="PIDTuningTest")
//public class PIDTuningTest extends LoggingOpMode {
//
//    private DcMotor arm_lower;
//    private PIDTuneArm arm_upper;
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
////    private double[] al_tune = {al_kp,al_ki,al_kd,al_mxis,al_a};
////
////    private double au_kp = 0.01;
////    private double au_ki = 0;
////    private double au_kd = 0;
////    private double au_mxis = 0;
////    private double au_a = 0;
////    private double[] au_tune = {au_kp,au_ki,au_kd,au_mxis,au_a};
////
//    private double wr_kp = 0.003;
//    private double wr_ki = 0;
//    private double wr_kd = 0;
//    private double wr_mxis = 0;
//    private double wr_a = 0;
//
////    private double[] wr_tune = {wr_kp,wr_ki,wr_kd,wr_mxis,wr_a};
////
////    private double[][] tune = {al_tune,au_tune,wr_tune};
//
//    private final PID arm_lower_pid = new PID(al_kp,al_ki,al_kd,al_mxis,al_a);
////    private final PID arm_upper_pid = new PID(au_kp,au_ki,au_kd,au_mxis,au_a);
//    private final PID wrist_pid = new PID(wr_kp,wr_ki,wr_kd,wr_mxis,wr_a);
////
////    private final PID arm_lower_pid = new PID(tune[0][0],tune[0][1],tune[0][2],tune[0][3],tune[0][4]);
////    private final PID arm_upper_pid = new PID(tune[1][0],tune[1][1],tune[1][2],tune[1][3],tune[1][4]);
////    private final PID wrist_pid = new PID(tune[2][0],tune[2][1],tune[2][2],tune[2][3],tune[2][4]);
//
////    private final PID arm_lower_pid = new PID(0.025,0,0,0,0);
////    private final PID arm_upper_pid = new PID(0.015,0,0,0,0);
////    private final PID wrist_pid = new PID(0.003,0,0,0,0);
//
//
//
//    private boolean up_down = false;
//    private boolean ga = false;
//
//
//    @Override
//    public void init() {
//
//        arm_lower = hardwareMap.get(DcMotor.class, "arm lower");
////        arm_upper = hardwareMap.get(DcMotor.class, "arm upper");
//        arm_upper = new PIDTuneArm(hardwareMap,"arm upper");
//        wrist = hardwareMap.get(DcMotor.class, "wrist");
//
////        arm_upper.setDirection(DcMotorSimple.Direction.REVERSE);
//
//        this.lift = new Lift(arm_lower, arm_upper.getArm(), wrist);
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
//
//        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
//    }
//
//    @Override
//    public void loop() {
//
////        double[] angles = lift.get_ang(ARM_LOWER_LENGTH, ARM_UPPER_LENGTH, x, y, 90, -90);
//
//        if(!gamepad2.a) {
//            ga = false;
//        }
//
//
//        double[] cur_angles = lift.getEncoderValue();
//        cur_angles[0] *= AL_DEGREES_PER_TICK;
//        cur_angles[1] *= AU_DEGREES_PER_TICK;
//        cur_angles[2] *= WRIST_DEGREES_PER_TICK;
//
//        double al_f = Math.cos(Math.toRadians(cur_angles[0])) * 0.2;
//        double au_f = Math.cos(Math.toRadians(cur_angles[0]) + Math.toRadians(cur_angles[1]));
//
//
//        if (gamepad2.a && !ga) {
//            up_down = !up_down;
//            arm_upper.startMotionProfile();
//            ga = true;
//
//        }
////        double al_tar_ang;
////        double au_tar_ang;
////        double wrist_tar_ang;
////
////        if (!up_down) {
////            al_tar_ang = 90;
////            au_tar_ang = -90;
////            wrist_tar_ang = 0;
////        }
////        else {
////            al_tar_ang = 90;
////            au_tar_ang = 0;
////            wrist_tar_ang = 0;
////        }
//
//
//        double al_pow = arm_lower_pid.getOutPut(al_tar_ang, cur_angles[0], al_f);
//        double au_pow = arm_upper.getOutPut(au_tar_ang, cur_angles[1], au_f);
//        double wrist_pow = -1 * wrist_pid.getOutPut(wrist_tar_ang, cur_angles[2], 0);
//
////        double al_pow = arm_lower_pid.getOutPut(90,cur_angles[0], al_f);
////        double au_pow = arm_upper_pid.getOutPut(-90,cur_angles[1], au_f);
////        double wrist_pow = -1 * wrist_pid.getOutPut(0,cur_angles[2],wr_f);
//
//        lift.setLiftPower(al_pow,au_pow,wrist_pow);
//
//
//
//
//        telemetry.addData("AL Target Angle",al_tar_ang);
//        telemetry.addData("AU Target Angle",au_tar_ang);
//        telemetry.addData("WR Target Angle",wrist_tar_ang);
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
//        telemetry.update();
//    }
//}
