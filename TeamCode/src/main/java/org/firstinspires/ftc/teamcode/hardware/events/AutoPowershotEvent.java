package org.firstinspires.ftc.teamcode.hardware.events;

import org.firstinspires.ftc.teamcode.util.event.Event;

public class AutoPowershotEvent extends Event {
    public final static int TRIGGER_AUTO_POWERSHOT = 0;

    public AutoPowershotEvent(int channel){
        super(channel);
    }
}
