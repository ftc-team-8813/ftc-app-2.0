package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DigitalChannel;

public class Arm {

    private final DcMotorEx arm;
    private final DigitalChannel arm_limit;
    private String val = "Default";

    public Arm (DcMotorEx arm, DigitalChannel arm_limit) {
        this.arm = arm;
        this.arm_limit = arm_limit;
    }

    public void setPower(double pow) {
        arm.setPower(pow);
    }

    public void resetEncoders() {
        arm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        arm.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public double getCurrentPosition() {
        return arm.getCurrentPosition();
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
