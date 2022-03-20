package org.firstinspires.ftc.teamcode.hardware;
import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;
import com.qualcomm.robotcore.hardware.DcMotor;
<<<<<<< Updated upstream

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
=======
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.util.Logger;
import org.firstinspires.ftc.teamcode.util.Status;

public class Lift {
    private final DcMotor lift;
    private final DcMotor lift2;
    private final DigitalChannel limit_switch;
    private final Servo outrigger;
    private Logger log = new Logger("Lift");

    private boolean lift_reached = true;

    private double target_pos;
    private double integral;
    private double past_error;
    private double p_term;
    private double i_term;
    private double d_term;
    private final ElapsedTime timer = new ElapsedTime();
    public boolean auto_override = false;
    private boolean was_reset = false;

    public boolean duck_cycle_flag = false;

    public Lift(DcMotor lift, DcMotor lift2, DigitalChannel limit_switch, Servo outrigger){
        lift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        lift.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        lift2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        lift.setDirection(DcMotorSimple.Direction.REVERSE);
        lift2.setDirection(DcMotorSimple.Direction.REVERSE);

        this.lift = lift; // Encoder and motor on same port
        this.lift2 = lift2; // No encoder
        this.limit_switch = limit_switch;
        this.outrigger = outrigger;
>>>>>>> Stashed changes
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

<<<<<<< Updated upstream
    public void set_pivot(DcMotor pivotMotor, double increment, boolean doneWithPivot){
        double error = (target + increment) - pivotMotor.getCurrentPosition();
        finalDoubleValue = error * kp;
        pivotMotor.setPower(error * kp);
        pivotMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        if(pivotMotor.getCurrentPosition() == finalDoubleValue){
            doneWithPivot = true;
=======
    public boolean ifReached(double check_pos){
        double min = check_pos - 1500;
        double max = check_pos + 1500;
        if (!lift_reached && min <= getLiftCurrentPos() && getLiftCurrentPos() <= max){
            lift_reached = true;
            return true;
>>>>>>> Stashed changes
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
<<<<<<< Updated upstream
        pivoter.setPower(0);
=======

        if (!was_reset && limitPressed()){
            resetEncoder();
            was_reset = true;
        } else if (!limitPressed()){
            was_reset = false;
        }
    }

    public double getLiftCurrentPos(){
        return lift.getCurrentPosition();
    }
    public boolean limitPressed(){
        return !limit_switch.getState();
    }
    public double getLiftTargetPos(){
        return target_pos;
    }
    public void resetLiftTarget(){
        target_pos = 0;
    }
    public double getPower(){
        return lift.getPower();
    }
    public double[] getPIDTerms(){
        return new double[]{p_term, i_term, d_term};
>>>>>>> Stashed changes
    }
}