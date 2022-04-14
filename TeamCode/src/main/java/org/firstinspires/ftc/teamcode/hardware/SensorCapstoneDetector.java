package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DistanceSensor;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import java.util.Arrays;

public class SensorCapstoneDetector {

    private int location = 0;
    private DistanceSensor left;
    private DistanceSensor right;
    private double[] rightSensArray;
    private double[] leftSensArray;
    private int loopCycleNum;


    public SensorCapstoneDetector(DistanceSensor left, DistanceSensor right){
        this.left = left;
        this.right = right;
        rightSensArray = new double[300];
        leftSensArray = new double[300];
        loopCycleNum = 0;
    }

    public int finalLocation(){
        double leftAverage = Arrays.stream(leftSensArray).sum()/leftSensArray.length;
        double rightAverage = Arrays.stream(rightSensArray).sum()/rightSensArray.length;

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

    public void capstoneDetection(Telemetry telemetry){
        if(loopCycleNum < 300){
            leftSensArray[loopCycleNum] = left.getDistance(DistanceUnit.CM);
            rightSensArray[loopCycleNum] = right.getDistance(DistanceUnit.CM);
        }

        if(loopCycleNum > 300){
            finalLocation();
            loopCycleNum = 0;
        }
        loopCycleNum++; 
    }
}