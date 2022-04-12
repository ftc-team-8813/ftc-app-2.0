package org.firstinspires.ftc.teamcode.util;

import java.util.HashMap;

public class Status {
    // Drivetrain
    public static final double HEADING_CORRECTION_kP = 0.02;
    public static final double HEADING_CORRECTION_kI = 0.02;

    //Odometry

    // Intake
    public static final double FREIGHT_DETECTION = 2.5;
    public static final double HOLD_TIME = 0.5;
    public static final double CLOSE_DEPOSIT = 0;
    public static final double OPEN_DEPOSIT = 0;

    // Lift
    public static final double MAX_HEIGHT = 100000;
    public static final double PITSTOP = 30000;
    public static double LIFT_KP = 0.0002;

    // Pivot
    public static final double TURN_LIMIT = 90;
    public static final double DEGREES_PER_TICK = 0.0248;
    public static double PIVOT_KP = 0.06;
}