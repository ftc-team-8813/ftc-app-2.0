package org.firstinspires.ftc.teamcode.opmodes.test;

import com.arcrobotics.ftclib.hardware.motors.MotorEx;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.opmodes.LoggingOpMode;

@TeleOp(name="GeneralTest")
public class GeneralTest extends LoggingOpMode {

    private DcMotorEx front_left;
    private DcMotorEx front_right;
    private DcMotorEx back_left;
    private DcMotorEx back_right;

    private DcMotorEx lift1;
    private DcMotorEx lift2;
    private DcMotorEx arm;
    private DcMotorEx horizontal;

    private DigitalChannel lift_limit;
    private DigitalChannel arm_limit;
    private DigitalChannel horiz_limit;

    private DistanceSensor claw_sensor;

    private Servo center_odo;
    private Servo left_odo;
    private Servo right_odo;

    private boolean move_odos = false;
    private boolean pressed_a = false;

    private boolean move_deposit = false;
    private boolean pressed_b = false;

    @Override
    public void init() {
        super.init();
        front_left = hardwareMap.get(DcMotorEx.class, "front left");
        front_right = hardwareMap.get(DcMotorEx.class, "front right");
        back_left = hardwareMap.get(DcMotorEx.class, "back left");
        back_right = hardwareMap.get(DcMotorEx.class, "back right");

        lift1 = hardwareMap.get(DcMotorEx.class, "lift1");
        lift2 = hardwareMap.get(DcMotorEx.class, "lift2");
        arm = hardwareMap.get(DcMotorEx.class, "arm");
        horizontal = hardwareMap.get(DcMotorEx.class, "horizontal");

        lift_limit = hardwareMap.get(DigitalChannel.class, "lift limit");
        arm_limit = hardwareMap.get(DigitalChannel.class, "arm limit");
        horiz_limit = hardwareMap.get(DigitalChannel.class, "horizontal limit");
        claw_sensor = hardwareMap.get(DistanceSensor.class, "claw sensor");


        center_odo = hardwareMap.get(Servo.class, "back odo lift");
        left_odo = hardwareMap.get(Servo.class, "left odo lift");
        right_odo = hardwareMap.get(Servo.class, "right odo lift");

        front_right.setDirection(DcMotorSimple.Direction.REVERSE);
        back_right.setDirection(DcMotorSimple.Direction.REVERSE);

        front_left.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        front_right.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        back_left.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        back_right.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

    }

    @Override
    public void loop() {

        double forward = -gamepad1.left_stick_y;
        double strafe = gamepad1.left_stick_x;
        double turn = gamepad1.right_stick_x;

        double lift1_move = -gamepad2.right_stick_y;

        double arm_move_pos = gamepad1.right_trigger;
        double arm_move_neg = -gamepad1.left_trigger;

        double horizonal_move_pos = gamepad2.right_trigger;
        double horizonal_move_neg = -gamepad2.left_trigger;

        double turn_correct = 0;



        front_left.setPower((forward + strafe + (turn + turn_correct)));
        front_right.setPower((forward - strafe - (turn + turn_correct)));
        back_left.setPower((forward - strafe + (turn + turn_correct)));
        back_right.setPower((forward + strafe - (turn + turn_correct)));


        lift1.setPower(lift1_move);

        lift2.setPower(-lift1_move);

        arm.setPower((arm_move_pos+arm_move_neg));

        horizontal.setPower((horizonal_move_pos+horizonal_move_neg));

        if (!gamepad1.a && pressed_a) {
            pressed_a = false;
        }

        if(gamepad1.a && !pressed_a) {
            move_odos = !move_odos;
            pressed_a = true;
        }

        if (!gamepad1.b && pressed_b) {
            pressed_b = false;
        }

        if(gamepad1.b && !pressed_b) {
            move_deposit = !move_deposit;
            pressed_b = true;
        }

        if(move_odos) {
            center_odo.setPosition(0.34);
            left_odo.setPosition(0.566);
            right_odo.setPosition(0.63);
        }
        else {
            center_odo.setPosition(0);
            left_odo.setPosition(0.137);
            right_odo.setPosition(1);
        }

//        if (move_deposit) {
//
//        }
//        else {
//
//        }




        telemetry.addData("lift_limit",lift_limit.getState());
        telemetry.addData("arm_limit",arm_limit.getState());
        telemetry.addData("horiz_limit",horiz_limit.getState());
        telemetry.addData("lift1",lift1.getCurrentPosition());
        telemetry.addData("lift2",lift2.getCurrentPosition());
        telemetry.addData("arm",arm.getCurrentPosition());
        telemetry.addData("horizontal",horizontal.getCurrentPosition());
        telemetry.addData("claw_sensor",claw_sensor.getDistance(DistanceUnit.MM));
        telemetry.update();

    }
}
