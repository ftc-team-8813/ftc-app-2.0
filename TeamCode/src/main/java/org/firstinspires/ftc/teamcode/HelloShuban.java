package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp()

public class HelloShuban extends OpMode{
    DcMotorEx front_left;
    DcMotorEx front_right;
    DcMotorEx back_right;
    DcMotorEx back_left;

    @Override
    public void init() {
        DcMotorEx front_left = hardwareMap.get(DcMotorEx.class,"front_left");
        DcMotorEx front_right = hardwareMap.get(DcMotorEx.class,"front_right");
        DcMotorEx back_left = hardwareMap.get(DcMotorEx.class,"back_left");
        DcMotorEx back_right = hardwareMap.get(DcMotorEx.class,"back_right");

        front_right.setDirection(DcMotorSimple.Direction.REVERSE);
        back_right.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    @Override
    public void loop() {
        double y = -gamepad1.left_stick_y;
        double x = gamepad1.left_stick_x;
        double rot = gamepad1.right_stick_x;
        front_left.setPower(y+x+rot);
        front_right.setPower(y-x-rot);
        back_left.setPower(y-x+rot);
        back_right.setPower(y+x-rot);
    }


}