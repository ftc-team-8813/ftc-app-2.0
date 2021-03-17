package org.firstinspires.ftc.teamcode.hardware.events;

import org.firstinspires.ftc.teamcode.util.event.Event;

public class RingEvent extends Event {
    public final static int TRIGGER_AUTO_RING = 0;
    public final static int BUCKET_FULL = 1;

    public RingEvent(int channel){
        super(channel);
    }
}
