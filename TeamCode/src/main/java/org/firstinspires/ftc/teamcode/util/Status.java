package org.firstinspires.ftc.teamcode.util;

import java.util.HashMap;

public class Status {
    // Odometry
    public static final double ROTATIONAL_TICKS = 8192;
    public static final double WHEEL_CIRCUMFERENCE = 1.377953 * Math.PI;
    public static final double UP_POS_LEFT = 0.812;
    public static final double UP_POS_RIGHT = 0.239;
    public static final double DOWN_POS_LEFT = 0;
    public static final double DOWN_POS_RIGHT = 1;
    public static final double FORWARD_KP = 0.007;
    public static final double STRAFE_KP = 0.033;
    public static final double TURN_KP = 0.03;
    public static final double FORWARD_KI = 0.0009; // 0.003
    public static final double STRAFE_KI = 0.0002; // 0.003
    public static final double TURN_KI = 0.0004;

    // Lift
    public static final double UPPER_LIMIT =754;
    public static final double RAISE_SPEED = 1;
    public static final double LOWER_SPEED = 0.1;
    public static final double kP = 0.008;
    public static final double kI = 0;
    public static final double kD = 0;
    public static final HashMap<String, Double> STAGES = new HashMap<String, Double>(){{
        put("low", 204.0);
        put("mid", 353.0);
        put("high", 718.0);
    }};

    // Arm
    public static final HashMap<String, Double> EXTENSIONS = new HashMap<String, Double>(){{
        put("high_left", .82);
        put("high_right", 0.34);
        put("low_left", 0.76);
        put("low_right", 0.44);
        put("mid_left", .79);
        put("mid_right", .38);
        put("center_from_left", 0.59);
        put("center_from_right", 0.55);
    }};
    public static final double ARM_WAIT_TIME = 0.7;

    // Deposit
    public static final HashMap<String, Double> DEPOSITS = new HashMap<String, Double>(){{
        put("left", 0.25);
        put("right", 0.8);
        put("center", 0.504);
    }};
}