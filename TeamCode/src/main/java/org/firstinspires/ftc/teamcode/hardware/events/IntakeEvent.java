package org.firstinspires.ftc.teamcode.hardware.events;

import org.firstinspires.ftc.teamcode.util.event.Event;

public class IntakeEvent extends Event {
    public static final int PICKUP_FREIGHT = 0;

    public IntakeEvent(int channel) {
        super(channel);
    }
}
