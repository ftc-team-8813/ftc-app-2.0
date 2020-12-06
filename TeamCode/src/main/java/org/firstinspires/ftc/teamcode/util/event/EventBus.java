package org.firstinspires.ftc.teamcode.util.event;

import org.firstinspires.ftc.teamcode.util.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EventBus
{
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
        
        Subscriber(Class<T> evClass, SubCallback<T> callback, String name, int channel)
        {
            this.evClass = evClass;
            this.callback = callback;
            this.name = name;
            this.channel = channel;
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
        Subscriber<T> sub = new Subscriber<T>(evClass, callback, name, channel);
        subscribers.add(sub);
        log.d("Subscriber Add: '%s' receives %s on channel %d", name, evClass.getSimpleName(), channel);
        return sub;
    }
    
    public void unsubscribe(Subscriber<?> sub)
    {
        subscribers.remove(sub);
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
        String callingClassName = Thread.currentThread().getStackTrace()[1].getClassName();
        log.d("Push %s on channel %d (from %s)", ev.getClass().getSimpleName(), ev.getChannel(), callingClassName);
    }
    
    @SuppressWarnings("unchecked")
    public void update()
    {
        // copy the subscriber list so they can remove themselves if they wish
        List<Subscriber<?>> oldSubs = new ArrayList<>(subscribers);
        for (Event ev : events)
        {
            log.v("Event: %s on channel %d: %s", ev.getClass().getSimpleName(), ev.getChannel(), ev.toString());
            for (Subscriber sub : oldSubs)
            {
                if (ev.getClass() == sub.evClass && ev.getChannel() == sub.channel)
                {
                    log.v(" -> Send to subscriber '%s'", sub.name);
                    sub.callback.run(ev, this, sub);
                }
            }
        }
    }
}
