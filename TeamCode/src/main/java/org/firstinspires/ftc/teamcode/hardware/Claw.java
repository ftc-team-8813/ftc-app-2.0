package org.firstinspires.ftc.teamcode.hardware;
import com.qualcomm.robotcore.hardware.Servo;


public class Claw {

    private double a = -1;
    private Servo claw;
    boolean isOpen = false;
    public static final double closePosition = 0.204;
    public static final double openPosition = 0.416;


    public Claw(Servo claw){
        this.claw = claw;
    }

    public void SetValues(){
        if(!isOpen){
            claw.setPosition(closePosition);
            isOpen = true;
        }
        if(isOpen){
            claw.setPosition(openPosition);
            isOpen = false;
        }
    }

    public boolean getStatus(){
        return isOpen;
    }

    public void setPosition(double position){
        claw.setPosition(position);
    }

    public double getPosition(){
        return claw.getPosition();
    }
}
