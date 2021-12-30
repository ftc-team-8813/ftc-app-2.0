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

    //Drivetrain Odometry
    public static final double WHEEL_DIAMETER = 96; //mm
    public static final double TICKS_PER_ROTATION = 28;

    // Intake
    public static final double FREIGHT_DETECTION = 2.8;
    public static final long TIME_BEFORE_INTAKING = 100000000; // nanoseconds

    // Lift
    public static final double SENSITIVITY = 500;
    public static final double RETRACT_POWER_THRESHOLD = 8000;
    public static final double UPPER_LIMIT = 70000;
    public static final double MAX_SPEED = 1;
    public static final double RETRACT_SPEED = 1;
    public static final double kP = 0.0007;
    public static final double kI = 0;
    public static final double kD = 0;
    public static final HashMap<String, Double> STAGES = new HashMap<String, Double>(){{
        put("pitstop", 10500.0);
        put("neutral", 9700.0);
        put("low", 34400.0);
        put("mid", 41500.0);
        put("high", 46500.0);
        put("high2", 50000.0);
        put("really high", 68000.0);
    }};

    // Arm
    public static final HashMap<String, Double> ROTATIONS = new HashMap<String, Double>(){{
        put("high_out", 0.433);
        put("mid_out", 0.568);
        put("low_out", 0.58);
        put("neutral_out", 0.6);
        put("high_out2", 0.43);
        put("in", 0.82);
    }};
    public static final double BUCKET_WAIT_TIME = 0.2;
    public static final double PITSTOP_WAIT_TIME = 0.15;
    public static final double OUTRIGGER_UP = 1.0;
    public static final double OUTRIGGER_DOWN = 0.0;

    // Deposit
    public static final HashMap<String, Double> DEPOSITS = new HashMap<String, Double>(){{
        put("dump", 0.1);
        put("carry", 0.7);
        put("front", 0.36);
        put("back", 0.98);
    }};
}