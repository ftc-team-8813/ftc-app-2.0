package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DigitalChannel;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

public class Horizontal {

    private final DcMotorEx horizontal;
    private final DigitalChannel horizontal_limit;
    private double horizTarget;
    private double horizontal_position;

    public Horizontal(DcMotorEx horizontal, DigitalChannel horizontal_limit) {
        this.horizontal = horizontal;
        this.horizontal_limit = horizontal_limit;
    }

    public void setPower(double pow) {
        horizontal.setPower(pow);
    }

    public void resetEncoders() {
        horizontal.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        horizontal.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void setHorizTarget(double position) {
        horizTarget = position;
        //horiz.setTargetPosition((int) position); //used for run to position
    }

    public double getPower() {
        return horizontal.getPower();
    }

    public double getHorizTarget() {
        return horizTarget;
    }

    public void updatePosition() {
        horizontal_position = horizontal.getCurrentPosition();
    }

    public double getCurrentPosition() {
        return horizontal_position;
    }

    public boolean getLimit() {
        return !horizontal_limit.getState();
    }

    public double getCurrentAmps() {
        return horizontal.getCurrent(CurrentUnit.AMPS);
    }
}
