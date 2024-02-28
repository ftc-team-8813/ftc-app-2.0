package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.Servo;

public class Deposit {
    private Servo swivel;
    private Servo c1;
    private Servo c2;
    private Servo pivot;
    private AnalogInput c1I;
    private AnalogInput c2I;
    private AnalogInput pivotI;


    public Deposit(Servo swivel, Servo c1, Servo c2, Servo pivot, AnalogInput c1I, AnalogInput c2I, AnalogInput pivotI){
        this.swivel = swivel;
        this.c1 = c1;
        this.c2 = c2;
        this.pivot = pivot;

        this.c1I = c1I;
        this.c2I = c2I;
        this.pivotI = pivotI;
    }


    public void setC1(double pos){
        c1.setPosition(pos);
    }

    public void setC2(double pos){
        c2.setPosition(pos);
    }
    public void setPivot(double pos){
        pivot.setPosition(pos);
    }
    public double getPivot(){
        return pivot.getPosition();
    }

    public void setSwivel(double pos){
        swivel.setPosition(pos);
    }

    public double getPivotCurrent(){
        return pivotI.getVoltage() / 3.3 * 360;
    }
    public double getC1(){
        return c2I.getVoltage() / 3.3 * 360;
    }
    public double getC2(){
        return c2I.getVoltage() / 3.3 * 360;
    }




}
