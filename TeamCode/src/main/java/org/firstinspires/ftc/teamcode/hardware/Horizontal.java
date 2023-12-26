package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotorEx;


public class Horizontal{
    private DcMotorEx horiz;
    private double horizPos;
    private double horizTarget;

    public void update() {
        horizPos = horiz.getCurrentPosition();
    }

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
        horiz.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
    }
    public void setHorizTarget(double pos) {
        horizTarget = pos;
    }

    public double getHorizTarget() {
        return horizTarget;
    }

    public double getCurrentPosition() {
        return horizPos;
    }


}
