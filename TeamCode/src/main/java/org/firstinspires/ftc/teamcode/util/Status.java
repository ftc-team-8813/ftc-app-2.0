package org.firstinspires.ftc.teamcode.util;

import java.util.HashMap;

public class Status {
    // Odometry
    public static final double ROTATIONAL_TICKS = 8192;
    public static final double WHEEL_DIAMETER = 1.377953;
    public static final double ROBOT_RADIUS_SIDE = 8.5;
    public static final double ROBOT_RADIUS_FRONT = 5.5;
    public static final double RELEASE_POS_1 = 0;
    public static final double RELEASE_POS_2 = 0;

    // Four Bar
    public static final double LOWER_LIMIT = 0;
    public static final double UPPER_LIMIT = 3000;
    public static final double THRESHOLD = 0.01;
    public static final double kP = 1;
    public static final HashMap<String, Double> STAGES = new HashMap<String, Double>(){{put("low", 0.0); put("mid", 0.0); put("high", 0.0);}};
    public static final double DELTA_MULTIPLIER = 100;
    public static final double DEPOSIT_EXTEND_LEFT = 0;
    public static final double DEPOSIT_EXTEND_RIGHT = 0;
    public static final double DEPOSIT_RETRACT = 0;
}