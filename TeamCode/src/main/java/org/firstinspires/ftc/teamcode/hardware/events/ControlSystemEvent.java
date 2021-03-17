package org.firstinspires.ftc.teamcode.hardware.events;

import org.firstinspires.ftc.teamcode.util.event.Event;

public class ControlSystemEvent extends Event {
    public final static int TASK_COMPLETE = 0;

    public ControlSystemEvent(int channel){
        super(channel);
    }
}
