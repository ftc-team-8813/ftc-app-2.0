package org.firstinspires.ftc.teamcode.opmodes.test;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.hardware.navigation.PID;
import org.firstinspires.ftc.teamcode.opmodes.LoggingOpMode;

@Config
public class ArmTest extends LoggingOpMode {

    private DcMotorEx arm;

    public static double target = 0;

    private final PID pid = new PID(0.0095, 0, 0, 0, 0, 0);

    @Override
    public void init() {
        arm = hardwareMap.get(DcMotorEx.class,"arm");
        arm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        arm.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        super.init();
    }

    @Override
    public void loop() {

        double power = Range.clip(pid.getOutPut(target, arm.getCurrentPosition(), Math.cos(Math.toRadians(arm.getCurrentPosition() + 0))), -0.6, 0.6);

        arm.setPower(power);

        telemetry.addData("Arm Position",-arm.getCurrentPosition() * 288.0 / 8192.0);
        telemetry.addData("Arm Power", power);
        telemetry.addData("Target",target);
        telemetry.update();
    }
}