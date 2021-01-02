package org.firstinspires.ftc.teamcode.hardware;

import com.google.gson.JsonObject;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.util.Configuration;

import java.io.File;

public class SimpleLift
{
    private Servo lift_a;
    private Servo lift_b;
    
    private double[] lift_down = new double[2];
    private double[] lift_up   = new double[2];
    
    public SimpleLift(Servo lift_a, Servo lift_b, File lift_config)
    {
        this.lift_a = lift_a;
        this.lift_b = lift_b;
    
        JsonObject root = Configuration.readJson(lift_config);
        lift_down[0] = root.get("down_a").getAsDouble();
        lift_down[1] = root.get("down_b").getAsDouble();
        lift_up[0]   = root.get("up_a").getAsDouble();
        lift_up[1]   = root.get("up_b").getAsDouble();
    }
    
    public void up()
    {
        lift_a.setPosition(lift_up[0]);
        lift_b.setPosition(lift_up[1]);
    }
    
    public void down()
    {
        lift_a.setPosition(lift_down[0]);
        lift_b.setPosition(lift_down[1]);
    }
}
