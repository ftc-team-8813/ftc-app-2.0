package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.hardware.Drivetrain;
import org.firstinspires.ftc.teamcode.hardware.Lift;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.util.Logger;
import org.firstinspires.ftc.teamcode.util.LoopTimer;
import org.firstinspires.ftc.teamcode.util.Persistent;
import org.firstinspires.ftc.teamcode.util.Storage;

@Autonomous(name = "Red Auto")
public class RedAuto extends LoggingOpMode{

    private Drivetrain drivetrain;
    private Lift lift;

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void init_loop() {
        super.init_loop();
        Robot robot = Robot.initialize(hardwareMap);
        drivetrain = robot.drivetrain;
        lift = robot.lift;

        telemetry.update();
    }

    @Override
    public void start() {
        super.start();
        drivetrain.resetEncoders();

    }

    @Override
    public void loop() {

        telemetry.addData("Loop Time: ", LoopTimer.getLoopTime());
        telemetry.update();

        LoopTimer.resetTimer();
    }

    @Override
    public void stop() {
        super.stop();
    }

}
