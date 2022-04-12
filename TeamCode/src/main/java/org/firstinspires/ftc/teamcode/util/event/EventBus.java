package org.firstinspires.ftc.teamcode.util.event;

import org.firstinspires.ftc.teamcode.util.Logger;
import org.firstinspires.ftc.teamcode.util.Time;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 * Event Bus -- Handles the passing of events in a publish-subscribe system.
 * * Events
 *   An event is generally an instance of any Event subclass. This allows different types of Event
 *   objects to have different parameters (i.e. the current state when the event was created). All
 *   Event objects also have a 'channel' that is used to filter which subscriber receives that event.
 * * Subscribers
 *   A Subscriber object contains information about what event type and channel to receive events
 *   from, as well as a callback function to run when that event is received. The callback class
 *   must implement SubCallback, whose run function takes three parameters:
 *     * The Event that triggered the callback
 *     * The EventBus that the Subscriber is subscribed to
 *     * The Subscriber that received the event
 *   The latter two functions are helpful when a callback must unsubscribe itself after it has
 *   completed in order to conserve resources (i.e. when an event is guaranteed to only occur once).
 *   While some event publishers may do this automatically with the function EventBus.unsubscribeAll(),
 *   this is not currently supported by all of them.
 * * Publishers
 *   To publish an Event, simply use the function EventBus.pushEvent()
 * </pre>
 */
public class EventBus
{
    @FunctionalInterface
    public interface SubCallback<T extends Event>
    {
        void run(T event, EventBus bus, Subscriber<T> sub);
    }
    
    public static class Subscriber<T extends Event>
    {
        public final Class<T> evClass;
        public final SubCallback<T> callback;
        public final String name;
        public final int channel;
        private boolean subbed;
        
        public Subscriber(Class<T> evClass, SubCallback<T> callback, String name, int channel)
        {
            this.evClass = evClass;
            this.callback = callback;
            this.name = name;
            this.channel = channel;
            subbed = false;
        }
        
        public boolean subbed()
        {
            return subbed;
        }
    }
    
    public static class TickEvent extends Event
    {
        protected TickEvent()
        {
            super(0);
            this.suppressDebug = true;
        }
    }
    
    private List<Event> events;
    private List<Subscriber<?>> subscribers;
    private Logger log = new Logger("Event Bus");
    
    public EventBus()
    {
        events = new ArrayList<>();
        subscribers = new ArrayList<>();
    }
    
    public <T extends Event> Subscriber<T> subscribe(Class<T> evClass, SubCallback<T> callback, String name, int channel)
    {
        Subscriber<T> sub = new Subscriber<>(evClass, callback, name, channel);
        subscribers.add(sub);
        sub.subbed = true;
        log.d("Subscriber Add: '%s' receives %s on channel %d", name, evClass.getSimpleName(), channel);
        return sub;
    }
    
    public <T extends Event> void subscribe(Subscriber<T> sub)
    {
        if (sub.subbed) throw new IllegalArgumentException("Subscriber already subscribed");
        subscribers.add(sub);
        sub.subbed = true;
        log.d("Subscriber add (existing): '%s' receives %s on channel %d", sub.name, sub.evClass.getSimpleName(), sub.channel);
    }
    
    public void unsubscribe(Subscriber<?> sub)
    {
        if (!sub.subbed) throw new IllegalArgumentException("Subscriber already unsubscribed");
        subscribers.remove(sub);
        sub.subbed = false;
    }
    
    public <T extends Event> void unsubscribeAll(Class<T> evClass, int channel)
    {
        log.d("Unsubscribing all %s listeners on channel %d", evClass.getSimpleName(), channel);
        subscribers.removeIf((sub) -> {
            if (sub.evClass == evClass && sub.channel == channel)
            {
                log.v(" -> Removed '%s'", sub.name);
                return true;
            }
            return false;
        });
    }
    
    public void pushEvent(Event ev)
    {
        events.add(ev);
        if (!ev.suppressDebug)
        {
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            String callingClassName = stackTrace[3].getClassName();
//            log.d("Push %s on channel %d (from %s)", ev.getClass().getSimpleName(), ev.channel, callingClassName);
        }
    }
    
    @SuppressWarnings("unchecked")
    public void update()
    {
        pushEvent(new TickEvent()); // TickEvent subs will run on every loop cycle
        
        
        // copy the subscriber list so they can remove themselves if they wish
        List<Subscriber<?>> oldSubs = new ArrayList<>(subscribers);
        // same for the event list
        List<Event> oldEvents = new ArrayList<>(events);
        events.clear();
        
        // loop subs first, since there will most likely be more subs than events at any given time
        for (Subscriber sub : oldSubs)
        {
            for (Event ev : oldEvents)
            {
                if (ev.getClass() == sub.evClass && ev.channel == sub.channel)
                {
                    if (!ev.suppressDebug)
                    {
//                        log.v("Event: %s on channel %d: %s", ev.getClass().getSimpleName(), ev.channel, ev.toString());
//                        log.v(" -> Send to subscriber '%s'", sub.name);
                    }
                    double execStart = Time.now();
                    sub.callback.run(ev, this, sub);
                    double elapsed = Time.now() - execStart;
                    if (elapsed > 0.25)
                    {
//                        log.w("Subscriber %s took %.3fs to handle %s (ch=%d)",
//                                sub.name, elapsed, ev.getClass().getSimpleName(), ev.channel);
                    }
                }
            }
        }
    }
}
