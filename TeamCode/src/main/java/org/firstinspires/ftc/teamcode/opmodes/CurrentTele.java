package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.util.Scheduler;
import org.firstinspires.ftc.teamcode.util.event.EventBus;
import org.firstinspires.ftc.teamcode.util.event.TimerEvent;

import java.util.Arrays;

@TeleOp(name="CurrentTele")
public class CurrentTele extends OpMode {
    private Robot robot;
    private EventBus eventBus;
    private Scheduler taskScheduler;

    @Override
    public void init() {
        robot = new Robot(hardwareMap);
        eventBus = new EventBus();
        taskScheduler = new Scheduler(eventBus);
        Scheduler.Timer timer = taskScheduler.addRepeatingTrigger(0.5, "Example timer");
        eventBus.subscribe(TimerEvent.class, (ev, bus, sub) -> telemetry.update(), "Telemetry update trigger", timer.eventChannel);
    }

    @Override
    public void loop() {
        // Drivetrain (Normal Drive)
        robot.drivetrain.telemove(0.5*(gamepad1.left_stick_y), 0.5*(gamepad1.right_stick_x));
        robot.turret.liftGrab(gamepad2.left_stick_y, gamepad2.a);
        telemetry.addData("Lift Positions", Arrays.toString(robot.turret.getPotenPos()));
        taskScheduler.loop();
        eventBus.update();
    }
}
