package org.firstinspires.ftc.teamcode.hardware;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;


import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class Lift {
    private DcMotorEx lift1;
    private DcMotorEx lift2;
    private double liftPos;
    private double liftTarget;

    public Lift(DcMotorEx lift1, DcMotorEx lift2) {
        this.lift1 = lift1;
        this.lift2 = lift2;
    }
    public void update() {
        liftPos = lift1.getCurrentPosition();
    }

    public void setLiftsPower(double pow) {
        lift1.setPower(pow);
        lift2.setPower(-pow);
    }

    public double getLiftPower() {
        return lift1.getPower();
    }

    public void setLiftTarget(double pos) {
        liftTarget = pos;
    }

    public double getLiftTarget() {
        return liftTarget;
    }

    public void resetEncoders() {
        lift1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        lift2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        lift1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        lift2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public double getCurrentPosition() {
        return liftPos;
    }
}