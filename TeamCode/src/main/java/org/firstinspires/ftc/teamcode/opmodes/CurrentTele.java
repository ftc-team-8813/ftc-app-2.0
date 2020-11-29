package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.util.Scheduler;

import java.util.Arrays;

@TeleOp(name="CurrentTele")
public class CurrentTele extends OpMode {
    public Robot robot;
    public Scheduler scheduler;

    @Override
    public void init() {
        robot = new Robot(hardwareMap);
        scheduler = new Scheduler();
    }

    @Override
    public void loop() {
        // Drivetrain (Normal Drive)
        robot.drivetrain.telemove(0.5*(gamepad1.left_stick_y), 0.5*(gamepad1.right_stick_x));

        // Turret Lift
        robot.turret.liftGrab(gamepad2.left_stick_y, gamepad2.a);
        telemetry.addData("Lift Positions", Arrays.toString(robot.turret.getPotenPos()));

        boolean ring_in = false;
        if (ring_in){
            scheduler.addFutureTask(0.1, () -> robot.turret.setLift(0));
            scheduler.addFutureTask(0.5, () -> robot.turret.setGrabber(1));
            scheduler.addFutureTask(0.1, () -> robot.turret.setGrabber(2));
            scheduler.addFutureTask(0.2, () -> robot.turret.setLift(2));
            scheduler.addFutureTask(0.2, () -> robot.turret.setFinger(1));
            scheduler.addFutureTask(0.2, () -> robot.turret.setGrabber(2));
            scheduler.addFutureTask(0.2, () -> robot.turret.setFinger(2));
        }
    }
}
