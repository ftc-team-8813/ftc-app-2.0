package org.firstinspires.ftc.teamcode.opmodes.auto;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.checkerframework.checker.units.qual.A;
import org.firstinspires.ftc.teamcode.hardware.AutoDrive;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.opmodes.LoggingOpMode;
import org.firstinspires.ftc.teamcode.opmodes.auto.AutonomousTemplate;
import org.firstinspires.ftc.teamcode.opmodes.teleop.ControlMgr;
import org.firstinspires.ftc.teamcode.util.Status;
import org.firstinspires.ftc.teamcode.util.event.EventBus;

@Autonomous(name="Auto Drive Test", group="Blues")
public class Encoder_Auto_test extends LoggingOpMode
{
    private Robot robot;
    private AutonomousTemplate auto;
    private AutoDrive autoDrive;
    private final String name = "Auto Drive Test";

    @Override
    public void init() {
        super.init();
        this.robot = Robot.initialize(hardwareMap, name, 0);
        this.auto = new AutonomousTemplate(
                name,
                this.robot,
                hardwareMap,
                new ControllerMap(gamepad1, gamepad2, new EventBus()),
                telemetry
        );
        auto.init_camera();
        auto.init_lift();
    }

    @Override
    public void start() {
        auto.timer.reset();
    }

    @Override
    public void loop() {

        autoDrive.moveToPosition(0.0, 32.0,0.0, 0.0, true);
        autoDrive.update(telemetry);
        robot.eventBus.update();
        robot.scheduler.loop();
    }

    @Override
    public void stop() {
        auto.stop();
    }
}