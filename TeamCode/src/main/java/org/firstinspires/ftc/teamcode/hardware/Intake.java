package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.util.Status;

public class Intake {
    private final DcMotor intake;
    public final DistanceSensor freight_checker;
    private final Servo claw;


    public Intake(DcMotor intake, DistanceSensor freight_checker, Servo claw){
        this.intake = intake;
        this.freight_checker = freight_checker;
        this.claw = claw;
    }

    public void deposit(double target_pos){
        claw.setPosition(target_pos);
    }

    public void setPower(double power){
        intake.setPower(power);
    }

    public void stop(){
        intake.setPower(0);
    }

    public boolean freightDetected() {
        return freight_checker.getDistance(DistanceUnit.CM) < Status.FREIGHT_DETECTION;
    }

    public double getPower(){
        return intake.getPower();
    }
}
