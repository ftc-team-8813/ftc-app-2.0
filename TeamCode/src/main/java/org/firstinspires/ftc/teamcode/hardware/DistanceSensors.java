package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.hardware.ColorSensor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class DistanceSensors {

    public Rev2mDistanceSensor left;
    public Rev2mDistanceSensor right;
    public RevColorSensorV3 tape; //might need to change the object class

    public DistanceSensors(Rev2mDistanceSensor left, Rev2mDistanceSensor right, RevColorSensorV3 tape){
        this.right = right;
        this.left = left;
        this.tape = tape;
    }

    public double getLeftDistance(){
        return left.getDistance(DistanceUnit.MM);
    }

    public double getRightDistance(){
        return right.getDistance(DistanceUnit.MM);
    }

    public double getRed(){
        return tape.red();
    }

    public double getBlue(){
        return tape.blue();
    }

}
