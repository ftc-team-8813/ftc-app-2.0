package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DistanceSensor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.util.Logger;

import java.util.Arrays;

public class CapstoneDetector {
    private final DistanceSensor left;
    private final DistanceSensor right;
    private final double[] rightSensArray;
    private final double[] leftSensArray;
    private int loopCycleNum;
    private String op_mode;
    private Logger log = new Logger("Capstone");


    public CapstoneDetector(DistanceSensor left, DistanceSensor right){
        this.left = left;
        this.right = right;
        rightSensArray = new double[200];
        leftSensArray = new double[200];
        loopCycleNum = 0;
    }

    public void setOpMode(String op_mode){
        this.op_mode = op_mode;
    }

    public int final_location(){
        double leftAverage = Arrays.stream(leftSensArray).sum()/leftSensArray.length;
        double rightAverage = Arrays.stream(rightSensArray).sum()/rightSensArray.length;
        int location = 0;
        log.i("Left Cap Average: %f", leftAverage);
        log.i("Right Cap Average: %f", rightAverage);


        if (op_mode.contains("Red")) {
            if (Math.abs((leftAverage - rightAverage)) > 250)
                if (leftAverage > rightAverage) {
                    location = 2;
                } else {
                    location = 1;
                }
            else {
                location = 3;
            }
        } else if (op_mode.contains("Blue")) {
            if (Math.abs((leftAverage - rightAverage)) > 250)
                if (leftAverage > rightAverage) {
                    location = 3;
                } else {
                    location = 2;
                }
            else {
                location = 1;
            }
        }

        return location;
    }

    public boolean detect_capstone(){
        if(loopCycleNum >= 200){
            loopCycleNum = 0;
            return true;
        }

        leftSensArray[loopCycleNum] = getLeftDistance();
        rightSensArray[loopCycleNum] = getRightDistance();

        loopCycleNum++;
        return false;
    }

    public double getLeftDistance(){
        return left.getDistance(DistanceUnit.CM);
    }

    public double getRightDistance(){
        return right.getDistance(DistanceUnit.CM);
    }
}