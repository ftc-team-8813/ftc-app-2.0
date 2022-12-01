package org.firstinspires.ftc.teamcode.opmodes.test;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.opmodes.LoggingOpMode;

@TeleOp(name = "ResetClawEncoder")
public class ResetClawEncoderTest extends LoggingOpMode {

    private DcMotor wrist;

    @Override
    public void init() {
        super.init();
        wrist = hardwareMap.get(DcMotor.class, "wrist");
    }

    @Override
    public void start() {
        super.start();
        wrist.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        wrist.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    @Override
    public void loop() {

        telemetry.addData("WR", wrist.getCurrentPosition());

    }
}
