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

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.util.Configurations;
import org.firstinspires.ftc.teamcode.util.Storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class Lift
{
    private CRServo lServo, rServo;
    private CalibratedAnalogInput lPot, rPot;
    public double liftTarget; // distance from top (top=0)
    public double grabTarget; // positive = roll IN
    private double kP = 12;
    
    public boolean hold = false;
    private boolean homing = false;
    // Homing stages:
    // * 0 =
    private int home_stage = 0;
    private double topR = 0.5;

    private HashMap<String, Double> positions;
    
    public Lift(CRServo lServo, CRServo rServo, CalibratedAnalogInput lPot, CalibratedAnalogInput rPot)
    {
        this.lServo = lServo;
        this.rServo = rServo;
        this.lPot = lPot;
        this.rPot = rPot;

        final String[] pos_keys = new String[]{"bottom", "middle", "top"};
        positions = Configurations.readData(pos_keys, Storage.getFile("lift.json"));
    }
    
    public void update(Telemetry telemetry)
    {
        if (!homing)
        {
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
    }

    public void setLiftTarget(String pos){
        liftTarget = positions.get(pos);
    }
    
    private void stopServos()
    {
        LynxServoController lCtrl = (LynxServoController)lServo.getController();
        LynxServoController rCtrl = (LynxServoController)rServo.getController();
        lCtrl.setServoPwmDisable(lServo.getPortNumber());
        rCtrl.setServoPwmDisable(rServo.getPortNumber());
    }
    
    private void enableServos()
    {
        LynxServoController lCtrl = (LynxServoController)lServo.getController();
        LynxServoController rCtrl = (LynxServoController)rServo.getController();
        lCtrl.setServoPwmEnable(lServo.getPortNumber());
        rCtrl.setServoPwmEnable(rServo.getPortNumber());
    }
    
    public double[] getPositions()
    {
        return new double[] {lPot.get(), rPot.get()};
    }
}
