package org.firstinspires.ftc.teamcode.util.event;

/**
 * @deprecated -- Create a new event class instead of using this class.
 */
@Deprecated
public class TriggerEvent extends Event
{
    
    public TriggerEvent(int id)
    {
        super(id);
    }
    
    public String toString()
    {
        return String.format("TriggerEvent ch=%d", channel);
    }
    
}
