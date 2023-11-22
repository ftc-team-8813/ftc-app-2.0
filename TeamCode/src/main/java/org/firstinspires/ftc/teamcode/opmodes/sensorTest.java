package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.hardware.DistanceSensor;

import org.firstinspires.ftc.robotcontroller.external.samples.SensorREVColorDistance;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.hardware.Sensor;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.opmodes.teleop.ControlMgr;
import org.firstinspires.ftc.teamcode.opmodes.teleop.ControlModule;

public class sensorTest extends ControlModule{

    private Sensor sensors;

    public sensorTest(String name) {
        super(name);
    }

    @Override
    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager) {
        this.sensors = robot.sensors;

    }

    @Override
    public void update(Telemetry telemetry) {
//        telemetry.addData("Sensor 1", sensors.getDistance()[0]);
//        telemetry.addData("Sensor 2", sensors.getDistance()[1]);
        telemetry.addData("Sensor 3 Red Color", sensors.getRed());

    }
}
