package org.firstinspires.ftc.teamcode.util;

import java.util.HashMap;

public class Status {
    // Drivetrain
    public static final double TURN_CORRECTION_P = 0.04;
    public static final double MAX_VELOCITY = 0;

    //Odometry
    public static final double WHEEL_DIAMETER = 96; //mm
    public static final double TICKS_PER_ROTATION = 28; //encoder counts/revolution of the bare motor
    public static final double turnP = 1.3; //no unit
    public static final double LIGHT_MULTIPLIER = 1.5; //initial light sensor value x LIGHT_MULTIPLIER = value expected at tape crossing
    public static final double TAPE_Y_OFFSET = 15.56; //inches from zero to tape line. Sign varies depending on autonomous mode. Positive is blue, negative is red.

    // Intake
    public static final double FREIGHT_DETECTION = 2.6;
    public static final long TIME_BEFORE_INTAKING = 100000000; // nanoseconds

    // Lift
    public static final double SENSITIVITY = 500;
    public static final double RETRACT_POWER_THRESHOLD = 8000;
    public static final double UPPER_LIMIT = 70000;
    public static final double MAX_SPEED = 1;
    public static final double RETRACT_SPEED = 1;
    public static final double kP = 0.00021;
    public static final double kI = 0.000001;
    public static final double kD = 0.000004 ;
    public static final HashMap<String, Double> STAGES = new HashMap<String, Double>(){{
        put("pitstop", 10000.0);
        put("neutral", 10100.0);
        put("low", 34400.0);
        put("mid", 41500.0);
        put("high", 46500.0);
        put("high2", 48500.0);
        put("really high", 68000.0);
        put("speed mode threshold", 35000.0);
    }};

    // Arm
    public static final HashMap<String, Double> ROTATIONS = new HashMap<String, Double>(){{
        put("high_out", 0.436);
        put("mid_out", 0.568);
        put("low_out", 0.58);
        put("neutral_out", 0.6);
        put("high_out2", 0.43);
        put("in", 0.82);
    }};
    public static final double BUCKET_WAIT_TIME = 0.5;
    public static final double PITSTOP_WAIT_TIME = 0.5;
    public static final double PITSTOP_WAIT_TIME_OUT = 0.01;

    // Outrigger
    public static final HashMap<String, Double> OUTRIGGERS = new HashMap<String, Double>(){{
        put("up", 1.0);
        put("down", 0.0);
    }};

    // Deposit
    public static final HashMap<String, Double> DEPOSITS = new HashMap<String, Double>(){{
        put("dump", 0.0);
        put("carry", 0.7);
        put("front", 0.355);
        put("back", 1.0);
        put("tilt", 0.7); //when the slides are extending, the bucket tilts a little bit to save time
    }};
}