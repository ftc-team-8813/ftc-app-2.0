package org.firstinspires.ftc.teamcode.hardware;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.Servo;

public class Lift {
    private DigitalChannel lift_limit;
    private DcMotorEx lift1;
    private DcMotorEx lift2;
    private Servo dumper;
    private double lift1Target;

    public Lift(DigitalChannel lift_limit, DcMotorEx lift1, DcMotorEx lift2, Servo dumper) {
        this.lift_limit = lift_limit;
        this.lift1 = lift1;
        this.lift2 = lift2;
        this.dumper = dumper;
    }

    public void setLiftPower(double power) {
        lift1.setPower(power);
        lift2.setPower(-power); //one of them have to be inversed
    }

    public void setDumper(double pos) {
        dumper.setPosition(pos);
    }


    public boolean getLift_limit() {
        return lift_limit.getState();
    }

    public void resetLiftEncoder() {
        //ask john which motor encoder is connected to
        lift1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        lift1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void setLiftTarget(double pos) {
        //ask john if the lift motors target position are inversed as well
        lift1Target = pos;
    }


    public double getLiftTarget() {
        return lift1Target;
    }

    public double getEncoderVal() {
        return (lift1.getCurrentPosition());
    }

}