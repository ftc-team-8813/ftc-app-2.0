package org.firstinspires.ftc.teamcode.opmodes.util;

import com.acmerobotics.dashboard.config.Config;

@Config
public class FTCDashboardValues {

    public static double alpha = -1006;
    public static double beta = 255;

    public static double kp = 0;
    public static double ki = 0;
    public static double kd = 0;

    public double getBeta() {
        return beta;
    }
    public double getAlpha() {
        return alpha;
    }

    public static double getKp() {
        return kp;
    }

    public static double getKi() {
        return ki;
    }

    public static double getKd() {
        return kd;
    }
}
