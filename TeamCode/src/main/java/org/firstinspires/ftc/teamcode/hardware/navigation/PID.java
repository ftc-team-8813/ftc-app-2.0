package org.firstinspires.ftc.teamcode.hardware.navigation;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.ArrayList;

public class PID {

//    private double position;
//    private double target_position;
//    private double error;

//    private double reference;
    private double lastReference = 0;
    private double integralSum = 0;

    private double lastError = 0;

    private double maxIntegralSum; // need find

    double a; // = 0.8; // a can be anything from 0 < a < 1 and need to find
    double previousFilterEstimate = 0;
    double currentFilterEstimate = 0;

    private ElapsedTime timer = new ElapsedTime();

    private double Kp;
    private double Ki;
    private double Kd;
    private double Kf;


    public PID (double kp, double ki, double kd, double kf, double mxis, double aVal) {
        Kp = kp;
        Ki = ki;
        Kd = kd;
        Kf = kf;
        maxIntegralSum = mxis;
        a = aVal;
    }


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

        double out = (Kp * error) + (Ki * integralSum) + (Kd * derivative) + (Kf * feedforward);

        lastError = error;

        lastReference = reference;

        timer.reset();
        return out;
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
