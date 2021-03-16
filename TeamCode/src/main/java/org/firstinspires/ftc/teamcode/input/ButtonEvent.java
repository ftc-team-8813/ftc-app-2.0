package org.firstinspires.ftc.teamcode.input;

import org.firstinspires.ftc.teamcode.util.event.Event;

public class ButtonEvent extends Event
{
    public final ControllerMap.ButtonEntry button;
    public final int edge;
    
    public ButtonEvent(ControllerMap.ButtonEntry entry, int edge)
    {
        super(entry.getEventID());
        this.button = entry;
        this.edge = edge;
    }
}
