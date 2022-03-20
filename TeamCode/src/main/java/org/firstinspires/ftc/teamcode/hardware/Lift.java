package org.firstinspires.ftc.teamcode.hardware;
import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;
import com.qualcomm.robotcore.hardware.DcMotor;

public class ReethamLift {

    DcMotor motor1;
    DcMotor motor2;
    DcMotor pivoter;
    double finalDoubleValue;
    double kp;
    double target;

    public ReethamLift(DcMotor motor1, DcMotor motor2, DcMotor pivoter, double kp){
        motor1 = this.motor1;
        motor2 = this.motor2;
        kp = this.kp;
        pivoter = this.pivoter;
        finalDoubleValue = 0;
        target = 0;
    }

    public void set_DcMotor_power(DcMotor motor1, DcMotor motor2, double power){
        motor1.setPower(power);
        motor2.setPower(power);
    }

    public void lift_motors_extension(int ticks){
        motor1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor1.setTargetPosition(ticks); //Can depend because I don't know the exact target tick position
        motor2.setTargetPosition(ticks);
        set_DcMotor_power(motor1, motor2, 1);
        motor1.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motor2.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        while(motor1.isBusy() && motor2.isBusy()){
            telemetry.addData("2 Motors Status", "Motors are busy");
            telemetry.update();
        }
        set_DcMotor_power(motor1, motor2, 0);
    }

    public void set_pivot(DcMotor pivotMotor, double increment, boolean doneWithPivot){
        double error = (target + increment) - pivotMotor.getCurrentPosition();
        finalDoubleValue = error * kp;
        pivotMotor.setPower(error * kp);
        pivotMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        if(pivotMotor.getCurrentPosition() == finalDoubleValue){
            doneWithPivot = true;
        }
    }

    public void pivot_motor_extension(int ticks){
        pivoter.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        pivoter.setTargetPosition(ticks);
        pivoter.setPower(1);
        pivoter.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        while(pivoter.isBusy()){
            telemetry.addData("Pivot Motor Status", "Motor is busy");
            telemetry.update();
        }
        pivoter.setPower(0);
    }
}