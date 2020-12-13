package org.firstinspires.ftc.teamcode.opmodes.util;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.hardware.Robot;

@TeleOp(name="LightDetector")
public class LightDetector extends OpMode {
    private Robot robot;

    @Override
    public void init() {
        robot = new Robot(hardwareMap);
    }

    @Override
    public void loop() {
        int r = robot.ring_detector.red();
        int g = robot.ring_detector.green();
        int b = robot.ring_detector.blue();
        int a = robot.ring_detector.alpha();
        telemetry.addData("RGBA", "%3d, %3d, %3d, %3d", r, g, b, a);
        telemetry.addData("Brightness", Math.max(r, Math.max(g, b)));
        telemetry.update();
    }
}
