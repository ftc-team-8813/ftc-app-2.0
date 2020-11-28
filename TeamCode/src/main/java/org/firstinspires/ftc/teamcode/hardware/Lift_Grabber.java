package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.CRServo;

public class Lift_Grabber {
    public AnalogInput left_potentiometer;
    public AnalogInput right_potentiometer;
    public CRServo left_lift;
    public CRServo right_lift;

    public Lift_Grabber(AnalogInput left_potentiometer, AnalogInput right_potentiometer, CRServo left_lift, CRServo right_lift){
        this.left_potentiometer = left_potentiometer;
        this.right_potentiometer = right_potentiometer;
        this.left_lift = left_lift;
        this.right_lift = right_lift;
    }

    public double[] getPotenPos(){
        return new double[]{left_potentiometer.getVoltage(), right_potentiometer.getVoltage()};
    }

    // Lift rotations pinned in #dev-ops
    public void lift_grab(double left_stick_y, boolean a){
        if (a) {
            left_lift.setPower(-0.2);
            right_lift.setPower(-0.2);
        } else {
            left_lift.setPower(left_stick_y);
            right_lift.setPower(-left_stick_y);
        }
    }
}
