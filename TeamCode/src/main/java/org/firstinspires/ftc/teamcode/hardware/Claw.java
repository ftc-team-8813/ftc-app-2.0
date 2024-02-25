package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcontroller.external.samples.SensorREVColorDistance;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class Claw {
    private Servo claw;
    private DistanceSensor sensor;

    public Claw(Servo claw, DistanceSensor claw_sensor){
        this.claw = claw;
        this.sensor = claw_sensor;
    }

    public void setClawPos(double pos) {
        claw.setPosition(pos);
    }

    public double getClawPos(){
        return claw.getPosition();
    }

    public double getSensorDistance(){
        return sensor.getDistance(DistanceUnit.MM);
    }

}
