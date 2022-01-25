package org.firstinspires.ftc.teamcode.opmodes.test;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorRangeSensor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.opmodes.LoggingOpMode;
import org.firstinspires.ftc.teamcode.util.Persistent;

@TeleOp(name="Distance Sensor Test")
public class DistanceSensorTest extends LoggingOpMode {
    private Robot robot;
    private ColorRangeSensor color_dist;

    @Override
    public void init() {
        super.init();
        robot = Robot.initialize(hardwareMap, "REV v3 color distance sensor test program", 0);
    }

    @Override
    public void init_loop() { super.init_loop(); }

    @Override
    public void start()
    {
        Persistent.clear();
    }

    @Override
    public void loop() {
        double distance = color_dist.getDistance(DistanceUnit.MM);
        telemetry.addData("Distance", distance);
    }
    @Override
    public void stop() { super.stop(); }
}



