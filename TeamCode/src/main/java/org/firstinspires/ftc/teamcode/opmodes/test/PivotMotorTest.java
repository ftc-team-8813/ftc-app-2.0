package org.firstinspires.ftc.teamcode.opmodes.test;

import com.hubspot.jinjava.util.Logging;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.opmodes.LoggingOpMode;

@TeleOp(name = "PivotMotorTest")
public class PivotMotorTest extends LoggingOpMode {
    private DcMotor pivot;
    private DcMotor lift1;
    private DcMotor lift2;

    @Override
    public void init() {
        super.init();
        pivot = hardwareMap.get(DcMotor.class, "pivot");
        lift1 = hardwareMap.get(DcMotor.class, "lift1");
        lift2 = hardwareMap.get(DcMotor.class, "lift2");
    }

    @Override
    public void loop() {
        pivot.setPower(gamepad1.left_stick_x * 0.3);
        lift1.setPower(gamepad1.left_stick_y * 0.9);
        lift2.setPower(gamepad1.right_stick_y * 0.9);


        if (gamepad1.a){
            pivot.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            pivot.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        } else if (gamepad1.b){
            lift1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            lift1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }

        telemetry.addData("Pivot Encoder: ", pivot.getCurrentPosition());
        telemetry.addData("Lift Encoder: ", lift1.getCurrentPosition());
        telemetry.update();
    }
}
