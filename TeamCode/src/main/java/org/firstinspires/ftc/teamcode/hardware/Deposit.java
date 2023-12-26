package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.Servo;

public class Deposit {
    private Servo leftLiftDepo;
    private Servo rightLiftDepo;
    private Servo depoPivot;
    private Servo depoLock;

    public Deposit(Servo leftLiftDepo, Servo rightLiftDepo, Servo depoPivot, Servo depoLock){
        this.leftLiftDepo = leftLiftDepo;
        this.rightLiftDepo = rightLiftDepo;
        this.depoPivot = depoPivot;
        this.depoLock = depoLock;
    }


    public void setLiftDepos(double pos){
        leftLiftDepo.setPosition(pos);
        rightLiftDepo.setPosition(pos);
    }

    public double getLiftDepo(){
        return leftLiftDepo.getPosition();
    }

    public void setDepoPivot(double pos){
        depoPivot.setPosition(pos);
    }

    public double getDepoPivot(){
        return depoPivot.getPosition();
    }

    public void setDepoLock(double pos){
        depoLock.setPosition(pos);
    }

    public double getDepoLock(){
        return depoLock.getPosition();
    }
}
