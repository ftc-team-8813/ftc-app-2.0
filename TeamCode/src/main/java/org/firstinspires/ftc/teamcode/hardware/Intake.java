package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.ColorRangeSensor;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.util.Status;

public class Intake {
    private final DcMotor intake;
    public final DistanceSensor dist;
    private final Servo bucket;

    private boolean freight_detected = false;


    public Intake(DcMotor intake, DistanceSensor dist, Servo bucket){
        this.intake = intake;
        this.dist = dist;
        this.bucket = bucket;
    }

    public void deposit(double target_pos){
        bucket.setPosition(target_pos);
    }

    public void setIntake(double power){
        intake.setPower(power);
    }

    public void stop(){
        intake.setPower(0);
    }

    public boolean freightDetected() {
        return getFreightDistance() < Status.FREIGHT_DETECTION;
    }

    public double getFreightDistance(){
        return dist.getDistance(DistanceUnit.CM);
    }
}
