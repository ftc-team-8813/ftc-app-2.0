package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

public class Turret {
    DcMotor shooter;
    AnalogInput left_potentiometer;
    AnalogInput right_potentiometer;
    Servo finger;
    CRServo left_lift;
    CRServo right_lift;
    int lift_target_pos;

    public Turret(AnalogInput left_potentiometer, AnalogInput right_potentiometer, Servo finger, CRServo left_lift, CRServo right_lift, DcMotor shooter){
        this.left_potentiometer = left_potentiometer;
        this.right_potentiometer = right_potentiometer;
        this.finger = finger;
        this.left_lift = left_lift;
        this.right_lift = right_lift;
        this.shooter = shooter;
    }

    public void setGrabber(int mode){
        // TODO Find grabber power needed for keeping ring controlled
        if (mode == 0){
            left_lift.setPower(0);
            right_lift.setPower(0);
        } else if (mode == 1){
            left_lift.setPower(-0.2);
            right_lift.setPower(-0.2);
        } else if (mode == 2){
            left_lift.setPower(0.2);
            right_lift.setPower(0.2);
        }
    }

    public void setFinger(int pos){
        // TODO Find init, hold, extended positions of finger
        if (pos == 0){
            finger.setPosition(0);
        } else if (pos == 1){
            finger.setPosition(0.8);
        } else if (pos == 2){
            finger.setPosition(1);
        }
    }

    public void setShooter(int mode){
        // TODO Find shooter power
        if (mode == 0){
            shooter.setPower(0);
        } else if (mode == 1){
            shooter.setPower(1);
        }
    }

    public void setLift(int pos){
        this.lift_target_pos = pos;
    }

    public void updateLiftPID(){
        // TODO Determine Proportional Gain
        // TODO Integrate second potentiometer towards PID
        double proportional = 0.6;
        double[] voltage_positions = new double[] {0, 0.669, 0.776};
        double error = getPotenPos()[0] - voltage_positions[lift_target_pos];
        left_lift.setPower(error * proportional);
        right_lift.setPower(-error * proportional);
    }

    public double[] getPotenPos(){
        return new double[]{left_potentiometer.getVoltage(), right_potentiometer.getVoltage()};
    }

    // Lift rotations pinned in #dev-ops
    public void liftGrab(double left_stick_y, boolean a){
        boolean grab_in_use = false;
        boolean lift_in_use = false;
        if (a && !lift_in_use) {
            grab_in_use = true;
            left_lift.setPower(-0.2);
            right_lift.setPower(-0.2);
        } else if (left_stick_y != 0 && !grab_in_use) {
            lift_in_use = true;
            left_lift.setPower(left_stick_y);
            right_lift.setPower(-left_stick_y);
        }
    }

    public void shoot(boolean b) throws InterruptedException {
        double finger_up_pos = 0;
        double finger_down_pos = 0;
        double finger_push_pos = 0;
        double shooter_pow = 0;
        if (b){
            finger.setPosition(finger_up_pos);

            left_lift.setPower(0.2);
            right_lift.setPower(0.2);
            Thread.sleep(1000);

            shooter.setPower(shooter_pow);

            finger.setPosition(finger_push_pos);
        } else {
            finger.setPosition(finger_down_pos);
        }
    }
}
