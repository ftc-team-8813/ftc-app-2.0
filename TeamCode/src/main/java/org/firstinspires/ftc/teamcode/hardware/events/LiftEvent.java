package org.firstinspires.ftc.teamcode.hardware.events;

import org.firstinspires.ftc.teamcode.util.event.Event;

public class LiftEvent extends Event
{
    public static final int LIFT_MOVED = 0;
    public static final int LIFT_HOMED = 1;
    
    public LiftEvent(int channel)
    {
        super(channel);
    }
}
