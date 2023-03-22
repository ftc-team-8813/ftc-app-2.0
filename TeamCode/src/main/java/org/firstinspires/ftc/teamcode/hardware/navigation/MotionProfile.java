package org.firstinspires.ftc.teamcode.hardware.navigation;

import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

public class MotionProfile {
    private double min;
    private double max;

    private double output = 1.0;

    private double error;

    private ElapsedTime rise_timer;

    private double rise_slope = 1;
    private double fall_slope = 1;

    private double old_id;

    public MotionProfile(double rise_slope, double fall_slope, double min, double max) {
        rise_timer = new ElapsedTime();
        this.rise_slope = rise_slope;
        this.fall_slope = fall_slope;
        this.min = min;
        this.max = max;
    }

    public void setBounds(double min, double max) {
        this.min = min;
        this.max = max;
    }

    public void setSlopes(double rise_slope, double fall_slope) {
        this.rise_slope = rise_slope;
        this.fall_slope = fall_slope;
    }

    public void updateMotionProfile(double id, boolean rise, boolean fall) {
        output = 2.0;

        if (old_id != id) {
            rise_timer.reset();
            old_id = id;
        }

        if (rise) {
            output = Math.min(rise_timer.seconds() * rise_slope, output);
        }
        if (fall) {
            output = Math.min(error * fall_slope, output);
        }

        if (!rise && !fall) {
            output = 0.7;
        }

        output = Range.clip(output, min, max);
    }

    public double getProfiledPower(double error, double power, double feedforward) {
        this.error = Math.abs(error);
        return Range.clip(power, Math.max(-output + feedforward, -1), Math.min(output + feedforward, 1));
    }
}
