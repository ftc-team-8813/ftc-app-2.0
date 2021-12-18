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
    public static final double FREIGHT_DETECTION = 2.4;

    // Lift
    public static final double SENSITIVITY = 400;
    public static final double RETRACT_POWER_THRESHOLD = 7500;
    public static final double UPPER_LIMIT = 53000;
    public static final double MAX_SPEED = 0.9;
    public static final double RETRACT_SPEED = 0.95;
    public static final double kP = 0.0005;
    public static final double kI = 0;
    public static final double kD = 0;
    public static final HashMap<String, Double> STAGES = new HashMap<String, Double>(){{
        put("pitstop", 13000.0);
        put("neutral", 15000.0);
        put("low", 33200.0);
        put("mid", 43000.0);
        put("high", 46500.0);
    }};

    // Arm
    public static final HashMap<String, Double> ROTATIONS = new HashMap<String, Double>(){{
        put("high_out", 0.433);
        put("mid_out", 0.568);
        put("low_out", 0.58);
        put("neutral_out", 0.7);
        put("in", 0.834);
    }};
    public static final double BUCKET_WAIT_TIME = 0.2;
    public static final double PITSTOP_WAIT_TIME = 0.15;

    // Deposit
    public static final HashMap<String, Double> DEPOSITS = new HashMap<String, Double>(){{
        put("dump", 0.1);
        put("carry", 0.7);
        put("front", 0.27);
        put("back", 0.98);
    }};
}