package org.firstinspires.ftc.teamcode.hardware;

import com.google.gson.JsonObject;
import com.qualcomm.robotcore.hardware.Servo;

public class Blocker
{
    private Servo servo;
    private boolean isDown = false;
    
    private double up;
    private double down;
    
    public Blocker(Servo servo, JsonObject config)
    {
        this.servo = servo;
        this.up = config.get("up").getAsDouble();
        this.down = config.get("down").getAsDouble();
    }
    
    public void up()
    {
        servo.setPosition(up);
        isDown = false;
    }
    
    public void down()
    {
        servo.setPosition(down);
        isDown = true;
    }
    
    public void toggle()
    {
        if (isDown) up();
        else down();
    }
    
    public boolean isDown()
    {
        return isDown;
    }
}
