package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.hardware.ColorSensor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class DistanceSensors {

    public Rev2mDistanceSensor l1;
//    public Rev2mDistanceSensor l2;
    public Rev2mDistanceSensor r1;
//    public Rev2mDistanceSensor r2;
    public RevColorSensorV3 colorSensor; //might need to change the object class

    public DistanceSensors(Rev2mDistanceSensor r1){
//        this.l1 = l1;
//        this.l2 = l2;
        this.r1 = r1;
//        this.r2 = r2;
//        this.colorSensor = colorSensor;
    }

    public double getLeftDistance(){
        return l1.getDistance(DistanceUnit.MM);
    }

    public double getRightDistance(){
        return r1.getDistance(DistanceUnit.MM);
    }

//    public double getRed(){
//        return colorSensor.red();
//    }
//
//    public double getBlue(){
//        return colorSensor.blue();
//    }

}
