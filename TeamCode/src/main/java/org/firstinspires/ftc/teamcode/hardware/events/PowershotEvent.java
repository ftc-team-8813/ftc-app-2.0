package org.firstinspires.ftc.teamcode.hardware.events;

import org.firstinspires.ftc.teamcode.util.event.Event;

public class PowershotEvent extends Event {
    public final static int TRIGGER_POWERSHOT = 0;
    public final static int AIM_TURRET = 1;
    public final static int SHOOT_RING = 2;

    public PowershotEvent(int channel){
        super(channel);
    }
}
