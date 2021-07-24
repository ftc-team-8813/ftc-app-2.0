package org.firstinspires.ftc.teamcode.util.event;

import org.firstinspires.ftc.teamcode.util.Logger;

import java.util.ArrayList;
import java.util.List;

public class EventFlow
{
    private final EventBus bus;
    private List<Node> nodes; // nodes by name; hard references here only to make cleanup easier
    private Node rootNode;
    private Node currentNode;
    private Logger log = new Logger("Event Flow");
    private int jumpTarget = -1;
    private boolean stop;
    
    public EventFlow(EventBus bus)
    {
        this.bus = bus;
        nodes = new ArrayList<>();
    }
    
    public <T extends Event> NodeBuilder start(EventBus.Subscriber<T> rootSub)
    {
        if (!nodes.isEmpty()) throw new IllegalStateException("Event flow already built");
        if (rootSub.subbed())
        {
            log.w("Unsubscribing node");
            bus.unsubscribe(rootSub);
        }
        rootNode = new Node(rootSub);
        rootNode.subscribe();
        nodes.add(rootNode);
        return new NodeBuilder(rootNode);
    }
    
    public void jump(int index)
    {
        jumpTarget = index;
        log.d("Jump -> %d", index);
    }
    
    public void forceJump(int index)
    {
        currentNode.unsubscribe();
        nodes.get(index).subscribe();
    }
    
    public void stop()
    {
        stop = true;
    }
    
    public class NodeBuilder
    {
        private Node prevNode;
        
        protected NodeBuilder(Node prevNode)
        {
            this.prevNode = prevNode;
        }
        
        public NodeBuilder then(EventBus.Subscriber<?> sub)
        {
            if (sub.subbed())
            {
                log.w("Subscriber already subscribed; unsubscribing");
                bus.unsubscribe(sub);
            }
            Node nextNode = new Node(sub);
            nodes.add(nextNode);
            prevNode.next = nextNode;
            return new NodeBuilder(nextNode);
        }
    }
    
    private class Node
    {
        public final EventBus.Subscriber<?> sub;
        protected Node next;
        protected String name;
        
        protected <T extends Event> Node(EventBus.Subscriber<T> sub)
        {
            this.sub = new EventBus.Subscriber<>(sub.evClass,
                    (ev, bus1, sub1) -> {
                        sub.callback.run(ev, bus1, sub1);
                        unsubscribe();
                        if (jumpTarget >= 0)
                        {
                            nodes.get(jumpTarget).subscribe();
                            jumpTarget = -1;
                        }
                        else if (!stop)
                        {
                            if (next != null)
                            {
                                next.subscribe();
                            }
                            else
                            {
                                rootNode.subscribe();
                            }
                        }
                    }, sub.name, sub.channel);
            this.name = sub.name;
            next = null;
        }
        
        protected void subscribe()
        {
            currentNode = this;
            bus.subscribe(sub);
        }
        
        protected void unsubscribe()
        {
            bus.unsubscribe(sub);
        }
        
        public Node getNext()
        {
            return next;
        }
    }
    
    @FunctionalInterface
    public interface BranchCondition
    {
        boolean branch();
    }
    
    // TODO: figure out syntax
    private class BranchNode extends Node
    {
        protected Node branch;
        protected BranchCondition condition;
        
        protected BranchNode(EventBus.Subscriber<?> sub, BranchCondition cond)
        {
            super(sub);
            this.condition = cond;
        }
        
        @Override
        public Node getNext()
        {
            if (condition.branch()) return branch;
            else return next;
        }
    }
}
