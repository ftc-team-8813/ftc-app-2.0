package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.Servo;

public class Deposit {
    private Servo leftLiftDepo;
    private Servo rightLiftDepo;
    private Servo depoPivot;
    private Servo depoLock;

    private AnalogInput input;

    public Deposit(Servo leftLiftDepo, Servo rightLiftDepo, Servo depoPivot, Servo depoLock, AnalogInput input){
        this.leftLiftDepo = leftLiftDepo;
        this.rightLiftDepo = rightLiftDepo;
        this.depoPivot = depoPivot;
        this.depoLock = depoLock;
        this.input = input;
    }


    public void setLiftDepos(double pos){
        leftLiftDepo.setPosition(pos);
        rightLiftDepo.setPosition(pos);
    }

//    public double getLiftDepo(){
//        return ;
//    }

    public void setDepoPivot(double pos){
        depoPivot.setPosition(pos);
    }

    public double getDepoPivot(){
        return input.getVoltage() / 3.3 * 360;
    }

    public void setDepoLock(double pos){
        depoLock.setPosition(pos);
    }

    public double getDepoLock(){
        return depoLock.getPosition();
    }

}
