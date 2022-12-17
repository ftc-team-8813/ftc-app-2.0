package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class Intake {

    private DcMotorEx horiz;
    private DcMotorEx arm;
    private DigitalChannel horiz_limit;
    private DigitalChannel arm_limit;
    private DistanceSensor claw_sens;
    private Servo claw;
    private Servo wrist;
    private double armTarget;
    private double horizTarget;

    public Intake(DcMotorEx horiz, DcMotorEx arm, DigitalChannel horiz_limit, DigitalChannel arm_limit, DistanceSensor claw_sens, Servo claw, Servo wrist){
        this.horiz = horiz;
        this.arm = arm;
        this.horiz_limit = horiz_limit;
        this.arm_limit = arm_limit;
        this.claw_sens = claw_sens;
        this.claw = claw;
        this.wrist = wrist;
    }

    public void resetArmEncoder(){
        arm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        arm.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void resetHorizEncoder(){
        horiz.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        horiz.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void setArmTarget(double position){
        armTarget = position;
    }

    public double getArmTarget(){
        return armTarget;
    }

    public void setClaw(double position){
        claw.setPosition(position);
    }

    public void setWrist(double position){
        wrist.setPosition(position);
    }

    public void setArmPow(double pow){
        arm.setPower(pow);
    }

    public void setHorizPow(double pow){
        horiz.setPower(pow);
    }

    public void setHorizTarget (double position){
        horizTarget = position;
    }

    public double getHorizTarget(){
        return horizTarget;
    }

    public boolean getArmLimit(){
        return !arm_limit.getState();
    }
    
    public boolean getHorizLimit(){
        return !horiz_limit.getState();
    }
    
    public double getArmCurrent(){
        return arm.getCurrentPosition();
    }
    
    public double getHorizCurrent(){
        return horiz.getCurrentPosition();
    }

    public double getDistance() {
        return claw_sens.getDistance(DistanceUnit.MM);
    }


}
