package org.firstinspires.ftc.teamcode.opmodes.test;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.opmodes.LoggingOpMode;


@TeleOp(name="Amp Test")
public class AmpTest extends LoggingOpMode {

    private DcMotorEx horizontal;

    @Override
    public void init() {
        horizontal = hardwareMap.get(DcMotorEx.class,"horizontal");
        horizontal.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        horizontal.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        // 2145
        super.init();
    }

    @Override
    public void loop() {

//        horizontal.setPower(-0.3);

        // horizontal 3 is reset
        telemetry.addData("Horizontal Position", horizontal.getCurrentPosition());
//        telemetry.addData("Horizontal Amp Current", horizontal.getCurrent(CurrentUnit.AMPS));
        telemetry.update();
    }
}