package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.opmodes.auto.AutonomousTemplate;
import org.firstinspires.ftc.teamcode.util.event.EventBus;

// we going to use the event bus system for this so that everything can be done on one thread
@Autonomous(name="Navigation Test")
public class NavigationTest extends LoggingOpMode
{
    private Robot robot;
    private AutonomousTemplate auto;
    private String name = "Navigation Test";
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
        auto.init_server();
        auto.init_odometry(0, 0, 0);
    }

    @Override
    public void start() {
        robot.odometry.resetEncoders();
    }

    @Override
    public void loop() {
        auto.check_image();
        // DON'T FORGET BREAKS
        // NEXT CASE SHOULD BE +1
//        switch (id){
//            case 0:
//                robot.drivetrain.goToPosition(0, 30, 0.03);
//                break;
//        }

        id = auto.update();
    }

    @Override
    public void stop() {
        auto.stop();
    }
}
