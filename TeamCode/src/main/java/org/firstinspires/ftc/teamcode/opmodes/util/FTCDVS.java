package org.firstinspires.ftc.teamcode.opmodes.util;

import com.acmerobotics.dashboard.config.Config;

@Config
public class FTCDVS {

    public static double KPLift = 0.02;
    public static double KPArm = 0.0095;
    public static double KPHoriz = 0.01;
    public static double KFLift = 0.015;
    public static double KFArm = 0;
    public static double liftAccel = 0.4;
    public static double DepositTransfer = 0.15;
    public static double DepositLow = 0.37;
    public static double DepositMid = 0.31;
    public static double DepositHigh = 0.33;
    public static double DepositHighFast = 0.38;
    public static double ArmCompletePosition = -110;
    public static double ArmMidPosition = -35;

    public static double getKPLift() {
        return KPLift;
    }

    public static double getKPArm() {
        return KPArm;
    }

    public static double getKPHoriz() {
        return KPHoriz;
    }

    public static double getKFLift(){
        return KFLift;
    }

    public static double getKFArm(){
        return KFArm;
    }

    public static double getLiftAccel(){
        return liftAccel;
    }

    public static double getDepositTransfer(){
        return DepositTransfer;
    }

    public static double getDepositLow(){
        return DepositLow;
    }

    public static double getDepositMid(){
        return DepositMid;
    }

    public static double getDepositHigh(){
        return DepositHigh;
    }

    public static double getArmDownPosition(){
        return ArmCompletePosition;
    }

    public static double getArmMidPosition(){
        return ArmMidPosition;
    }

    public static double getDepositHighFast(){
        return DepositHighFast;
    }
}
