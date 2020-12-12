package org.firstinspires.ftc.teamcode.util.event;

import org.firstinspires.ftc.teamcode.util.Logger;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.concurrent.Callable;

// TODO: WORK IN PROGRESS; VERY INCOMPLETE
public class EventFlow
{
    private final EventBus bus;
    private HashMap<String, Node> nodes; // nodes by name; hard references here only to make cleanup easier
    private Node rootNode;
    private Logger log = new Logger("Event Flow");
    
    public <T extends Event> EventFlow(EventBus bus, EventBus.Subscriber<T> rootSub, String rootName)
    {
        this.bus = bus;
        if (rootSub.subbed())
        {
            bus.unsubscribe(rootSub);
        }
        rootNode = new Node(rootSub, rootName);
    }
    
    private static class Node
    {
        public final EventBus.Subscriber<?> sub;
        protected WeakReference<Node> next;
        protected String name;
        
        protected Node(EventBus.Subscriber<?> sub, String name)
        {
            this.sub = sub;
            this.name = name;
            next = null;
        }
        
        protected Node getNext()
        {
            return next.get();
        }
    }
    
    @FunctionalInterface
    public interface BranchCondition
    {
        boolean branch();
    }
    
    private static class BranchNode extends Node
    {
        protected WeakReference<Node> branch;
        protected BranchCondition condition;
        
        protected BranchNode(EventBus.Subscriber<?> sub, String name,BranchCondition cond)
        {
            super(sub, name);
            this.condition = cond;
        }
        
        @Override
        protected Node getNext()
        {
            if (condition.branch()) return branch.get();
            else return next.get();
        }
    }
}
