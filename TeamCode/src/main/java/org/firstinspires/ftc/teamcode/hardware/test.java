package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

@TeleOp()
public class TestMotors extends OpMode
{
    Servo one;
    @Override
    public void init() {
        one = hardwareMap.get(Servo.class, "one");
    }

    @Override
    public void init_loop() {

    }

    @Override
    public void start() {

    }
    @Override
    public void loop() {
        if (gamepad1.x){
            one.setPosition(20);
        } else if (gamepad1.a){
            one.setPosition(0);
        }

    }

    @Override
    public void stop() {

    }

}
