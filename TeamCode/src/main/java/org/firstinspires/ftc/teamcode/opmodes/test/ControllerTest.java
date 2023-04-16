package org.firstinspires.ftc.teamcode.opmodes.test;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.opmodes.LoggingOpMode;


@TeleOp(name="Controller Test")
public class ControllerTest extends LoggingOpMode {

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void loop() {

        telemetry.addData("Gamepad One Touch Pad", gamepad2.touchpad);
        telemetry.addData("Gamepad One Touch Pad", gamepad2.touchpad_finger_1);
        telemetry.addData("Gamepad One Touch Pad", gamepad2.touchpad_finger_1_x);
        telemetry.addData("Gamepad One Touch Pad", gamepad2.touchpad_finger_1_y);
        telemetry.addData("Gamepad One Touch Pad", gamepad2.touchpad_finger_2);
        telemetry.addData("Gamepad One Touch Pad", gamepad2.touchpad_finger_2_x);
        telemetry.addData("Gamepad One Touch Pad", gamepad2.touchpad_finger_2_y);

        telemetry.update();
    }
}