package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.CRServoImplEx;
import com.qualcomm.robotcore.hardware.PwmControl;
import com.qualcomm.robotcore.hardware.Servo;

public class Duck {
    private final CRServoImplEx duck_front;
    private final CRServoImplEx duck_back;
    private Servo sweeper;

    public Duck(CRServoImplEx duck_front, CRServoImplEx duck_back, Servo sweeper){
        this.duck_front = duck_front;
        this.duck_back = duck_back;
        this.sweeper = sweeper;
        duck_front.setPwmRange(new PwmControl.PwmRange(500, 2500));
        duck_back.setPwmRange(new PwmControl.PwmRange(500, 2500));
    }

    public void spin(double power){
        duck_front.setPower(power);
        duck_back.setPower(-power);
    }

    public void sweep(double position){
        sweeper.setPosition(position);
    }

    public double getPower(){
        return duck_front.getPower();
    }
}
