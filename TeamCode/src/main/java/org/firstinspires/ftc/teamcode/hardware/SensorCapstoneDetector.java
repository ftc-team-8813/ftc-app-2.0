package org.firstinspires.ftc.teamcode.hardware;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class SensorCapstoneDetector {

    private int location = 0;
    private DistanceSensor middle;
    private DistanceSensor right;

    public SensorCapstoneDetector(DistanceSensor middle, DistanceSensor right){
        this.middle = middle;
        this.right = right;
    }

    public int getLocation() {
        return location;
    }

    public void setLocation(int location) {
        this.location = location;
    }

    public void blueCapstoneDetection(){
        if((right.getDistance(DistanceUnit.CM) >= 50) && (right.getDistance(DistanceUnit.CM) <= 70)){
            setLocation(3);
        }else if((middle.getDistance(DistanceUnit.CM) >= 50) && (middle.getDistance(DistanceUnit.CM) <= 70)){
            setLocation(2);
        }else{
            setLocation(1);
        }
    }

    public void redCapstoneDetection(){
        if((right.getDistance(DistanceUnit.CM) >= 50) && (right.getDistance(DistanceUnit.CM) <= 70)){
            setLocation(2);
        }else if((middle.getDistance(DistanceUnit.CM) >= 50) && (middle.getDistance(DistanceUnit.CM) <= 70)){
            setLocation(1);
        }else{
            setLocation(3);
        }
    }

    public double getRightDistance(){
        return right.getDistance(DistanceUnit.CM);
    }

    public double getMiddleDistance(){
        return middle.getDistance(DistanceUnit.CM);
    }
}

