package org.firstinspires.ftc.teamcode.hardware;


import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.input.ControllerMap;

public class Intake {

    private DcMotorEx intake;
    private Servo lock;


    public Intake(DcMotorEx intake, Servo lock){
        this.intake = intake;
        this.lock = lock;

    }

    public void setPower(double pwr){
        intake.setPower(pwr);
    }

    public double getPower(){
        return intake.getPower();
    }

    public void setLock(double pos){
        lock.setPosition(pos);
    }

    public double getLock(){
        return lock.getPosition();
    }
}