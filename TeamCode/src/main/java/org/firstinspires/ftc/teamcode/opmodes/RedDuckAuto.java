package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.opmodes.auto.AutonomousTemplate;
import org.firstinspires.ftc.teamcode.util.Status;
import org.firstinspires.ftc.teamcode.util.event.EventBus;

// we going to use the event bus system for this so that everything can be done on one thread
@Autonomous(name="Red Duck Auto")
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
//        auto.init_server();
//        auto.init_odometry(0, 0, 0);
    }

    @Override
    public void start() {
        robot.odometry.resetEncoders();
        auto.timer.reset();
    }

    @Override
    public void loop() {
        //robot.intake.intake();
        //robot.duck.spin(+ or - 1);
        // DON'T FORGET BREAKS
        // NEXT CASE SHOULD BE +1
        // REMEMBER TIME BASED FOR ALL BUT LIFT
        switch (id){
//            case 0:
//                auto.check_image();
//                auto.set_timer(2);
//                break;
            case 0:
                // Sets powers and moves for set time
                auto.logger.i("First Move");
                robot.drivetrain.teleMove(0.3, 0.25, 0);
                auto.set_timer(1.7);
                break;
            case 1:
                robot.drivetrain.teleMove(-.01,-.255,0);
                auto.set_timer(1);
                break;
            case 2:
                robot.drivetrain.teleMove(0,0,0);
                robot.duck.spin(-1);
                auto.set_timer(4);
                break;
            case 3:
                robot.duck.spin(0);
                robot.drivetrain.teleMove(-0.232,0.232,0);
                auto.set_timer(2.5);
                break;
            case 4:
                robot.drivetrain.teleMove(0,0,0);
                robot.drivetrain.teleMove(0.05,0.05,0);
                auto.set_timer(1);
                break;
            case 5:
                robot.drivetrain.teleMove(0,0,0);
                // Lift heights based on detected height
                switch (auto.shipping_height) {
                    case 1:
                        robot.lift.raise(Status.STAGES.get("low"));
                        break;
                    case 2:
                        robot.lift.raise(Status.STAGES.get("mid"));
                        break;
                    case 3:
                        robot.lift.raise(Status.STAGES.get("high"));
                        break;
                }
                break;
            case 6:
                // Moves to right position (can be adjusted to different positions in Status.java) within 2 secs
                robot.lift.extend(Status.EXTENSIONS.get("right"));
                auto.set_timer(2);
                break;
            case 7:
                robot.lift.deposit(Status.DEPOSITS.get("right"));
                auto.set_timer(1);
                break;
            case 8:
                robot.lift.deposit(Status.DEPOSITS.get("center"));
                auto.set_timer(1);
                break;
            case 9:
                robot.lift.extend(Status.EXTENSIONS.get("center_from_right"));
                auto.set_timer(2);
                break;
            case 10:
                robot.lift.raise(0);
                auto.set_timer(1);
                break;
            case 11:
                robot.drivetrain.teleMove(-0.5,-0.15,0);
                auto.set_timer(2.5);
                break;
            case 12:
                robot.drivetrain.teleMove(0,0,0);
                auto.set_timer(.5);
                break;

//            case 7:
//                robot.drivetrain.teleMove(.22, .36, 0);
//                auto.set_timer(3);
//                break;
//            case 8:
//                robot.drivetrain.teleMove(0,0,0);
//                robot.drivetrain.teleMove(.35,0,0);
//                auto.set_timer(1.5);
//                break;
//            case 9:
//                robot.drivetrain.teleMove(0,0,0);
//                robot.intake.intake();
//                auto.set_timer(.75);
//                break;
//            case 10:
//                robot.intake.stop();
//                robot.drivetrain.teleMove(0,-.1,0);
//                auto.set_timer(.75);
//                break;
//            case 11:
//                robot.intake.outtake();
//                robot.drivetrain.teleMove(0,0,0);
//                auto.set_timer(.5);
//                break;
//            case 12:
//                robot.intake.stop();
//                break;
//            case 13:
//                robot.drivetrain.teleMove(0,-.1,0);
//                auto.set_timer(.75);
//                break;
//            case 14:
//                robot.drivetrain.teleMove(0,0,0);
//                auto.set_timer(.5);
//                break;

        }

        id = auto.update();
    }

    @Override
    public void stop() {
        auto.stop();
    }
}
