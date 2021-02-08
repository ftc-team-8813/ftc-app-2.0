package org.firstinspires.ftc.teamcode.hardware.events;

import org.firstinspires.ftc.teamcode.util.event.Event;

public class IMUEvent extends Event
{
    public final int old_state;
    public final int new_state;
    
    public IMUEvent(int old_state, int new_state)
    {
        super(0);
        this.old_state = old_state;
        this.new_state = new_state;
    }
}
