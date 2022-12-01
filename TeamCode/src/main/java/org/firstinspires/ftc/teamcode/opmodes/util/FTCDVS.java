package org.firstinspires.ftc.teamcode.opmodes.util;

import com.acmerobotics.dashboard.config.Config;

@Config
public class FTCDVS {

    public static double ALKP = 0.0346;
    public static double ALKI = 0.000307;
    public static double ALKD = 0.00091;
    public static double ALIS = 110;
    public static double AUKP = 0.0571;
    public static double AUKI = 0.00200;
    public static double AUKD = 0.0026;
    public static double AUIS = 100;
    public static double claw_increment = 0;
    public static double ALClip = 0.321;
    public static double AUClip = 0.303;

    public static double getALClip() {
        return ALClip;
    }

    public static double getAUClip() {
        return AUClip;
    }

    public static double getALIS() {
        return ALIS;
    }

    public static double getALKD() {
        return ALKD;
    }

    public static double getALKI() {
        return ALKI;
    }

    public static double getALKP() {
        return ALKP;
    }

    public static double getAUIS() {
        return AUIS;
    }

    public static double getAUKD() {
        return AUKD;
    }

    public static double getAUKI() {
        return AUKI;
    }

    public static double getAUKP() {
        return AUKP;
    }


    public static double getClaw_increment() {
        return claw_increment;
    }

}
