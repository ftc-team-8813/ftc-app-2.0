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
    private static final int[] formatVersion = {1, 0, 1};
    private double defaultSpeed;
    // private PositionSrc positionSrc;
    // private AngleSrc angleSrc;
    private HashMap<String, Timer> timers;
    private HashMap<String, Actuator> actuators;
    private HashMap<String, ConditionProducer> conditions;
    private HashMap<String, Double> constants;
    
    private ArrayList<PathEntry> paths;
    private int currPath;
    
    private AngleHold angleHold;
    private double fwdTarget = 0;
    private double speed = 0;
    private boolean sendEvent = false;
    private double kP;
    
    private static final String[] comparisons = {
            "==", "!=", "<", ">", "<=", ">="
    };
    
    private EventBus evBus;
    private Scheduler scheduler;
    private Robot robot;
    private File jsonFile;
    
    public double[] navTelemetry = new double[6];
    
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
        paths = new ArrayList<>();
        robot.drivetrain.resetEncoders();
        this.angleHold = new AngleHold(new IMU(robot.imu), evBus, scheduler, navConfig);
        this.kP = navConfig.get("dist_kp").getAsDouble();
        log.i("kP=%.3f", kP);
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
        double fwdPos = (robot.drivetrain.top_right.getCurrentPosition() + robot.drivetrain.top_left.getCurrentPosition()) / 2.0;
        double fwdError = fwdTarget - fwdPos;
        double fwdPower = Range.clip(kP * fwdError * speed, -speed, speed);
        if (Math.abs(fwdPower / speed) < 0.05 && sendEvent)
        {
            sendEvent = false;
            evBus.pushEvent(new NavMoveEvent(NavMoveEvent.FORWARD_COMPLETE));
        }
        double turnPower = angleHold.getTurnPower();
        robot.drivetrain.telemove(fwdPower, -turnPower);
        navTelemetry[0] = fwdPower;
        navTelemetry[1] = turnPower;
        navTelemetry[2] = fwdPos;
        navTelemetry[3] = angleHold.getHeading();
        navTelemetry[4] = currPath;
        navTelemetry[5] = speed;
        telemetry.addData("Forward target", "%.3f", fwdTarget);
        telemetry.addData("Turn target", "%.3f", angleHold.getTarget());
        telemetry.addData("Forward position", "%.3f", fwdPos);
        telemetry.addData("Power", fwdPower);
        telemetry.addData("Heading", "%.3f", angleHold.getHeading());
        telemetry.addData("Path", currPath);
    }
    
    private void setFwdTarget(double target, double speed)
    {
        this.speed = speed;
        fwdTarget = target;
        sendEvent = true;
    }
    
    /*
         {
           "version": "1.0.1",
           "defaultSpeed": default speed,
           "positionSrc": "drive", [or odometry] // for future implementation
           "angleSrc": "imu", [or drive, or odometry] // for future implementation
           "timers": {
             timer name: delay
           }
           "constants": {
             name: value
           }
           "path": [
             {
               "type": forward/turn/actuator/nop,
               "dist": [if forward] distance to move, {or constant name}
               "rotation": [if turn] angle to rotate, {or constant name}
               "ensure": make sure the position is correct, [unused]
               "speed": [optional, if forward or turn] non-default speed, {or constant name}
               "absolute": if true, set position instead of adding
               
               "actuator": { [if external]
                 "name": registered actuator name,
                 "params": { actuator parameters },
               },
               
               [only trigger and condition available for nop]
               
               // if not present, jump immediately to this path entry
               "trigger": {
                 "class": full class name of event,
                 "channel": event channel,
                 "timer": timer name (instead of channel)
               },
               
               
               "condition": {
                 "name": registered conditional name
                 "cond": "=="/"<"/">"/"<="/">="/"!="
                 "value": value to compare against {or constant name}
                 "jumpTrue": which index to jump to
                 "jumpFalse": which index to jump to otherwise (optional)
               }
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
        // positionSrc = PositionSrc.valueOf(root.get("positionSrc").getAsString());
        // angleSrc = AngleSrc.valueOf(root.get("angleSrc").getAsString());
        timers = new HashMap<>();
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
        
        constants = new HashMap<>();
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
    
    private class PathEntry
    {
        PathType type;
        double distance;
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
            if (type == PathType.forward)
            {
                distance = getNumOrConstant(entry.get("dist"));
                log.d("    -> Distance: %.1f", distance);
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
                jumpTrue = condInfo.get("jumpTrue").getAsInt();
                jumpFalse = condInfo.has("jumpFalse") ? condInfo.get("jumpFalse").getAsInt() : -1;
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
            if (type == PathType.forward)
            {
                if (absolute) setFwdTarget(distance, speed);
                else setFwdTarget(fwdTarget + distance, speed);
                log.d("-> Actually run path -> Move forward (abs=%s) %.1f ticks @ power=%.3f",
                        absolute, distance, speed);
                log.d("  -> Target position: %.1f", fwdTarget);
                evBus.subscribe(NavMoveEvent.class, (ev, bus, sub) -> {
                    runNextPath();
                    bus.unsubscribe(sub);
                }, "Forward Move Complete", NavMoveEvent.FORWARD_COMPLETE);
            }
            else if (type == PathType.turn)
            {
                if (absolute) angleHold.setTarget(rotation);
                else angleHold.setTarget(angleHold.getTarget() + rotation);
                log.d("-> Actually run path -> Turn (abs=%s) %.3f degrees", absolute, rotation);
                log.d("  -> Target angle: %.3f", angleHold.getTarget());
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
        int maj   = Integer.parseInt(split[0]);
        int min   = Integer.parseInt(split[1]);
        int patch = Integer.parseInt(split[2]);
        if (maj > formatVersion[0] || min > formatVersion[1] || patch > formatVersion[2])
            throw new IllegalArgumentException(String.format("Unsupported future version -- %d.%d.%d > %d.%d.%d",
                    maj, min, patch, formatVersion[0], formatVersion[1], formatVersion[2]));
        if (maj < formatVersion[0] || min < formatVersion[1])
            throw new IllegalArgumentException(String.format("Incompatible past version -- %d.%d.%d < %d.%d.%d",
                    maj, min, patch, formatVersion[0], formatVersion[1], formatVersion[2]));
    }
    
    private enum PositionSrc
    {
        drive,
        odometry
    }
    
    private enum AngleSrc
    {
        imu,
        drive,
        odometry
    }
    
    private enum PathType
    {
        forward,
        turn,
        actuator,
        nop
    }
}
