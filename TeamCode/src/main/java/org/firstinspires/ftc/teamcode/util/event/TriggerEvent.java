package org.firstinspires.ftc.teamcode.util.event;

public class TriggerEvent implements Event {
    public final int channel;

    public TriggerEvent(int id)
    {
        this.channel = id;
    }

    public String toString()
    {
        return String.format("TriggerEvent ch=%d", channel);
    }

    public int getChannel()
    {
        return this.channel;
    }
}
