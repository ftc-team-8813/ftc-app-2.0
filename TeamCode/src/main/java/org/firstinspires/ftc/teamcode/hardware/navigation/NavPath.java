package org.firstinspires.ftc.teamcode.hardware.navigation;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.IMU;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.hardware.events.NavMoveEvent;
import org.firstinspires.ftc.teamcode.util.Configuration;
import org.firstinspires.ftc.teamcode.util.Logger;
import org.firstinspires.ftc.teamcode.util.Scheduler;
import org.firstinspires.ftc.teamcode.util.Scheduler.Timer;
import org.firstinspires.ftc.teamcode.util.Time;
import org.firstinspires.ftc.teamcode.util.event.Event;
import org.firstinspires.ftc.teamcode.util.event.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// NavPath objects load path data from a JSON file
public class NavPath
{
    private static final int[] formatVersion = {1, 1, 0};
    private double defaultSpeed;
    private HashMap<String, Timer> timers;
    private HashMap<String, Actuator> actuators;
    private HashMap<String, ConditionProducer> conditions;
    private HashMap<String, Double> constants;
    private HashMap<String, Integer> labels;
    
    private ArrayList<PathEntry> paths;
    private int currPath;
    
    private Navigator navigator;
    private double lastTargetAngle;
    
    private static final String[] comparisons = {
            "==", "!=", "<", ">", "<=", ">="
    };
    
    private EventBus evBus;
    private Scheduler scheduler;
    private Robot robot;
    private File jsonFile;
    
    private Logger log = new Logger("Nav Path");
    private boolean pathComplete;
    
    private static boolean compare(int comp, double a, double b)
    {
        switch (comp)
        {
            case 0: return a == b;
            case 1: return a != b;
            case 2: return a <  b;
            case 3: return a >  b;
            case 4: return a <= b;
            case 5: return a >= b;
        }
        return false;
    }
    
    public interface Actuator
    {
        void move(JsonObject actuatorParams);
    }
    
    public interface ConditionProducer
    {
        double get();
    }
    
    public NavPath(File jsonFile, EventBus bus, Scheduler scheduler, Robot robot, JsonObject navConfig)
    {
        this.robot = robot;
        this.evBus = bus;
        this.scheduler = scheduler;
        this.jsonFile = jsonFile;
        actuators = new HashMap<>();
        conditions = new HashMap<>();
        timers = new HashMap<>();
        constants = new HashMap<>();
        labels = new HashMap<>();
        paths = new ArrayList<>();
        
        navigator = new Navigator(robot.drivetrain, robot.drivetrain.getOdometry(), evBus);
    }
    
    public Navigator getNavigator()
    {
        return navigator;
    }
    
    public void addActuator(String name, Actuator actuator)
    {
        actuators.put(name, actuator);
    }
    
    public void addCondition(String name, ConditionProducer producer)
    {
        conditions.put(name, producer);
    }
    
    public void start()
    {
        paths.get(0).run();
    }
    
    public boolean complete()
    {
        return pathComplete;
    }
    
    public void loop(Telemetry telemetry)
    {
        navigator.update(telemetry);
    }
    
    private void setXYTarget(double x, double y, double speed)
    {
        navigator.setForwardSpeed(speed);
        navigator.setTurnSpeed(speed);
        navigator.goTo(x, y);
    }
    
    private void setAngleTarget(double angle)
    {
        lastTargetAngle = angle;
        navigator.turnAbs(angle);
    }
    
