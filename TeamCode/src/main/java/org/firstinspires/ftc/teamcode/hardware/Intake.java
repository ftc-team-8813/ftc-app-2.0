package org.firstinspires.ftc.teamcode.hardware;


import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.input.ControllerMap;

public class Intake {

    private DcMotorEx intake;
    private Servo roller;
    private Servo cage;


    public Intake(DcMotorEx intake, Servo roller, Servo cage){
        this.intake = intake;
        this.roller = roller;
        this.cage = cage;
    }
    public void setPower(double pwr){
        intake.setPower(pwr);
    }

    public double getPower(){
        return intake.getPower();
    }

    public void setRol(double pos){
        roller.setPosition(pos);
    }
    public void setCage(double pos){
        cage.setPosition(pos);
    }
//
//    public double getLock(){
//        return lock.getPosition();
//    }
}