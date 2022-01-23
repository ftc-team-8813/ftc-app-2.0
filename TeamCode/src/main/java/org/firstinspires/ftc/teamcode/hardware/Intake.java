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
    private final DcMotor intake_front;
    private final DcMotor intake_back;
    private final DistanceSensor dist;
    private final Servo bucket;

    private boolean freight_detected = false;


    public Intake(DcMotor intake_front, DcMotor intake_back, DistanceSensor dist, Servo bucket){
        this.intake_front = intake_front;
        this.intake_back = intake_back;
        this.dist = dist;
        this.bucket = bucket;
        this.intake_back.setDirection(DcMotorSimple.Direction.REVERSE);
    }



    public void deposit(double target_pos){
        bucket.setPosition(target_pos);
    }

    public void setIntakeFront(double power){
        intake_front.setPower(power);
    }

    public void setIntakeBack(double power){
        intake_back.setPower(power);
    }

    public void stop(){
        intake_front.setPower(0);
        intake_back.setPower(0);
    }

    public boolean freightDetected() {
        if (!freight_detected && getFreightDistance() < Status.FREIGHT_DETECTION){
            freight_detected = true;
            return true;
        }
        return false;
    }

    public boolean autoFreightDetected() {
        return (getFreightDistance() < Status.FREIGHT_DETECTION);
    }

    public void detectFreight(){
        freight_detected = false;
    }

    public void stopDetectingFreight(){
        freight_detected = true;
    }

    public double getFreightDistance(){
        return dist.getDistance(DistanceUnit.CM);
    }
}
