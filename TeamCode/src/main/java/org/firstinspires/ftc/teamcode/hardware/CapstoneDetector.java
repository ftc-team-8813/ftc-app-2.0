package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DistanceSensor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import java.util.Arrays;

public class CapstoneDetector {
    private final DistanceSensor left;
    private final DistanceSensor right;
    private final double[] rightSensArray;
    private final double[] leftSensArray;
    private int loopCycleNum;


    public CapstoneDetector(DistanceSensor left, DistanceSensor right){
        this.left = left;
        this.right = right;
        rightSensArray = new double[300];
        leftSensArray = new double[300];
        loopCycleNum = 0;
    }

    public int final_location(){
        double leftAverage = Arrays.stream(leftSensArray).sum()/leftSensArray.length;
        double rightAverage = Arrays.stream(rightSensArray).sum()/rightSensArray.length;
        int location;

        if(Math.abs((leftAverage-rightAverage)) > 200)
            if(leftAverage > rightAverage){
                location = 2;
            }else{
                location = 3;
            }
        else{
            location = 1;
        }
        return location;
    }

    public boolean detect_capstone(){
        leftSensArray[loopCycleNum] = left.getDistance(DistanceUnit.CM);
        rightSensArray[loopCycleNum] = right.getDistance(DistanceUnit.CM);

        if(loopCycleNum < 300){
            loopCycleNum = 0;
            return true;
        }
        loopCycleNum++;
        return false;
    }
}