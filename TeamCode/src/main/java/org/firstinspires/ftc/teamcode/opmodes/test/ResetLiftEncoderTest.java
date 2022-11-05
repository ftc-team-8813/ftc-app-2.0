package org.firstinspires.ftc.teamcode.opmodes.test;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.hardware.Lift;
import org.firstinspires.ftc.teamcode.opmodes.LoggingOpMode;

import java.lang.annotation.Annotation;

@TeleOp(name = "ResetLiftEncoders")
public class ResetLiftEncoderTest extends LoggingOpMode {

    private DcMotor arm_lower;
    private DcMotor arm_upper;
    private DcMotor wrist;

    @Override
    public void init() {
        super.init();

        arm_lower = hardwareMap.get(DcMotor.class, "arm lower");
        arm_upper = hardwareMap.get(DcMotor.class, "arm upper");
        wrist = hardwareMap.get(DcMotor.class, "wrist");
    }

    @Override
    public void start() {
        super.start();
        arm_lower.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        arm_upper.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        wrist.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        arm_lower.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        arm_upper.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        wrist.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    @Override
    public void loop() {

    }
}
