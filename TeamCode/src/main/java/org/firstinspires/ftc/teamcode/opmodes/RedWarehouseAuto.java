package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.opmodes.auto.AutonomousTemplate;
import org.firstinspires.ftc.teamcode.util.Status;
import org.firstinspires.ftc.teamcode.util.event.EventBus;

// we going to use the event bus system for this so that everything can be done on one thread
@Autonomous(name="Red Warehouse Auto")
public class RedWarehouseAuto extends LoggingOpMode
{
    private Robot robot;
    private AutonomousTemplate auto;
    private String name = "Red Warehouse Auto";
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
                robot.drivetrain.teleMove(-.18, -.355, 0);
                auto.set_timer(1.5);
                break;
            case 1:
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
            case 2:
                // Moves to left position (can be adjusted to different positions in Status.java) within 2 secs
                robot.lift.extend(Status.EXTENSIONS.get("left"));
                auto.set_timer(1.5);
                break;
            case 3:
                robot.lift.deposit(Status.DEPOSITS.get("left"));
                auto.set_timer(.75);
                break;
            case 4:
                robot.lift.deposit(Status.DEPOSITS.get("center"));
                auto.set_timer(.75);
                break;
            case 5:
                robot.lift.extend(Status.EXTENSIONS.get("center_from_left"));
                auto.set_timer(3.5);
                break;
            case 6:
                robot.lift.raise(0);
                auto.set_timer(1);
                break;
            case 7:
                robot.drivetrain.teleMove(.22, .36, 0);
                auto.set_timer(2);
                break;
            case 8:
                robot.drivetrain.teleMove(0,0,0);
                robot.drivetrain.teleMove(.37,0,0);
                auto.set_timer(1.5);
                break;
            case 9:
                robot.drivetrain.teleMove(0,0,0);
                robot.intake.intake();
                auto.set_timer(1.3);
                break;
            case 10:
                robot.intake.stop();
                auto.set_timer(.25);
                break;
            case 11:
                robot.intake.outtake();
                auto.set_timer(.5);
                break;
            case 12:
                robot.intake.stop();
                auto.set_timer(.25);
                break;
            case 13:
                robot.drivetrain.teleMove(-0.425,-0.05,0);
                auto.set_timer(1.5);
                break;
            case 14:
                robot.drivetrain.teleMove(0,0,0);
                auto.set_timer(.25);
                break;
            case 15:
                robot.drivetrain.teleMove(-0.03,-0.3,0);
                auto.set_timer(2);
                break;
            case 16:
                robot.drivetrain.teleMove(0,0,0);
                robot.lift.raise(Status.STAGES.get("high"));
                break;
            case 17:
                // Moves to left position (can be adjusted to different positions in Status.java) within 2 secs
                robot.lift.extend(Status.EXTENSIONS.get("left"));
                auto.set_timer(1.5);
                break;
            case 18:
                robot.lift.deposit(Status.DEPOSITS.get("left"));
                auto.set_timer(.75);
                break;
            case 19:
                robot.lift.deposit(Status.DEPOSITS.get("center"));
                auto.set_timer(.75);
                break;
            case 20:
                robot.lift.extend(Status.EXTENSIONS.get("center_from_left"));
                auto.set_timer(3);
                break;
            case 21:
                robot.lift.raise(0);
                auto.set_timer(1);
                break;
            case 22:
                robot.drivetrain.teleMove(.15, .36, 0);
                auto.set_timer(2);
                break;
            case 23:
                robot.drivetrain.teleMove(0,0,0);
                robot.drivetrain.teleMove(.35,0,0);
                auto.set_timer(1.8);
                break;
            case 24:
                robot.drivetrain.teleMove(0,0,0);
                auto.set_timer(.5);
                break;

        }

        id = auto.update();
    }

    @Override
    public void stop() {
        auto.stop();
    }
}