    /*
         {
           "version": "1.1.0",
           "defaultSpeed": default speed,
           "timers": {
             timer name: delay
           }
           "constants": {
             name: value
           }
           "path": [
             {
               "type": drive/turn/actuator/nop,
               "x": target x position (if drive) {or constant}
               "y": target y position (if drive) {or constant}
               "rotation": target heading, in degrees (if turn) {or constant}
               "speed": [optional, if forward or turn] non-default speed, {or constant}
               "absolute": if true, set position instead of adding
               
               "actuator": { [if external]
                 "name": registered actuator name,
                 "params": { actuator parameters },
               },
               
               [nop can only have trigger and condition]
               
               // if not present, jump immediately to this path entry
               "trigger": {
                 "class": full class name of event,
                 "channel": event channel,
                 "timer": timer name (instead of channel), if class is TimerEvent
               },
               
               
               "condition": {
                 "name": registered conditional name
                 "cond": "=="/"<"/">"/"<="/">="/"!="
                 "value": value to compare against {or constant name}
                 "jumpTrue": which index/label to jump to
                 "jumpFalse": which index/label to jump to otherwise (optional)
               }
               
               // optional
               "label": label name
             }
           ]
         }
         */
    public void load()
    {
        log.d("Loading path from %s", jsonFile.getPath());
        JsonObject root = Configuration.readJson(jsonFile);
        String versionCode = root.get("version").getAsString();
        log.d("-> File version: %s", versionCode);
        validateVersion(versionCode);
        
        defaultSpeed = root.get("defaultSpeed").getAsDouble();
        log.d("-> Default speed: %.3f", defaultSpeed);
        if (root.has("timers"))
        {
            JsonObject timers = root.getAsJsonObject("timers");
            log.d("-> Loading timers");
            for (Map.Entry<String, JsonElement> entry : timers.entrySet())
            {
                double delay = entry.getValue().getAsDouble();
                Timer timer = scheduler.addPendingTrigger(delay, "NavPath-" + entry.getKey());
                log.d("  -> Timer '%s' with a delay of %s", entry.getKey(), Time.format(delay));
                this.timers.put(entry.getKey(), timer);
            }
        }
        
        if (root.has("constants"))
        {
            JsonObject consts = root.getAsJsonObject("constants");
            log.d("-> Loading constants");
            for (Map.Entry<String, JsonElement> entry : consts.entrySet())
            {
                String name = entry.getKey();
                double val = entry.getValue().getAsDouble();
                log.d("  -> %s: %.3f", name, val);
                constants.put(name, val);
            }
        }
        
        JsonArray path = root.get("path").getAsJsonArray();
        int i = 0;
        log.d("-> Loading paths");
        // initial pass -- read labels
        for (JsonElement elem : path)
        {
            JsonObject obj = elem.getAsJsonObject();
            if (obj.has("label"))
                labels.put(obj.get("label").getAsString(), i);
            i++;
        }
        
        i = 0;
        
        for (JsonElement elem : path)
        {
            paths.add(new PathEntry(elem.getAsJsonObject(), i));
            i++;
        }
    }
    
    public double getNumOrConstant(JsonElement elem)
    {
        JsonPrimitive prim = elem.getAsJsonPrimitive();
        if (prim.isString())
        {
            String key = prim.getAsString();
            return getConstant(key);
        }
        else return prim.getAsDouble();
    }
    
    public double getConstant(String name)
    {
        Double val = constants.get(name);
        if (val == null) throw new IllegalArgumentException("No constant named " + name);
        return val;
    }
    
    public int getJumpOrLabel(JsonElement elem)
    {
        JsonPrimitive prim = elem.getAsJsonPrimitive();
        if (prim.isString())
        {
            String key = prim.getAsString();
            Integer val = labels.get(key);
            if (val == null) throw new IllegalArgumentException("No label named " + key);
            return val;
        }
        return prim.getAsInt();
    }
    
    private class PathEntry
    {
        PathType type;
        double x, y;
        double rotation;
        boolean ensure;
        boolean absolute;
        double speed;
        JsonObject actuatorParams;
        Actuator actuator;
        String actuatorName;
        EventBus.Subscriber<?> trigger;
        Timer triggerTimer;
        int compareType;
        ConditionProducer producer;
        double compareValue;
        int jumpTrue, jumpFalse;
        int index;
        
