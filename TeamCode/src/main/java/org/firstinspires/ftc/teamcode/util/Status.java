package org.firstinspires.ftc.teamcode.util;

import java.util.HashMap;

public class Status {
    // Odometry
    public static final double ROTATIONAL_TICKS = 8192;
    public static final double WHEEL_CIRCUMFERENCE = 1.377953 * Math.PI;
    public static final double FORWARD_KP = 0.007;
    public static final double STRAFE_KP = 0.033;
    public static final double TURN_KP = 0.03;
    public static final double FORWARD_KI = 0.0009; // 0.003
    public static final double STRAFE_KI = 0.0002; // 0.003
    public static final double TURN_KI = 0.0004;

    // Intake
    public static final double FREIGHT_DETECTION = 5.0;

    // Lift
    public static final double SENSITIVITY = 100;
    public static final double ROTATABLE_THRESHOLD = 0;
    public static final double UPPER_LIMIT = 100000;
    public static final double RAISE_SPEED = 1;
    public static final double LOWER_SPEED = 0.1;
    public static final double kP = 0.003;
    public static final double kI = 0;
    public static final double kD = 0;
    public static final HashMap<String, Double> STAGES = new HashMap<String, Double>(){{
        put("low", 200.0);
        put("mid", 351.0);
        put("high", 710.0);
    }};

    // Arm
    public static final HashMap<String, Double> EXTENSIONS = new HashMap<String, Double>(){{
        put("out", 0.0); // Only extends one side so no need for left/right/mid
        put("in", 0.0);
    }};

    // Deposit
    public static final HashMap<String, Double> DEPOSITS = new HashMap<String, Double>(){{
        put("out", 0.0); // Only extends one side so no need for left/right/mid
        put("in", 0.0);
    }};
}