package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DigitalChannel;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.events.LiftEvent;
import org.firstinspires.ftc.teamcode.util.Configurations;
import org.firstinspires.ftc.teamcode.util.Logger;
import org.firstinspires.ftc.teamcode.util.Storage;
import org.firstinspires.ftc.teamcode.util.event.EventBus;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Lift -- Controls automatic lift movement. Also can run an automatic homing process. When connected
 * to an EventBus, produces LiftEvents when movement and homing commands are completed.
 */
public class Lift
{
    private CRServo lServo, rServo;
    private CalibratedAnalogInput lPot, rPot;
    private DigitalChannel topButton;
    public double liftTarget; // distance from top (top=0)
    public double grabTarget; // positive = roll IN
    private double kP = 12;
    
    public boolean hold = false;
    // Homing stages:
    // * 0 = not homing -> 1
    // * 1 = move up until an endstop is hit -> 2/3/4
    // * 2 = button hit, slide across to l=0 -> 0
    // * 3 = l endstop hit, slide r up until button hit -> 0
    // * 4 = r endstop hit, slide l back until button hit -> 2
    public int home_stage = 0;
    private double topR = 0.5;
    private boolean bounceHold = false;
    private long lastBounce = 0;

    private HashMap<String, Double> positions;
    private ArrayList<String> homingLog = new ArrayList<>();
    private long lastLog;
    
    private EventBus eventBus;
    private boolean sendEvent = false;
    
    private Logger log = new Logger("Lift");
    
    public Lift(CRServo lServo, CRServo rServo, CalibratedAnalogInput lPot, CalibratedAnalogInput rPot, DigitalChannel button)
    {
        this.lServo = lServo;
        this.rServo = rServo;
        this.lPot = lPot;
        this.rPot = rPot;
        this.topButton = button;

        final String[] pos_keys = new String[]{"bottom", "middle", "top", "grab"};
        positions = Configurations.readData(pos_keys, Storage.getFile("positions/lift.json"));
    }
    
    public void connectEventBus(EventBus evBus)
    {
        this.eventBus = evBus;
    }
    
    public void update(Telemetry telemetry)
    {
        if (home_stage == 0)
        {
            if (!homingLog.isEmpty())
            {
                lServo.setPower(0);
                rServo.setPower(0);
                topR = rPot.get();
                liftTarget = 0;
                grabTarget = 0;
                log.d("Writing homing log");
                try (FileWriter w = new FileWriter(Storage.createFile("homing_log.csv")))
                {
                    for (String s : homingLog)
                    {
                        w.write(s);
                        w.write('\n');
                    }
                    homingLog.clear();
                }
                catch (IOException e)
                {
                    log.e(e);
                }
                log.d("Complete");
            }
            
            if (!topButton.getState() && System.nanoTime() - lastBounce > 500_000_000)
            {
                lastBounce = System.nanoTime();
                // log.d("Bounce");
                bounceHold = true;
                liftTarget += 0.01;
            }
            
            // The lift knows where it is, because it knows where it isn't:
            double lTarget = liftTarget + grabTarget;
            double rTarget = topR - liftTarget + grabTarget;
    
            // by subtracting where it is from where it isn't, it obtains a difference, or deviation
            double lPos = lPot.get();
            double rPos = rPot.get();
    
            double lError = lTarget - lPos;
            double rError = rTarget - rPos;
    
            double lPower = -lError * kP;
            double rPower = -rError * kP;
            
            if (Math.abs(lError) < 0.005 && Math.abs(rError) < 0.005)
            {
                if (eventBus != null && sendEvent)
                {
                    eventBus.pushEvent(new LiftEvent(LiftEvent.LIFT_MOVED));
                    sendEvent = false;
                }
                if (bounceHold && topButton.getState())
                {
                    bounceHold = false;
                    if (eventBus != null) eventBus.pushEvent(new LiftEvent(LiftEvent.LIFT_HOMED));
                }
            }
    
            telemetry.addData("Left", "tgt=%.3f, pos=%.3f err=%.3f power=%.3f",
                    lTarget, lPos, lError, lPower);
            telemetry.addData("Right", "tgt=%.3f, pos=%.3f err=%.3f power=%.3f",
                    rTarget, rPos, rError, rPower);
            
            if (hold || bounceHold)
            {
                enableServos();
                lServo.setPower(lPower);
                rServo.setPower(rPower);
            }
            else
            {
                lServo.setPower(0);
                rServo.setPower(0);
                stopServos();
            }
        }
        else
        {
            if (home_stage == 1) // move up
            {
                lServo.setPower(0.2); // + power -> - distance
                rServo.setPower(-0.2);
                if (!topButton.getState()) // pressed -> LOW == 0 == false
                    home_stage = 2;
                else if (lPot.get() < 0.01)
                    home_stage = 3;
                else if (rPot.get() > 0.98)
                    home_stage = 4;
            }
            else if (home_stage == 2) // button hit, slide across
            {
                lServo.setPower(0.2);
                rServo.setPower(0.2);
                if (lPot.get() < 0.01) home_stage = 0;
            }
            else if (home_stage == 3) // left end hit, slide right servo up
            {
                lServo.setPower(0);
                rServo.setPower(-0.2);
                if (!topButton.getState()) home_stage = 0;
            }
            else if (home_stage == 4) // right end hit, slide left servo down
            {
                lServo.setPower(0.2);
                rServo.setPower(0);
                if (!topButton.getState()) home_stage = 2;
            }
            if (System.nanoTime() > lastLog + 100_000_000)
            {
                lastLog = System.nanoTime();
                homingLog.add(String.format("%d,%.3f,%.3f", home_stage, lPot.get(), rPot.get()));
            }
        }
    }
    
    public void moveLift(double lift)
    {
        liftTarget = lift;
        sendEvent = true;
    }
    
    public void moveGrabber(double grab)
    {
        grabTarget = grab;
        sendEvent = true;
    }
    
    public boolean moveGrabberPreset(int dir)
    {
        double newPos = grabTarget + dir * positions.get("grab");
        if (newPos < 0 || newPos > (1 - topR))
        {
            log.e("FAIL: Cannot set grabber position to %.3f", newPos);
            return false;
        }
        moveGrabber(newPos);
        return true;
    }

    public void moveLiftPreset(String pos){
        moveLift(positions.get(pos));
    }
    
    private void stopServos()
    {
        /*
        LynxServoController lCtrl = (LynxServoController)lServo.getController();
        LynxServoController rCtrl = (LynxServoController)rServo.getController();
        lCtrl.setServoPwmDisable(lServo.getPortNumber());
        rCtrl.setServoPwmDisable(rServo.getPortNumber());
         */
    }
    
    private void enableServos()
    {
        /*
        LynxServoController lCtrl = (LynxServoController)lServo.getController();
        LynxServoController rCtrl = (LynxServoController)rServo.getController();
        lCtrl.setServoPwmEnable(lServo.getPortNumber());
        rCtrl.setServoPwmEnable(rServo.getPortNumber());
         */
    }
    
    public double[] getPositions()
    {
        return new double[] {lPot.get(), rPot.get()};
    }
}
