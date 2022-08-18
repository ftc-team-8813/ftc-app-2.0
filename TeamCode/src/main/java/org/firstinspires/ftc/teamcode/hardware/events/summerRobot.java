package org.firstinspires.ftc.teamcode.hardware.events;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.util.event.EventBus;

public class summerRobot {
    public SRobotIntake intake;
    public SRDrivetrain chassis;
    public SRShooter shooter;
    public SRGate gate;

    public EventBus eventBus = new EventBus();

    public summerRobot(HardwareMap hardwareMap){
        DcMotorEx frontLeft = hardwareMap.get(DcMotorEx.class, "front left");
        DcMotorEx frontRight = hardwareMap.get(DcMotorEx.class, "front right");
        DcMotorEx backLeft = hardwareMap.get(DcMotorEx.class, "back left");
        DcMotorEx backRight = hardwareMap.get(DcMotorEx.class, "back right");  //Has right odometry encoder
        DcMotorEx intake = hardwareMap.get(DcMotorEx.class, "intake"); // Has strafe odometry encoder
        DcMotorEx shooter = hardwareMap.get(DcMotorEx.class, "shooter"); //Has left odometry encoder

        Servo gate = hardwareMap.get(Servo.class, "gate");

        this.intake = new SRobotIntake(intake);
        this.shooter = new SRShooter(shooter);
        this.chassis = new SRDrivetrain(frontLeft, frontRight, backLeft, backRight);
        this.gate = new SRGate(gate);
    }

}