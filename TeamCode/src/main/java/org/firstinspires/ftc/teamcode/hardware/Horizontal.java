package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotorEx;


public class Horizontal{
    private DcMotorEx horiz;
    public Horizontal(DcMotorEx horiz){
        this.horiz = horiz;
    }

    public void setHorizPwr(double pwr){
        horiz.setPower(pwr);
    }


    public double getHorizPwr(){
        return horiz.getPower();
    }


    public void resetEncoders() {
        horiz.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);

//        horiz.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void setHorizTarget(double position) {
        horiz.setTargetPosition((int) position); //used for run to position
    }

    public double getHorizTarget() {
        return horiz.getTargetPosition();
    }


    public double getCurrentPosition() {
        return horiz.getCurrentPosition();
    }

}
