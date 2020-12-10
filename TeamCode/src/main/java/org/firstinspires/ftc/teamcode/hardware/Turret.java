package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.util.Logger;
import org.firstinspires.ftc.teamcode.util.event.EventBus;
import org.firstinspires.ftc.teamcode.util.event.TriggerEvent;

public class Turret {
    private DcMotor shooter;
    private AnalogInput left_potentiometer;
    private AnalogInput right_potentiometer;
    private Servo finger;
    private CRServo left_lift;
    private CRServo right_lift;
    private int lift_target_pos;
    private boolean enable_lift_event = false;
    private EventBus ev_bus;
    private Logger log = new Logger("Turret");

    public Turret(AnalogInput left_potentiometer, AnalogInput right_potentiometer, Servo finger, CRServo left_lift, CRServo right_lift, DcMotor shooter){
        this.left_potentiometer = left_potentiometer;
        this.right_potentiometer = right_potentiometer;
        this.finger = finger;
        this.left_lift = left_lift;
        this.right_lift = right_lift;
        this.shooter = shooter;
        this.ev_bus = null;
    }
    
    public void connectEventBus(EventBus evBus)
    {
        this.ev_bus = evBus;
        log.d("Event bus connected");
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
        this.enable_lift_event = true;
    }

    public void updateLiftPID(){
        // TODO Determine Proportional Gain
        // TODO Integrate second potentiometer towards PID
        double kP = 0.1;
        double[] voltage_positions = new double[] {0, 0.669, 0.776};
        double voltage = getPotenPos()[0];
        double error = voltage - voltage_positions[lift_target_pos];
        left_lift.setPower(error * kP);
        right_lift.setPower(-error * kP);
        log.v("PID update: voltage=%.3f target=%.3f error=%.3f power=%.3f", voltage, voltage_positions[lift_target_pos], error, error * kP);
        if (Math.abs(error) < 0.05 && enable_lift_event)
        {
            enable_lift_event = false;
            if (ev_bus != null) ev_bus.pushEvent(new TriggerEvent(1));
        }
    }
    
    // Potentiometer linearization tables
    //    x=0   0.1    0.2     0.3    0.4   0.5    0.6   0.7    0.8    0.9   1
    private static final double[] POT_LINEARIZE_LY = {
            0.9915, 0.807,  0.6776, 0.5433, 0.4794, 0.4291, 0.3845, 0.347,  0.3148, 0.2736, 0.2518,
            0.2297, 0.2094, 0.1897, 0.1624, 0.1442, 0.1252, 0.1048, 0.0824, 0.0394, 0.0045
    };
    
    private static final double[] POT_LINEARIZE_R = {
            0, 0.025, 0.051, 0.085, 0.124, 0.175, 0.243, 0.333, 0.468, 0.68, 1
    };
    
    private double linearize(double y, final double[] table)
    {
        int idx = 0;
        for (; idx < 11; idx++)
        {
            if (table[idx] > y) break;
        }
        if (idx == 0) return 0;
        if (idx == 11) return 1;
        double xbase = (double)idx / 10;
        double dx = 0.1 * (y - table[idx]) / (table[idx] - table[idx-1]);
        return xbase + dx;
    }
    
    public double[] getRawPos()
    {
        return new double[]{left_potentiometer.getVoltage(), right_potentiometer.getVoltage()};
    }

    public double[] getPotenPos(){
        double lVoltage = left_potentiometer.getVoltage();
        double rVoltage = right_potentiometer.getVoltage();
        double lValue = lVoltage / 3.25;
        double rValue = (rVoltage - 0.66) / 2.59;
        if (rValue <= 0) rValue = 0;
        // compensate for REV hub ADC load resistance
        lValue = linearize(lValue, POT_LINEARIZE_LY);
        rValue = linearize(rValue, POT_LINEARIZE_R);
        
        return new double[]{lValue, rValue};
    }
    
    public double[] getXYPos()
    {
        double[] pPos = getPotenPos();
        // bottom: 1,    0.8
        // top:    0.85,   1
        double l = (pPos[0] - 0.85) / 0.15; // bottom = 1, top = 0
        double r = (pPos[1] - 0.8) / 0.2;   // bottom = 0, top = 1
        double y = (r - l + 1) / 2;
        double x = (r + l) / 2;
        
        return new double[] {x, y};
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
