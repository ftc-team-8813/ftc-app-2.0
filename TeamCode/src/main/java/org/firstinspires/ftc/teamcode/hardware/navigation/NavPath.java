package org.firstinspires.ftc.teamcode.hardware.navigation;

import com.google.gson.JsonObject;

import org.firstinspires.ftc.teamcode.util.Configuration;

import java.io.File;

// NavPath objects load path data from a JSON file
public class NavPath
{
    private double defaultSpeed;
    
    public NavPath(File jsonFile)
    {
    
    }
    
    
    /*
         {
           "defaultSpeed": default speed,
           "positionSrc": "drive", [or odometry] // for future implementation
           "angleSrc": "imu", [or drive, or odometry] // for future implementation
           "timers": {
             timer name: delay
           }
           "path": [
             {
               "type": forward/turn/external,
               "dist": [if forward] distance to move,
               "rotation": [if turn] angle to rotate,
               "ensure": [if forward] make sure the position is correct,
               "speed": [optional, if forward or turn] non-default speed,
               "absolute": if true, set
               
               "actuator": { [if external]
                 "name": registered actuator name,
                 "params": { actuator parameters },
               },
               
               "trigger": {
                 "class": full class name of event,
                 "channel": event channel,
                 "timer": timer name (instead of channel)
               }
             }
           ]
         }
         */
    private void load(File jsonFile)
    {
        JsonObject root = Configuration.readJson(jsonFile);
        
    }
}
