package org.firstinspires.ftc.teamcode.opmodes.test;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.hardware.Intake;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.hardware.navigation.PID;

@Config

@TeleOp(name="Arm Test")
public class ArmTest extends OpMode {

    Robot robot;
    Intake intake;
    ElapsedTime arm_trapezoid;
    PID armPID;
    double target;
    public static double armAccel;
    public static double KPArm = 0.0095;
    public static double KFArm;
    public static double KIArm;
    public static double KDArm;
    public static double ARMCLIPDOWN = 0.4;
    public static double ARMCLIPUP = 1;
    
    @Override
    public void init() {
        robot = Robot.initialize(hardwareMap);
        intake = robot.intake;
        armAccel = 1000;
        arm_trapezoid = new ElapsedTime();
        intake.resetArmEncoder();
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
    }

    @Override
    public void loop() {
        target = Range.clip(target + (gamepad1.right_stick_y * 2), -120, 0);
        armPID = new PID(KPArm,KIArm,KDArm,KFArm,0,0);
        double arm_power = Range.clip(armPID.getOutPut(intake.getArmTarget(), intake.getArmCurrent(), 0), -ARMCLIPDOWN, ARMCLIPUP) + (Math.cos(Math.toRadians((intake.getArmCurrent() * 1.25) + 136.5)) * KFArm) * Math.min(arm_trapezoid.seconds() * armAccel, 1);
        if(gamepad1.a){
            arm_trapezoid.reset();
            intake.setArmTarget(target);
        }
        intake.setArmPow(arm_power);
        telemetry.addData("Arm Target: ", target);
        telemetry.addData("Arm Current: ", intake.getArmCurrent());
        telemetry.addData("Arm Degrees: ", (intake.getArmCurrent() * 1.25) + 136.5);
        telemetry.addData("Arm Power: ", arm_power);

        telemetry.update();
    }
}

//low = -120
// kf = 0.5