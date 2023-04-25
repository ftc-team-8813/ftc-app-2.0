package org.firstinspires.ftc.teamcode.opmodes.test;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.VoltageSensor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.opmodes.LoggingOpMode;

@TeleOp(name="Distance Test")
public class DistanceTest extends LoggingOpMode {

    private DistanceSensor claw_sensor;

    @Override
    public void init() {
        claw_sensor = hardwareMap.get(DistanceSensor.class, "claw sensor");
        super.init();
    }

    @Override
    public void loop() {
        telemetry.addData("Distance",claw_sensor.getDistance(DistanceUnit.MM));
        telemetry.update();
    }
}