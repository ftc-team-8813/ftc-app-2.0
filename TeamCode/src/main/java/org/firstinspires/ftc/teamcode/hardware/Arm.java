package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DigitalChannel;

public class Arm {

    private final DcMotorEx arm;
    private final DigitalChannel arm_limit;
    private String val = "Default";
    private double arm_position;
    private double armTarget;

    public Arm (DcMotorEx arm, DigitalChannel arm_limit) {
        this.arm = arm;
        this.arm_limit = arm_limit;

        arm.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public void updatePosition() {
        arm_position = -arm.getCurrentPosition() * 288.0 / 8192.0;
    }

    public void setPower(double pow) {
        arm.setPower(pow);
    }

    public void resetEncoders() {
        arm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        arm.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public double getCurrentPosition() {
        return arm_position;
    }

//    public double getarm_position() {
//        return arm_position;
//    }

    public void setArmTarget(double position){
        armTarget = position;
    }

    public double getArmTarget(){
        return armTarget;
    }

    public boolean getLimit() {
        return !arm_limit.getState();
    }

    public void setVal(String v) {
        val = v;
    }

    public String getVal() {
        return val;
    }
}
