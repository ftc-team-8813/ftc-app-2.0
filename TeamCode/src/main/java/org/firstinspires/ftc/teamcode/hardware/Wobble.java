package org.firstinspires.ftc.teamcode.hardware;

import com.google.gson.JsonObject;
import com.qualcomm.robotcore.hardware.Servo;

public class Wobble
{
    private Servo armServo, clawServo;
    
    private double arm_up, arm_down;
    private double claw_open, claw_close;
    
    public Wobble(Servo armServo, Servo clawServo, JsonObject config)
    {
        this.armServo = armServo;
        this.clawServo = clawServo;
        arm_up = config.get("arm_up").getAsDouble();
        arm_down = config.get("arm_down").getAsDouble();
        claw_open = config.get("claw_open").getAsDouble();
        claw_close = config.get("claw_close").getAsDouble();
    }
    
    public void up()
    {
        armServo.setPosition(arm_up);
    }
    
    public void down()
    {
        armServo.setPosition(arm_down);
    }
    
    public void open()
    {
        clawServo.setPosition(claw_open);
    }
    
    public void close()
    {
        clawServo.setPosition(claw_close);
    }
}
