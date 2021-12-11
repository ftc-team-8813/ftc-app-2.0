package org.firstinspires.ftc.teamcode.util;

import java.util.HashMap;

public class Status {
    // Odometry
    public static final double FORWARD_KP = 0.007;
    public static final double STRAFE_KP = 0.033;
    public static final double TURN_KP = 0.03;
    public static final double FORWARD_KI = 0.0009; // 0.003
    public static final double STRAFE_KI = 0.0002; // 0.003
    public static final double TURN_KI = 0.0004;

    // Intake
    public static final double FREIGHT_DETECTION = 2.6;

    // Lift
    public static final double SENSITIVITY = 800;
    public static final double ROTATABLE_THRESHOLD = 30000;
    public static final double RETRACT_POWER_THRESHOLD = 7500;
    public static final double UPPER_LIMIT = 100000;
    public static final double MAX_SPEED = 0.50;
    public static final double RETRACT_SPEED = 0.6;
    public static final double kP = 0.0005;
    public static final double kI = 0;
    public static final double kD = 0;
    public static final HashMap<String, Double> STAGES = new HashMap<String, Double>(){{
        put("pitstop", 24000.0);
        put("low", 30000.0);
        put("mid", 35000.0);
        put("neutral", 21577.0);
        put("high", 42000.0);
    }};

    // Arm
    public static final HashMap<String, Double> EXTENSIONS = new HashMap<String, Double>(){{
        put("out", 0.45); // Only extends one side so no need for left/right/mid
        put("in", 0.8);
    }};

    // Deposit
    public static final HashMap<String, Double> DEPOSITS = new HashMap<String, Double>(){{
        put("dump", 0.03); // Only extends one side so no need for left/right/mid
        put("carry", 0.65);
        put("front", 0.33);
        put("back", 0.98);
    }};
}