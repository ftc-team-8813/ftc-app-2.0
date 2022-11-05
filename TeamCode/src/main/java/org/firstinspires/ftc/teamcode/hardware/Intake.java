package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.hardware.broadcom.BroadcomColorSensor;
import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.lang.reflect.Array;
import java.util.Arrays;

public class Intake {

    private final Servo claw;
    private final DistanceSensor claw_sensor;
    private boolean intaken = false;

    public Intake(Servo claw, DistanceSensor claw_sensor){
        this.claw = claw;
        this.claw_sensor = claw_sensor;
    }
    public void setClaw(double pos) {
        claw.setPosition(pos);
    }

    public double getDistance() {
        return claw_sensor.getDistance(DistanceUnit.MM);
    }

    public double getClawPosition() {
        return claw.getPosition();
    }

}
