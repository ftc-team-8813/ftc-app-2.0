package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DistanceSensor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class Sensor {
    private ColorSensor sens;

//    public Sensor(Rev2mDistanceSensor sens1, Rev2mDistanceSensor sens2){
//        this.sens1 = sens1;
//        this.sens2 = sens2;
////        this.sens3 = sens3;
//
//    }

    public Sensor(ColorSensor sensor){
        this.sens = sensor;

    }
//    public double[] getDistance(){
////        double[] getDistance={sens1.getDistance(DistanceUnit.MM),sens2.getDistance(DistanceUnit.MM),sens3.red()};  //initializing array
//        double[] getDistance={sens1.getDistance(DistanceUnit.MM),sens2.getDistance(DistanceUnit.MM)};  //initializing array
//        return getDistance;
//    }

    public double getRed(){
        return sens.red();
    }

}
