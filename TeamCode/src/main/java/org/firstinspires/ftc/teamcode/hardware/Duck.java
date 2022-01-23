package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.CRServoImplEx;
import com.qualcomm.robotcore.hardware.PwmControl;

public class Duck {
    private final CRServoImplEx frontServo;
    private final CRServoImplEx backServo;

    public Duck(CRServoImplEx frontServo, CRServoImplEx backServo){
        this.frontServo = frontServo;
        this.backServo = backServo;
        frontServo.setPwmRange(new PwmControl.PwmRange(500, 2500));
        backServo.setPwmRange(new PwmControl.PwmRange(500, 2500));
    }

    public void spin(double power){
        frontServo.setPower(power);
        backServo.setPower(power);
    }
}
