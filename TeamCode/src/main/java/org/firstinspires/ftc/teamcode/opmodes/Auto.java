package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.hardware.Drivetrain;
import org.firstinspires.ftc.teamcode.hardware.Duck;
import org.firstinspires.ftc.teamcode.hardware.Intake;
import org.firstinspires.ftc.teamcode.hardware.Lift;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.util.Status;

@Autonomous(name = "Auto")
public class Auto extends LoggingOpMode{

    private Robot robot;
    private Drivetrain drivetrain;
    private int id = 0;

    private ElapsedTime timer;

    @Override
    public void init() {
        super.init();
        robot = Robot.initialize(hardwareMap);
        drivetrain = robot.drivetrain;
    }

    @Override
    public void loop() {
        switch (id) {
            case 0:
                drivetrain.autoMove(2000, 0.45);
            case 1:
                drivetrain.autoMove(2000, 0.45);
        }

        if (drivetrain.ifReached()){
            id += 1;
        }
        drivetrain.update();

        telemetry.addData("Id: ", id);
        telemetry.update();
    }
}
