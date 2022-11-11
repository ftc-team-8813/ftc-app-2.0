package org.firstinspires.ftc.teamcode.opmodes.util;

import com.acmerobotics.dashboard.config.Config;

@Config
public class FTCDashboardValues {

    public static double ki = 0.005;
    public static double kp = 0.053;
    public static double mxis = 32;

    public static double getKi() {
        return ki;
    }

    public static double getKp() {
        return kp;
    }

    public static double getMxis() {
        return mxis;
    }
}
