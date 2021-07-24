package org.firstinspires.ftc.teamcode.hardware.events;

import org.firstinspires.ftc.teamcode.util.event.Event;

public class CameraEvent extends Event
{
    public final static int FRAME_CAUGHT = 0;
    
    public CameraEvent(int channel)
    {
        super(channel);
    }
}
