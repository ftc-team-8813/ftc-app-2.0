package org.firstinspires.ftc.teamcode.opmodes.test;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorRangeSensor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.DistanceSensor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.opmodes.LoggingOpMode;
import org.firstinspires.ftc.teamcode.util.Persistent;

@TeleOp(name="Sensor Test")
public class SensorTest extends LoggingOpMode {
    DistanceSensor cap_left;
    DistanceSensor cap_right;
    DigitalChannel lift_limit;

    @Override
    public void init() {
        super.init();
        cap_left = hardwareMap.get(DistanceSensor.class, "cap left");
        cap_right = hardwareMap.get(DistanceSensor.class, "cap right");
        lift_limit = hardwareMap.get(DigitalChannel.class, "lift limit");

    }

    @Override
    public void loop() {
        double left_distance = cap_left.getDistance(DistanceUnit.CM);
        double right_distance = cap_right.getDistance(DistanceUnit.CM);
        boolean lift_limit_state = !lift_limit.getState();

        telemetry.addData("Left Cap Distance: ", left_distance);
        telemetry.addData("Right Cap Distance: ", right_distance);
        telemetry.addData("Lift Limit Pressed: ", lift_limit_state);
        telemetry.update();
    }
}



