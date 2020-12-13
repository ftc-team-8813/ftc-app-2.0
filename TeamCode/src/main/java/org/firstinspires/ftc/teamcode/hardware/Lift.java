package org.firstinspires.ftc.teamcode.hardware;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.qualcomm.hardware.lynx.LynxServoController;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DigitalChannel;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.util.Logger;
import org.firstinspires.ftc.teamcode.util.Storage;
import org.firstinspires.ftc.teamcode.util.Configurations;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class Lift
{
    private CRServo lServo, rServo;
    private CalibratedAnalogInput lPot, rPot;
    private DigitalChannel topButton;
    public double liftTarget; // distance from top (top=0)
    public double grabTarget; // positive = roll IN
    private double kP = 5;
    
    public boolean hold = false;
    // Homing stages:
    // * 0 = not homing -> 1
    // * 1 = move up until an endstop is hit -> 2/3/4
    // * 2 = button hit, slide across to l=0 -> 0
    // * 3 = l endstop hit, slide r up until button hit -> 0
    // * 4 = r endstop hit, slide l back until button hit -> 2
    public int home_stage = 0;
    private double topR = 0.5;

    private HashMap<String, Double> positions;
    private ArrayList<String> homingLog = new ArrayList<>();
    private long lastLog;
    
    private Logger log = new Logger("Lift");
    
    public Lift(CRServo lServo, CRServo rServo, CalibratedAnalogInput lPot, CalibratedAnalogInput rPot, DigitalChannel button)
    {
        this.lServo = lServo;
        this.rServo = rServo;
        this.lPot = lPot;
        this.rPot = rPot;
        this.topButton = button;

        final String[] pos_keys = new String[]{"bottom", "middle", "top"};
        positions = Configurations.readData(pos_keys, Storage.getFile("lift.json"));
    }
    
    public void update(Telemetry telemetry)
    {
        if (home_stage == 0)
        {
            if (!homingLog.isEmpty())
            {
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
    
            telemetry.addData("Left", "tgt=%.3f, pos=%.3f err=%.3f power=%.3f",
                    lTarget, lPos, lError, lPower);
            telemetry.addData("Right", "tgt=%.3f, pos=%.3f err=%.3f power=%.3f",
                    rTarget, rPos, rError, rPower);
            
            if (hold)
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
                lServo.setPower(0.1); // + power -> - distance
                rServo.setPower(-0.1);
                if (!topButton.getState()) // pressed -> LOW == 0 == false
                    home_stage = 2;
                else if (lPot.get() < 0.01)
                    home_stage = 3;
                else if (rPot.get() > 0.98)
                    home_stage = 4;
            }
            else if (home_stage == 2) // button hit, slide across
            {
                lServo.setPower(0.1);
                rServo.setPower(0.1);
                if (lPot.get() < 0.01) home_stage = 0;
            }
            else if (home_stage == 3) // left end hit, slide right servo up
            {
                lServo.setPower(0);
                rServo.setPower(-0.1);
                if (!topButton.getState()) home_stage = 0;
            }
            else if (home_stage == 4) // right end hit, slide left servo down
            {
                lServo.setPower(0.1);
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

    public void setLiftTarget(String pos){
        liftTarget = positions.get(pos);
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
