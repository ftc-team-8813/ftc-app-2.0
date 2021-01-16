package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import org.firstinspires.ftc.teamcode.hardware.Robot;

public class OdoAuto extends OpMode {
    private Robot robot;
    private int id;

    @Override
    public void init() {
        robot = new Robot(hardwareMap);
    }

    @Override
    public void loop() {
        switch (id) {
            case 0:
                robot.drivetrain.automove(30);
                break;
            case 1:
                robot.drivetrain.automove(40);
                break;
            case 2:
                robot.drivetrain.automove(50);
                break;
        }

        id = robot.drivetrain.odoPIDUpdate();
    }
}
