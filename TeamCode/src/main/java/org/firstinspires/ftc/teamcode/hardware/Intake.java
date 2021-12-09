package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.ColorRangeSensor;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.util.Status;

public class Intake {
    private final DcMotor intake_front;
    private final DcMotor intake_back;
    private final DistanceSensor dist;
    private final Servo bucket;


    public Intake(DcMotor intake_front, DcMotor intake_back, DistanceSensor dist, Servo bucket){
        this.intake_front = intake_front;
        this.intake_back = intake_back;
        this.dist = dist;
        this.bucket = bucket;
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
        return dist.getDistance(DistanceUnit.CM) < Status.FREIGHT_DETECTION;
    }

    public double getFreightDistance(){
        return dist.getDistance(DistanceUnit.CM);
    }
}
