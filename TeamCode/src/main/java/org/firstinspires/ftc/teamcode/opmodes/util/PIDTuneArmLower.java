package org.firstinspires.ftc.teamcode.opmodes.util;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.util.ElapsedTime;

@Deprecated
@Config
public class PIDTuneArmLower {

//    private double position;
//    private double target_position;
//    private double error;

    //    private double reference;

    private double lastReference = 0;
    private double integralSum = 0;

    private double lastError = 0;

    public static double clip = 0.3;

    public static double maxIntegralSum = 100; // need find

    public static double a = 0.8; // = 0.8; // a can be anything from 0 < a < 1 and need to find
    double previousFilterEstimate = 0;
    double currentFilterEstimate = 0;

    private ElapsedTime timer = new ElapsedTime();
    private ElapsedTime trapezoid = new ElapsedTime();

    public static double Kp = 0.025;
    public static double Ki = 0.0;
    public static double Kd = 0.001;
    public static double Kf = 0.2;

    public static double a_max = 1;

    public double getOutPut(double reference, double cur, double feedforward) {
        double error = reference - cur;

        double errorChange = (error - lastError);

        currentFilterEstimate = (a * previousFilterEstimate) + (1-a) * errorChange;
        previousFilterEstimate = currentFilterEstimate;

        double derivative = currentFilterEstimate / timer.seconds();

        integralSum = integralSum + (error * timer.seconds());


        if (integralSum > maxIntegralSum) {
            integralSum = maxIntegralSum;
        }

        if (integralSum < -maxIntegralSum) {
            integralSum = -maxIntegralSum;
        }

        if (reference != lastReference) {
            integralSum = 0;
        }

//        double out_target = Range.clip((Kp * error) + (Ki * integralSum) + (Kd * derivative), -clip,clip) + (feedforward * Kf);
        double out = (Kp * error) + (Ki * integralSum) + (Kd * derivative) + (Kf * feedforward);

        lastError = error;

        lastReference = reference;

        timer.reset();

//        double out = Math.min(Math.abs(out_target), Math.min(Math.abs(prev_out + (a_max * timer.seconds())), Math.sqrt(2 * a_max * Math.abs(error)))) * Math.signum(out_target);
//
//        prev_out = out;
//        if ((Math.abs(error) > 10) && ((trapezoid.seconds() * a_max) > 1)) {
//            trapezoid.reset();
//        }
//        double out = out_target * Math.min(trapezoid.seconds() * a_max, 1);


        return out;
    }

    public void startMotionProfile() {
        trapezoid.reset();
    }

//    public void update(double pos, double target_pos) {
//        position = pos;
//        target_position = target_pos;
//    }

    //
//    double reference;
//    double lastReference = reference;
//    double integralSum = 0;
//
//    double lastError = 0;
//
//    double maxIntegralSum;
//
//    a = 0.8; // a can be anything from 0 < a < 1
//    double previousFilterEstimate = 0;
//    double currentFilterEstimate = 0;
//
//    ElapsedTime timer = new ElapsedTime();
//
//    if (setPointIsNotReached) {
//
//
//        // obtain the encoder position
//        encoderPosition = armMotor.getPosition();
//        // calculate the error
//        error = reference - encoderPosition;
//
//        errorChange = (error - lastError)
//
//        // filter out hight frequency noise to increase derivative performance
//        currentFilterEstimate = (a * previousFilterEstimate) + (1-a) * errorChange;
//        previousFilterEstimate = currentFilterEstimate;
//
//        // rate of change of the error
//        derivative = currentFilterEstimate / timer.seconds();
//
//        // sum of all error over time
//        integralSum = integralSum + (error * timer.seconds());
//
//
//        // max out integral sum
//        if (integralSum > maxIntegralSum) {
//            integralSum = maxIntegralSum;
//        }
//
//        if (integralSum < -maxIntegralSum) {
//            integralSum = -maxIntegralSum;
//        }
//
//        // reset integral sum upon setpoint changes
//        if (reference != lastReference) {
//            integralSum = 0;
//        }
//        double out = (Kp * error) + (Ki * integralSum) + (Kd * derivative);
//
//        armMotor.setPower(out);
//        lastError = error;
//
//        lastReference = reference;
//
//        // reset the timer for next time
//        timer.reset();
//
//    }
}
