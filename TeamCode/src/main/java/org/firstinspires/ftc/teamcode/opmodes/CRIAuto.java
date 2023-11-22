package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.hardware.Drivetrain;
import org.firstinspires.ftc.teamcode.hardware.Robot;

import java.util.ArrayList;

@Autonomous(name = "!!Parking Auto")
public class CRIAuto extends LoggingOpMode {

    private Drivetrain drivetrain;
    private ElapsedTime timer;


    @Override
    public void init() {
        super.init();
        Robot robot = Robot.initialize(hardwareMap);
        drivetrain = robot.drivetrain;
        timer = new ElapsedTime(ElapsedTime.MILLIS_IN_NANO);

        timer.startTime();


    }

    @Override
    public void loop() {

        if(timer.time() == 20){
            drivetrain.move(0, 0.1, 0, 0);
            timer.reset();
        }
        if(timer.time() == 100){
            drivetrain.move(0.5,0, 0, 0);
        }


    }
}
