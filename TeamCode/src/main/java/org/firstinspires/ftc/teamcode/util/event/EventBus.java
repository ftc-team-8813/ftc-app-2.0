package org.firstinspires.ftc.teamcode.util.event;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EventBus
{
    public interface SubCallback<T extends Event>
    {
        void run(T event, EventBus bus, Subscriber<T> sub);
    }
    
    public class Subscriber<T extends Event>
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
    
    public EventBus()
    {
        events = new ArrayList<>();
        subscribers = new ArrayList<>();
    }
    
    public <T extends Event> Subscriber<T> subscribe(Class<T> evClass, SubCallback<T> callback, String name, int channel)
    {
        Subscriber<T> sub = new Subscriber<T>(evClass, callback, name, channel);
        subscribers.add(sub);
        return sub;
    }
    
    public void unsubscribe(Subscriber<?> sub)
    {
        subscribers.remove(sub);
    }
    
    public void pushEvent(Event ev)
    {
        events.add(ev);
    }
    
    @SuppressWarnings("unchecked")
    public void update()
    {
        // copy the subscriber list so they can remove themselves if they wish
        List<Subscriber<?>> oldSubs = new ArrayList<>(subscribers);
        for (Event ev : events)
        {
            for (Subscriber sub : oldSubs)
            {
                if (ev.getClass() == sub.evClass && ev.getChannel() == sub.channel)
                    sub.callback.run(ev, this, sub);
            }
        }
    }
}
