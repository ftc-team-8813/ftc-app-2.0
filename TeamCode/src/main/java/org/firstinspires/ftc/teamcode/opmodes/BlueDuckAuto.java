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
    private String name = "Blue Duck Auto";
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
        auto.init_odometry(0, 0, 0);
//        auto.init_server();
    }

    @Override
    public void start() {
        robot.odometry.resetEncoders();
    }

    @Override
    public void loop() {
        // DON'T FORGET BREAKS
        // NEXT CASE SHOULD BE +1
        switch (id){
            case -1:
                auto.check_image();
                break;
            case 0:
                robot.drivetrain.goToPosition(-50, -10, 0.0001);
                break;
            case 1:
                robot.drivetrain.goToPosition(0, 0, 0.0001);
                break;
            case 2:
                robot.drivetrain.stop();
                break;
        }

        id = auto.update();
    }

    @Override
    public void stop() {
        auto.stop();
    }
}
