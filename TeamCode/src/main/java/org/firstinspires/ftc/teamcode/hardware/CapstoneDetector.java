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

    public double[] getDistances(){
        return new double[]{left_cap.getDistance(DistanceUnit.CM), right_cap.getDistance(DistanceUnit.CM)};
    }
}
