package org.firstinspires.ftc.teamcode.util.event;

public class TriggerEvent extends Event {

    public TriggerEvent(int id)
    {
        super(id);
    }

    public String toString()
    {
        return String.format("TriggerEvent ch=%d", channel);
    }

}
