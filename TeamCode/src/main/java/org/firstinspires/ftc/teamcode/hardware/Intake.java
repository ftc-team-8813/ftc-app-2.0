package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.hardware.broadcom.BroadcomColorSensor;
import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class Intake {

    DcMotorEx horiz;
    DcMotorEx arm;
    DigitalChannel horiz_limit;
    DigitalChannel arm_limit;
    DistanceSensor claw_sens;
    Servo claw;
    double[] encoderVals = new double[2];
    boolean[] limitValues = new boolean[2];

    public Intake(DcMotorEx horiz, DcMotorEx arm, DigitalChannel horiz_limit, DigitalChannel arm_limit, DistanceSensor claw_sens, Servo claw){
        this.horiz = horiz;
        this.arm = arm;
        this.horiz_limit = horiz_limit;
        this.arm_limit = arm_limit;
        this.claw_sens = claw_sens;
        this.claw = claw;
    }

    public void resetIntakeEncoders(){
        horiz.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        arm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        horiz.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        arm.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void setArmTarget(int position){
        arm.setTargetPosition(position);
    }

    public double getArmTarget(){
        return arm.getTargetPosition();
    }

    public void setClaw(double position){
        claw.setPosition(position);
    }

    public double getClaw(double pos){
        return claw.getPosition();
    }

    public void setArmPow(double pow){
        arm.setPower(pow);
    }

    public void setHorizPow(double pow){
        horiz.setPower(pow);
    }

    public void setHorizTarget (int position){
        horiz.setTargetPosition(position);
    }

    public double getHorizTarget(){
        return horiz.getTargetPosition();
    }

    public void setHoriz_limit(boolean state){
        horiz_limit.setState(state);
    }

    public void setArm_limit(boolean state){
        arm_limit.setState(state);
    }

    public boolean[] limitValues(){
        limitValues[0] = arm_limit.getState();
        limitValues[1] = horiz_limit.getState();

        return limitValues;
    }

    public double[] getEncoderVals(){
        encoderVals[0] = arm.getCurrentPosition();
        encoderVals[1] = horiz.getCurrentPosition();
        return encoderVals;
    }

    public double getDistance() {
        return claw_sens.getDistance(DistanceUnit.MM);
    }
}
