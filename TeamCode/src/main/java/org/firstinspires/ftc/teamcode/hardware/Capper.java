package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.CRServoImplEx;
import com.qualcomm.robotcore.hardware.PwmControl;
import com.qualcomm.robotcore.hardware.ServoImplEx;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

public class Capper {
    private final CRServoImplEx tape;
    private final ServoImplEx tape_tilt;
    private final ServoImplEx tape_swivel;
    public double extension = 0.0;
    private ElapsedTime loop_timer;
    private double loop_time = 1.0;
    private final double swivel_sensitivity = 0.01;
    private final double tilt_sensitivity = 0.03;

    public Capper(CRServoImplEx tape, ServoImplEx tape_tilt, ServoImplEx tape_swivel) {
        this.tape = tape;
        this.tape_tilt = tape_tilt;
        this.tape_swivel = tape_swivel;
        loop_timer = new ElapsedTime();

        tape.setPwmRange(new PwmControl.PwmRange(500, 2500));
        tape_tilt.setPwmRange(new PwmControl.PwmRange(500, 2500));
        tape_swivel.setPwmRange(new PwmControl.PwmRange(500, 2500));
        tape_tilt.scaleRange(0.35, 1);
    }

    public void init() {
        tape_tilt.setPosition(0.7);
        tape_swivel.setPosition(0.5);
    }

    public void extend(double power) {
        loop_time = loop_timer.seconds();
        loop_timer.reset();

        tape.setPower(power);
        extension += -(power * loop_time); //no idea why this is negative but it is
    }

    public void go_to(double tilt, double swivel) {
        tape_tilt.setPosition(tilt);
        tape_swivel.setPosition(swivel);
    }

    public void adjust(double tilt, double swivel) {
        tape_tilt.setPosition(Range.clip(tape_tilt.getPosition() - (tilt * tilt_sensitivity / Math.max(extension, 1.0)), 0.0, 1.0));
        tape_swivel.setPosition(Range.clip(tape_swivel.getPosition() + (swivel * swivel_sensitivity / Math.max(extension, 1.0)), 0.0, 1.0));
    }
}
