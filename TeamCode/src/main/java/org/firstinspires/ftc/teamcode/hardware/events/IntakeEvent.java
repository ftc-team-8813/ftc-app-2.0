package org.firstinspires.ftc.teamcode.hardware.events;

import org.firstinspires.ftc.teamcode.util.event.Event;

public class IntakeEvent extends Event {
    public static final int FREIGHT_DETECTED = 0;
    public static final int INTAKE_FRONT = 1;
    public static final int INTAKE_BACK = 1;

    public IntakeEvent(int channel) {
        super(channel);
    }
}
