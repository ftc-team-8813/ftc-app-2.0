package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DistanceSensor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class CapstoneDetector {
    DistanceSensor left_cap;
    DistanceSensor right_cap;

    public CapstoneDetector(DistanceSensor left_cap, DistanceSensor right_cap){
        this.left_cap = left_cap;
        this.right_cap = right_cap;
    }

    public int capHeight(){
        double[] distances = getDistances();
        if (50 < distances[0] && distances[0] < 70){
            return 1;
        } else if (50 < distances[1] && distances[1] < 70){
            return 3;
        } else {
            return 2;
        }
    }

    public double[] getDistances(){
        return new double[]{left_cap.getDistance(DistanceUnit.CM), right_cap.getDistance(DistanceUnit.CM)};
    }
}
