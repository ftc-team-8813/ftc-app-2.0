package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DigitalChannel;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class DistanceSensors {

    private DigitalChannel polulu1;
    private DigitalChannel polulu2;

    public DistanceSensors(DigitalChannel polulu1, DigitalChannel polulu2){
        this.polulu1 = polulu1;
        this.polulu2 = polulu2;
    }

    public boolean getLeft(){
        return polulu1.getState();
    }

    public boolean getRight(){
        return polulu2.getState();
    }

    }

}
