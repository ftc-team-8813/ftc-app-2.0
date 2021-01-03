package org.firstinspires.ftc.teamcode.hardware;

import com.google.gson.JsonObject;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.events.LiftEvent;
import org.firstinspires.ftc.teamcode.util.Configuration;
import org.firstinspires.ftc.teamcode.util.Logger;
import org.firstinspires.ftc.teamcode.util.Time;
import org.firstinspires.ftc.teamcode.util.event.EventBus;

import java.io.File;

public class SimpleLift
{
    private Servo lift_a;
    private Servo lift_b;
    
    private double[] lift_down = new double[2];
    private double[] lift_up   = new double[2];
    private double lift_time;
    
    private boolean up;
    private double start;
    private double startUp;
    private double upness;
    private boolean sendEvent = false;
    
    private EventBus evBus;
    
    private Logger log = new Logger("Lift");
    
    public SimpleLift(Servo lift_a, Servo lift_b, JsonObject lift_config)
    {
        this.lift_a = lift_a;
        this.lift_b = lift_b;
    
        JsonObject root = lift_config;
        lift_down[0] = root.get("down_a").getAsDouble();
        lift_down[1] = root.get("down_b").getAsDouble();
        lift_up[0]   = root.get("up_a").getAsDouble();
        lift_up[1]   = root.get("up_b").getAsDouble();
        JsonObject meta = root.getAsJsonObject("metadata");
        lift_time    = meta.get("lift_time").getAsDouble();
    }
    
    public void connectEventBus(EventBus evBus)
    {
        this.evBus = evBus;
    }
    
    public void up()
    {
        if (!up)
        {
            log.d("up");
            start = Time.now();
            startUp = upness;
            sendEvent = true;
        }
        up = true;
    }
    
    public void down()
    {
        if (up)
        {
            log.d("down");
            start = Time.now();
            startUp = upness;
            sendEvent = true;
        }
        up = false;
    }
    
    public void update(Telemetry telemetry)
    {
        double t = Time.since(start) / lift_time;
        if (up)
        {
            upness = Range.clip(t + startUp, 0, 1);
            if (upness == 1 && sendEvent && evBus != null)
            {
                sendEvent = false;
                evBus.pushEvent(new LiftEvent(LiftEvent.LIFT_MOVED, true));
            }
        }
        else
        {
            // upness = Range.clip(startUp - t, 0, 1);
            upness = 0; // yeet down FAST
            if (upness == 0 && sendEvent && evBus != null)
            {
                sendEvent = false;
                evBus.pushEvent(new LiftEvent(LiftEvent.LIFT_MOVED, false));
            }
        }
        lift_a.setPosition(lift_down[0] + upness * (lift_up[0] - lift_down[0]));
        lift_b.setPosition(lift_down[1] + upness * (lift_up[1] - lift_down[1]));
        telemetry.addData("a", lift_a.getPosition());
        telemetry.addData("b", lift_b.getPosition());
    }
}
