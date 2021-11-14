package org.firstinspires.ftc.teamcode.util;

import java.util.HashMap;

public class Status {
    // Odometry
    public static final double MOVEMENT_TICKS = 1896.5; // TODO Could tune better (tuned to __ ticks/in)
    public static final double ROTATIONAL_TICKS = 42475.5; // TODO Could tune better (tuned to __ ticks/degree)
    public static final double UP_POS_LEFT = 0.812;
    public static final double UP_POS_RIGHT = 0.239;
    public static final double DOWN_POS_LEFT = 0;
    public static final double DOWN_POS_RIGHT = 1;
    public static final double FORWARD_KP = 0.17;
    public static final double STRAFE_KP = 0.25;
    public static final double TURN_KP = 0.01;

    // Lift
    public static final double UPPER_LIMIT = 810;
    public static final double RAISE_SPEED = 1;
    public static final double LOWER_SPEED = 0.1;
    public static final double kP = 0.005;
    public static final double kI = 0;
    public static final double kD = 0;
    public static final HashMap<String, Double> STAGES = new HashMap<String, Double>(){{
        put("low_actual", 50.0);
        put("low_above", 300.0);
        put("mid", 360.0);
        put("high", 800.0);
    }};

    // Arm
    public static final HashMap<String, Double> EXTENSIONS = new HashMap<String, Double>(){{
        put("left", 1.0);
        put("right", 0.22);
        put("center_from_left", 0.57);
        put("center_from_right", 0.52);
        put("neutral_left", 0.75);
        put("neutral_right", 0.37);
    }};
    public static final double ARM_WAIT_TIME = 0.62;
    public static final double EXTENDABLE_THRESHOLD = 100;

    // Deposit
    public static final HashMap<String, Double> DEPOSITS = new HashMap<String, Double>(){{
        put("left", 0.17);
        put("right", 0.95);
        put("center", 0.504);
    }};
}