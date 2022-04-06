package org.firstinspires.ftc.teamcode.util;

import com.qualcomm.robotcore.util.ElapsedTime;

public class LoopTimer {
    public static ElapsedTime loop_timer = new ElapsedTime();

    public static double getLoopTime(){
        return loop_timer.seconds();
    }

    public static void resetTimer(){
        loop_timer.reset();
    }
}
