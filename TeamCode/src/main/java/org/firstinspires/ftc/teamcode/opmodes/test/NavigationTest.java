package org.firstinspires.ftc.teamcode.opmodes.test;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.opmodes.LoggingOpMode;

@Autonomous(name = "Navigation Test")
public class NavigationTest extends LoggingOpMode {
    Robot robot;

    public void init(){
        robot = Robot.initialize(hardwareMap, "Navigation Test");

        robot.odometry.podsDown();
    }

    @Override
    public void loop() {
        robot.drivetrain.goToPosition(40, -40, 0, 0.4);
        robot.drivetrain.updatePosition();

        double[] odo_data = robot.odometry.getOdoData();
        telemetry.addData("Y: ", odo_data[0]);
        telemetry.addData("X: ", odo_data[1]);
        telemetry.addData("Heading: ", odo_data[2]);

        double[] delta_positions = robot.drivetrain.getPositionDeltas();
        telemetry.addData("Forward Power: ", delta_positions[0]);
        telemetry.addData("Strafe Power: ", delta_positions[1]);
        telemetry.addData("Turn Power: ", delta_positions[2]);

        robot.odometry.update();
        telemetry.update();
    }
}
