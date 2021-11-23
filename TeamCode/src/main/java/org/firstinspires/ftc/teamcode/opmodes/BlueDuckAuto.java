package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.opmodes.auto.AutonomousTemplate;
import org.firstinspires.ftc.teamcode.util.event.EventBus;

// we going to use the event bus system for this so that everything can be done on one thread
@Autonomous(name="Blue Duck Auto")
public class BlueDuckAuto extends LoggingOpMode
{
    private Robot robot;
    private AutonomousTemplate auto;
    private int id;

    @Override
    public void init() {
        super.init();
        this.robot = Robot.initialize(hardwareMap, "Blue Duck Auto");
        this.auto = new AutonomousTemplate(
                "Blue Duck Auto",
                this.robot,
                hardwareMap,
                new ControllerMap(gamepad1, gamepad2, new EventBus()),
                telemetry
        );
        auto.init_camera();
        auto.init_server();
    }

    @Override
    public void start() {
        auto.check_image();
    }

    @Override
    public void loop() {
        // DON'T FORGET BREAKS
        // NEXT CASE SHOULD BE +1
        switch (id){
            case 0:
                robot.drivetrain.teleMove(0.22, 0.3, 0);
                auto.set_timer(2);
                break;
        }

        id = auto.update();
    }

    @Override
    public void stop() {
        auto.stop();
    }
}