        PathEntry(JsonObject entry, int index)
        {
            this.index = index;
            log.d("  -> Path entry %d:", index);
            type = PathType.valueOf(entry.get("type").getAsString());
            log.d("    -> Type: %s", type.name());
            if (type == PathType.drive)
            {
                x = getNumOrConstant(entry.get("x"));
                y = getNumOrConstant(entry.get("y"));
                log.d("    -> Displacement: <%.2f, %.2f>", x, y);
            }
            else if (type == PathType.turn)
            {
                rotation = getNumOrConstant(entry.get("rotation"));
                log.d("    -> Rotation: %.1f", rotation);
            }
            if (type != PathType.actuator)
            {
                speed = defaultSpeed;
                if (entry.has("ensure")) ensure = entry.get("ensure").getAsBoolean();
                if (entry.has("absolute")) absolute = entry.get("absolute").getAsBoolean();
                if (entry.has("speed")) speed = getNumOrConstant(entry.get("speed"));
                log.d("    -> ensure=%s absolute=%s speed=%.3f", ensure, absolute, speed);
            }
            else
            {
                JsonObject actuatorInfo = entry.getAsJsonObject("actuator");
                actuatorName = actuatorInfo.get("name").getAsString();
                actuator = actuators.get(actuatorName);
                actuatorParams = actuatorInfo.getAsJsonObject("params");
                log.d("    -> Actuator '%s':", actuatorName);
                log.d("      -> Parameters: %s", actuatorParams.toString());
            }
            
            if (entry.has("trigger"))
            {
                log.d("    -> Loading trigger info:");
                JsonObject triggerInfo = entry.getAsJsonObject("trigger");
                String className = triggerInfo.get("class").getAsString();
                try
                {
                    Class<?> cls = Class.forName(className);
                    Class<? extends Event> evClass = cls.asSubclass(Event.class);
                    log.d("      -> Event class: %s", className);
                    int channel;
                    if (triggerInfo.has("timer"))
                    {
                        String timerName = triggerInfo.get("timer").getAsString();
                        triggerTimer = timers.get(timerName);
                        if (triggerTimer == null) throw new IllegalArgumentException("Invalid timer: " + timerName);
                        channel = triggerTimer.eventChannel;
                        log.d("      -> Trigger on timer '%s' (channel %d)", timerName, channel);
                    }
                    else
                    {
                        channel = triggerInfo.get("channel").getAsInt();
                        log.d("      -> Trigger on channel %d", channel);
                    }
                    trigger = new EventBus.Subscriber<>(evClass, (ev, bus, sub) -> {
                        actuallyRun();
                        bus.unsubscribe(sub);
                    }, "NavPath sub#" + index, channel);
                } catch (ClassNotFoundException e)
                {
                    throw new IllegalArgumentException(String.format("Trigger class '%s' not found", className), e);
                }
            }
            
            if (entry.has("condition"))
            {
                log.d("    -> Loading condition info:");
                JsonObject condInfo = entry.getAsJsonObject("condition");
                producer = conditions.get(condInfo.get("name").getAsString());
                if (producer == null) throw new IllegalArgumentException("Invalid condition producer: " + condInfo.get("name").getAsString());
                log.d("      -> Producer: %s", condInfo.get("name").getAsString());
                compareType = -1;
                String compStr = condInfo.get("cond").getAsString();
                for (int i = 0; i < comparisons.length; i++)
                {
                    if (compStr.equals(comparisons[i])) compareType = i;
                }
                compareValue = getNumOrConstant(condInfo.get("value"));
                jumpTrue = getJumpOrLabel(condInfo.get("jumpTrue"));
                jumpFalse = condInfo.has("jumpFalse") ? getJumpOrLabel(condInfo.get("jumpFalse")) : -1;
                log.d("      -> Compare: %s %.2f (%d)", compStr, compareValue, compareType);
                log.d("      -> Jump to %d (else %d)", jumpTrue, jumpFalse);
            }
        }
        
