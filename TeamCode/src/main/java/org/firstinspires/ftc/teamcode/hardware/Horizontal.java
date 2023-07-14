package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

public class Horizontal{
    private Servo horiz;

    public Horizontal(Servo horiz){
        this.horiz = horiz;
    }

    public void setHorizPos(double pos){
        horiz.setPosition(pos);
    }

    public double getHorizPos(){
        return horiz.getPosition();
    }
}
