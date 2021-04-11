package org.firstinspires.ftc.teamcode.hardware.events;

import org.firstinspires.ftc.teamcode.util.event.Event;

public class AutoShooterEvent extends Event {
    public final static int TRIGGER_AUTO_SHOOTER = 0;

    public AutoShooterEvent(int channel){
        super(channel);
    }
}
