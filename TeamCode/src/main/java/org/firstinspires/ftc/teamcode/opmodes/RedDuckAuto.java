package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.opmodes.auto.AutonomousTemplate;
import org.firstinspires.ftc.teamcode.util.Status;
import org.firstinspires.ftc.teamcode.util.event.EventBus;

@Autonomous(name="Red Duck Auto", group="Reds")
public class RedDuckAuto extends LoggingOpMode
{
    private Robot robot;
    private AutonomousTemplate auto;
    private String name = "Red Duck Auto";
    private int id;

    @Override
    public void init() {
        super.init();
        this.robot = Robot.initialize(hardwareMap, name);
        this.auto = new AutonomousTemplate(
                name,
                this.robot,
                hardwareMap,
                new ControllerMap(gamepad1, gamepad2, new EventBus()),
                telemetry
        );
        auto.init_camera();
    }

    @Override
    public void start() {
        auto.timer.reset();
    }

    @Override
    public void loop() {
        switch (id){
            case 0:
                auto.check_image();
                auto.set_timer(2);
                break;
            case 1:
                robot.lift.extend(Status.STAGES.get("low"), true);
                break;
            case 2:
                robot.lift.rotate(Status.EXTENSIONS.get("out"));
                auto.set_timer(0.5);
                break;
            case 3:
                robot.lift.extend(Status.STAGES.get("high"), true);
                break;
        }

        id = auto.update();
    }

    @Override
    public void stop() {
        auto.stop();
    }
}
