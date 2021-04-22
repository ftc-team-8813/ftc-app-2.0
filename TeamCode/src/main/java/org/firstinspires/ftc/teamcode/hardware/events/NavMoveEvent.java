package org.firstinspires.ftc.teamcode.hardware.events;

import org.firstinspires.ftc.teamcode.util.event.Event;

public class NavMoveEvent extends Event
{
    public static final int FORWARD_COMPLETE = 0;
    public static final int TURN_COMPLETE = 1;
    public static final int MOVE_COMPLETE = 2;
    public static final int NAVIGATION_COMPLETE = 3;
    
    public NavMoveEvent(int channel)
    {
        super(channel);
    }
}
