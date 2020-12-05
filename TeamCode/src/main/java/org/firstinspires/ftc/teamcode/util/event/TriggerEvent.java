package org.firstinspires.ftc.teamcode.util.event;

public class TriggerEvent implements Event {
    public final int channel;

    public TriggerEvent(int id)
    {
        this.channel = id;
    }

    public String toString()
    {
        return String.format("TriggerEvent ch=%d t=%.3f", channel);
    }

    public int getChannel()
    {
        return this.channel;
    }
}
