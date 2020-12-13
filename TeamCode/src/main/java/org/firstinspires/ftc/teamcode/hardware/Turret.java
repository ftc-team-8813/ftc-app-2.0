package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.util.Configurations;
import org.firstinspires.ftc.teamcode.util.Logger;
import org.firstinspires.ftc.teamcode.util.Storage;
import org.firstinspires.ftc.teamcode.util.event.EventBus;
import org.firstinspires.ftc.teamcode.util.event.TriggerEvent;

import java.util.HashMap;

public class Turret {
    private DcMotor rotator;
    private DcMotor shooter;
    private AnalogInput left_potentiometer;
    private AnalogInput right_potentiometer;
    private AnalogInput rotate_potentiometer;
    private CalibratedAnalogInput turret_pot;
    private Servo finger;
    private CRServo left_lift;
    private CRServo right_lift;

    HashMap<String, Double> positions;

    private int lift_target_pos;
    private boolean enable_lift_event = false;
    
    private EventBus ev_bus;
    private Logger log = new Logger("Turret");

    public Turret(AnalogInput left_potentiometer, AnalogInput right_potentiometer,
                  Servo finger, CRServo left_lift, CRServo right_lift, DcMotor shooter, DcMotor rotator,
                  AnalogInput rotate_potentiometer){
        this.left_potentiometer = left_potentiometer;
        this.right_potentiometer = right_potentiometer;
        this.rotate_potentiometer = rotate_potentiometer;
        this.turret_pot = new CalibratedAnalogInput(rotate_potentiometer, Storage.getFile("turret_calib.json"));
        this.finger = finger;
        this.left_lift = left_lift;
        this.right_lift = right_lift;
        this.rotator = rotator;
        this.shooter = shooter;
        this.ev_bus = null;

        String[] pos_keys = new String[]{"in", "catch", "out"};
        positions = Configurations.readData(pos_keys, Storage.getFile("finger.json"));
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

    public void setFinger(String pos){
        // TODO Find init, hold, extended positions of finger
        finger.setPosition(positions.get(pos));
    }

    public void setShooter(int mode){
        // TODO Find shooter power
        if (mode == 0){
            shooter.setPower(0);
        } else if (mode == 1){
            shooter.setPower(1);
        }
    }

    public void rotateTurret(double rotation){
        rotator.setPower(rotation);
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

    public double[] getRawPos()
    {
        return new double[]{left_potentiometer.getVoltage(), right_potentiometer.getVoltage(), rotate_potentiometer.getVoltage()};
    }

    @Deprecated
    public double[] getPotenPos(){
        double lVoltage = left_potentiometer.getVoltage();
        double rVoltage = right_potentiometer.getVoltage();
        double lValue = lVoltage / 3.3;
        double rValue = rVoltage / 3.3;
        if (rValue <= 0) rValue = 0;
        
        return new double[]{lValue, rValue};
    }

    // Direct Tele-Op Movements
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
}
