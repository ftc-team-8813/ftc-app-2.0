package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.arcrobotics.ftclib.hardware.motors.MotorEx;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.hardware.Lift;
import org.firstinspires.ftc.teamcode.hardware.Robot;

public class LiftTest extends OpMode {
    private Robot robot;
    private Lift lift;
    // Motors

    DcMotorEx lift1 = hardwareMap.get(DcMotorEx.class, "lift1");  //lift1 and lift2 have to be inversed
    DcMotorEx lift2 = hardwareMap.get(DcMotorEx.class, "lift2");

    // Servos
    Servo dumper = hardwareMap.get(Servo.class, "dumper");

    //Sensors
    DigitalChannel lift_limit = hardwareMap.get(DigitalChannel.class, "lift limit");

    @Override
    public void init() {
        robot = new Robot(hardwareMap);

        lift = new Lift(lift_limit, lift1, lift2, dumper);

        robot.lift = lift;
    }

    @Override
    public void loop() {

        robot.lift.setLiftPower(gamepad1.right_stick_y);


    }
}
