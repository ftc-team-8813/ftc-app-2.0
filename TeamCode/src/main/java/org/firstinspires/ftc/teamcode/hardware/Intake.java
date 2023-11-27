package org.firstinspires.ftc.teamcode.hardware;


import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.input.ControllerMap;

public class Intake {

    private DcMotorEx intake;


    public Intake(DcMotorEx intake){
        this.intake = intake;

    }

    public void setPower(double pwr){
        intake.setPower(pwr);
    }

    public double getPower(){
        return intake.getPower();
    }



}