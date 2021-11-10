package org.firstinspires.ftc.teamcode.util;

import java.util.HashMap;

public class Status {
    // Odometry
    public static final double ROTATIONAL_TICKS = 8192;
    public static final double WHEEL_DIAMETER = 1.377953;
    public static final double REVOLUTION_TICKS = 47931;
    public static final double UP_POS_LEFT = 0.812;
    public static final double UP_POS_RIGHT = 0.239;
    public static final double DOWN_POS_LEFT = 0;
    public static final double DOWN_POS_RIGHT = 1;

    // Lift
    public static final double UPPER_LIMIT = 1500;
    public static final double RAISE_SPEED = 1;
    public static final double LOWER_SPEED = 0.25;
    public static final double kP = 0.004;
    public static final double kI = 0;
    public static final double kD = 0;
    public static final HashMap<String, Double> STAGES = new HashMap<String, Double>(){{put("low", 400.0); put("mid", 800.0); put("high", 1200.0);}};

    // Arm
    public static final HashMap<String, Double> EXTENSIONS = new HashMap<String, Double>(){{put("left", 1.0); put("right", 0.22); put("center", 0.56);}};
    public static final double ARM_WAIT_TIME = 0.55;
    public static final double EXTENDABLE_THRESHOLD = 100;

    // Deposit
    public static final HashMap<String, Double> DEPOSITS = new HashMap<String, Double>(){{put("left", 0.246); put("right", 0.736); put("center", 0.504);}};
}