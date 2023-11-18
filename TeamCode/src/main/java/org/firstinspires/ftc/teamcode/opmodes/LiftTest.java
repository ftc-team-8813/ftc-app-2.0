package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.hardware.Lift;


@TeleOp(name = "Lift Test")
public class LiftTest extends OpMode {


    private Lift lift;


    @Override
    public void init() {

        DcMotorEx lift1 = hardwareMap.get(DcMotorEx.class, "lift1");
        DcMotorEx lift2 = hardwareMap.get(DcMotorEx.class, "lift2");

        lift = new Lift(lift1, lift2);

        lift.resetEncoders();
    }

    @Override
    public void loop() {
        lift.update();

        lift.setLiftsPower(gamepad1.left_stick_y);
        telemetry.addData("Lift Current", lift.getCurrentPosition());
        telemetry.addData("Lift Power", lift.getLiftPower());
    }
}
