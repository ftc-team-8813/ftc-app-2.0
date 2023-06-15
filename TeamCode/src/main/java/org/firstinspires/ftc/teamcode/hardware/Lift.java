package org.firstinspires.ftc.teamcode.hardware;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoImplEx;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class Lift {

    private final DcMotorEx lift_left;
    private final DcMotorEx lift_right;
    private final DigitalChannel lift_limit;
    private final Servo holder;
    private final ServoImplEx latch;
    private final DistanceSensor pole_sensor;
    private double lift_position;
    private double lift1Target;
    private boolean old_state = true;
    private double pole_distance;

    public Lift(DcMotorEx lift_left, DcMotorEx lift_right, DigitalChannel lift_limit, Servo holder, ServoImplEx latch, DistanceSensor pole_sensor){
        this.lift_left = lift_left;
        this.lift_right = lift_right;
        this.lift_limit = lift_limit;
        this.holder = holder;
        this.latch = latch;
        this.pole_sensor = pole_sensor;

//        lift_right.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    public void update() {
        lift_position = lift_left.getCurrentPosition() * (5.23 / 3.7);
        pole_distance = pole_sensor.getDistance(DistanceUnit.MM);
    }

    public void setLiftTarget(double pos){
        lift1Target = pos;
    }

    public double getLiftTarget(){
        return lift1Target;
    }

    public double getPoleDistance() {
        return pole_distance;
    }

    public void setPower(double pow){
        lift_left.setPower(pow);
        lift_right.setPower(-pow);
    }

    public void resetEncoders() {
        lift_left.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        lift_right.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        lift_left.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        lift_right.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public double getCurrentPosition() {
        return lift_position;
    }

    public boolean getLimit(){
        return !lift_limit.getState();
    }

    public void setHolderPosition (double pos) {
        holder.setPosition(pos);
    }

    public double getHolderPosition() {
        return holder.getPosition();
    }

    public void setLatchPosition(double pos) {
        latch.setPosition(pos);
    }

    public double getLatchPosition() {
        return latch.getPosition();
    }

    public void setHolderState(boolean on){
        if (on != old_state) { //if the state changed
            if (on) {
                holder.setPosition(holder.getPosition());
            }
            old_state = on;
        }
        if (!on) {
            holder.setPosition(0);
        }
    }

    public double getPower() {
        return lift_right.getPower();
    }

    public double getCurrentAmps() {
        return lift_right.getCurrent(CurrentUnit.AMPS);
    }

}