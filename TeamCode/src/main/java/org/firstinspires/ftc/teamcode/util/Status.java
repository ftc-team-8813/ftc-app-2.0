package org.firstinspires.ftc.teamcode.util;

import java.util.HashMap;

public class Status {
    // Odometry
    public static final double ROTATIONAL_TICKS = 8192;
    public static final double WHEEL_DIAMETER = 1.377953;
    public static final double REVOLUTION_TICKS = 47931;
    public static final double UP_POS_LEFT = 0;
    public static final double UP_POS_RIGHT = 0;
    public static final double DOWN_POS_LEFT = 0;
    public static final double DOWN_POS_RIGHT = 1;

    // Four Bar
    public static final double UPPER_LIMIT = 3000;
    public static final double SPEED_CAP = 1;
    public static final double kP = 0.005;
    public static final double kI = 0;
    public static final double kD = 0;
    public static final HashMap<String, Double> STAGES = new HashMap<String, Double>(){{put("low", 100.0); put("mid", 500.0); put("high", 1000.0);}};
    public static final HashMap<String, Double> EXTENSIONS = new HashMap<String, Double>(){{put("left", 700.0); put("right", 1300.0); put("center", 2400.0);}};
    public static final HashMap<String, Double> DEPOSITS = new HashMap<String, Double>(){{put("left", 700.0); put("right", 1300.0); put("center", 2400.0);}};
    public static final double BLOCK_DETECT = 11;
    public static final double ARM_AWAY = 20;
}