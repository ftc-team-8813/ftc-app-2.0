package org.firstinspires.ftc.teamcode.hardware.events;

import org.firstinspires.ftc.teamcode.util.event.Event;

public class LiftEvent extends Event
{
    public static final int LIFT_EXTENDED = 0;

    
    public LiftEvent(int channel)
    {
        super(channel);
    }
}