        public void run()
        {
            log.d("Run path #%d", index);
            if (trigger != null)
            {
                log.d("-> Wait for trigger %s", trigger.name);
                if (triggerTimer != null) triggerTimer.reset();
                evBus.subscribe(trigger);
            }
            else
            {
                actuallyRun();
            }
        }
        
        private void runNextPath()
        {
            int jumpTo = currPath + 1;
            if (producer != null)
            {
                log.d("-> Check condition");
                double produceVal = producer.get();
                boolean result = compare(compareType, produceVal, compareValue);
                log.d("  -> %.3f %s %.3f => %s", produceVal, comparisons[compareType], compareValue, result);
                if (result) jumpTo = jumpTrue;
                else if (jumpFalse != -1) jumpTo = jumpFalse;
            }
            log.d("-> Jump -> %d", jumpTo);
            currPath = jumpTo;
            if (currPath >= paths.size())
            {
                log.d("-> Complete!");
                pathComplete = true;
                evBus.pushEvent(new NavMoveEvent(NavMoveEvent.NAVIGATION_COMPLETE));
            }
            else
            {
                paths.get(currPath).run();
            }
        }
        
        private void actuallyRun()
        {
            if (type == PathType.drive)
            {
                if (absolute) setXYTarget(x, y, speed);
                else setXYTarget(navigator.getTargetX() + x, navigator.getTargetY() + y, speed);
                log.d("-> Actually run path -> Move (abs=%s) <%.2f,%.2f> inches @ power=%.3f",
                        absolute, x, y, speed);
                log.d("  -> Target position: <%.2f,%.2f>", navigator.getTargetX(), navigator.getTargetY());
                evBus.subscribe(NavMoveEvent.class, (ev, bus, sub) -> {
                    runNextPath();
                    bus.unsubscribe(sub);
                }, "Move Complete", NavMoveEvent.MOVE_COMPLETE);
            }
            else if (type == PathType.turn)
            {
                if (absolute) setAngleTarget(rotation);
                else setAngleTarget(lastTargetAngle + rotation);
                log.d("-> Actually run path -> Turn (abs=%s) %.3f degrees", absolute, rotation);
                log.d("  -> Target angle: %.3f", lastTargetAngle);
                evBus.subscribe(NavMoveEvent.class, (ev, bus, sub) -> {
                    runNextPath();
                    bus.unsubscribe(sub);
                }, "Turn Complete", NavMoveEvent.TURN_COMPLETE);
            }
            else
            {
                if (type == PathType.actuator)
                {
                    log.d("Actually run path -> Run actuator %s -- %s", actuatorName, actuatorParams);
                    actuator.move(actuatorParams);
                }
                else
                {
                    log.d("Actually run path -> Nop");
                }
                runNextPath();
            }
        }
    }
    
    private static void validateVersion(String version)
    {
        String[] split = version.split("\\.");
        int maj = Integer.parseInt(split[0]);
        int min = Integer.parseInt(split[1]);
        int patch = Integer.parseInt(split[2]);
        if (maj > formatVersion[0]
            || (min > formatVersion[1] && maj <= formatVersion[0])
            || (patch > formatVersion[2] && min <= formatVersion[1] && maj <= formatVersion[0]))
            throw new IllegalArgumentException(String.format("Unsupported future version -- %d.%d.%d > %d.%d.%d",
                    maj, min, patch, formatVersion[0], formatVersion[1], formatVersion[2]));
        if (maj < formatVersion[0]
            || min < formatVersion[1])
            throw new IllegalArgumentException(String.format("Incompatible past version -- %d.%d.%d < %d.%d.%d",
                    maj, min, patch, formatVersion[0], formatVersion[1], formatVersion[2]));
    }
    
    private enum PathType
    {
        drive,
        turn,
        actuator,
        nop
    }
}
