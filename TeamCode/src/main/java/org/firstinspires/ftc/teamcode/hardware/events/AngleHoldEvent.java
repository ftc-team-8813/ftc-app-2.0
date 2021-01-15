package org.firstinspires.ftc.teamcode.hardware.events;

import org.firstinspires.ftc.teamcode.util.event.Event;

public class AngleHoldEvent extends Event
{
    public static final int HOLD_INITIALIZED = 0;
    public static final int TARGET_REACHED   = 1;
    
    public AngleHoldEvent(int channel)
    {
        super(channel);
    }
}
