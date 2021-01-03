package org.firstinspires.ftc.teamcode.hardware.events;

import org.firstinspires.ftc.teamcode.util.event.Event;

public class TurretEvent extends Event
{
    public static final int TURRET_MOVED = 0;
    
    public TurretEvent(int channel)
    {
        super(channel);
    }
}
