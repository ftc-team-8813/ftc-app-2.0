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
    public static final double UPPER_LIMIT = 3000;
    public static final double SPEED_CAP = 0.35;
    public static final double kP = 0.00085;
    public static final double kI = 0.00003;
    public static final HashMap<String, Double> STAGES = new HashMap<String, Double>(){{put("low", 700.0); put("mid", 1300.0); put("high", 2400.0);}};
    public static final double DELTA_MULTIPLIER = 50;
    public static final double DEPOSIT_EXTEND_LEFT = 1;
    public static final double DEPOSIT_EXTEND_RIGHT = 0.15;
    public static final double DEPOSIT_RESET = 0.6;
    public static final double DEPOSIT_OPEN = 0.139;
    public static final double DEPOSIT_CLOSED = 0.711;
    public static final double BLOCK_DETECT = 8.5;
    public static final double ARM_AWAY = 20;
}