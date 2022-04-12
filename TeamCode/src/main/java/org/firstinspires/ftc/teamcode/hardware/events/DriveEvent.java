package org.firstinspires.ftc.teamcode.hardware.events;

import org.firstinspires.ftc.teamcode.util.event.Event;

public class DriveEvent extends Event {
    public static final int FIND_LINE =  0;

    public DriveEvent(int channel) {
        super(channel);
    }
}
