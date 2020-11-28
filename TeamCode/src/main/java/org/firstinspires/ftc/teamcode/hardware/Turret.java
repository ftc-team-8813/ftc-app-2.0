package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;

public class Turret {
    DcMotor rotator;

    public Turret(DcMotor rotator){
        this.rotator = rotator;
    }

    //Rotates turret based opposite of motor
    public void teleturn(boolean dpad_left, boolean dpad_right){
        if (dpad_left){
            rotator.setPower(0.5);
        } else if (dpad_right){
            rotator.setPower(-0.5);
        }else{
            rotator.setPower(0);
        }
    }
}
