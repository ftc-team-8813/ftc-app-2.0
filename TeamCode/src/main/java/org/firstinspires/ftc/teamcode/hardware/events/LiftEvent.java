package org.firstinspires.ftc.teamcode.hardware.events;

import org.firstinspires.ftc.teamcode.util.event.Event;

/**
 * <p>Lift Event -- Triggers when a lift movement operation completes.</p>
 * <p>Channels:</p>
 * <pre>
 * | Number/Range/Const | Producer      | Usage                                                 |
 * |--------------------+---------------+-------------------------------------------------------|
 * | LIFT_MOVED (0)     | hardware.Lift | When the lift reaches the target position when set by |
 * |                    |               | moveLift()/moveGrabber() or their preset versions     |
 * |--------------------+---------------+-------------------------------------------------------|
 * | LIFT_HOMED (1)     | hardware.Lift | When the lift homing sequence completes               |
 * </pre>
 */
public class LiftEvent extends Event
{
    public static final int LIFT_MOVED = 0;
    public static final int LIFT_HOMED = 1;
    
    public LiftEvent(int channel)
    {
        super(channel);
    }
}
