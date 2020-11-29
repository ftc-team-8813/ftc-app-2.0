package org.firstinspires.ftc.teamcode.util;

import java.util.ArrayList;
import java.util.List;

public class Scheduler
{
    public static double getTime()
    {
        return (double)System.nanoTime() / 1_000_000_000.0;
    }
    
    public class Task
    {
        public Runnable execFunction;
        public double start;
        public double delay;
        public boolean repeat;
        public boolean cancelled;
        public float[] lastExecTimes;
        public int timeLogId; // which slot to log the next iteration time into

        public Task(Runnable execFunction, double delay, boolean repeat)
        {
            this.execFunction = execFunction;
            this.delay = delay;
            this.repeat = repeat;
            this.lastExecTimes = new float[5];
            this.cancelled = false;
            this.start = getTime();
        }
        
        private void exec()
        {
            if (cancelled) return;
            double start = getTime();
            execFunction.run();
            double end = getTime();
            lastExecTimes[timeLogId] = (float)(end - start);
            if (repeat)
            {
                this.start = start;
                // TODO: if (getTime() > start + delay), log a warning
            }
            else
            {
                this.cancelled = true;
            }
        }
    }
    
    private List<Task> tasks;
    
    public Scheduler()
    {
        tasks = new ArrayList<>();
    }
    
    public Task addFutureTask(double delay, Runnable fun)
    {
        Task t = new Task(fun, delay, false);
        tasks.add(t);
        return t;
    }
    
    public Task addRepeatingTask(double delay, Runnable fun)
    {
        Task t = new Task(fun, delay, true);
        tasks.add(t);
        return t;
    }
    
    public void loop()
    {
        for (Task task : new ArrayList<>(tasks))
        {
            double time = getTime();
            if (time >= task.start + task.delay)
            {
                // TODO: if (time >= task.start + task.delay + 0.01) log a warning about timing
                task.exec();
            }
        }
        tasks.removeIf((task) -> task.cancelled);
    }
}
