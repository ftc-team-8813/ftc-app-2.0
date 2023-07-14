package org.firstinspires.ftc.teamcode.hardware;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;


import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class Lift {
//    private Motor lift1;
//    private Motor lift2;
//
//
//    public Lift(Motor lift1, Motor lift2){
//        this.lift1 = lift1;
//        this.lift2 = lift2;
//    }
//
//    public void setLiftsPower(double power) {
//        lift1.set(power);
//        lift2.set(-power);
//    }
//
//    public void setLiftsPos(double pos){
//        lift1.
//    }
//
//
//
//    public enum Heights{
//        LOW,
//        MEDIUM,
//        HIGH,
//        GROUND
//    }
//
//
//
//    @Override
//    public void periodic() {
//        super.periodic();
//    }

    private DcMotorEx lift1;
    private DcMotorEx lift2;
    private double liftPos;
    private double lift1Target;

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
        lift1Target = pos;
    }

    public double getLiftTarget() {
        return lift1Target;
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