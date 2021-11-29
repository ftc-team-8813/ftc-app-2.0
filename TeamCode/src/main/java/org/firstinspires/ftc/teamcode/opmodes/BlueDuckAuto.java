package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.opmodes.auto.AutonomousTemplate;
import org.firstinspires.ftc.teamcode.util.Status;
import org.firstinspires.ftc.teamcode.util.event.EventBus;

@Autonomous(name="Blue Duck Auto", group="Blues")
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
    }

    @Override
    public void start() {
        robot.odometry.resetEncoders();
        auto.timer.reset();
    }

    @Override
    public void loop() {
        switch (id){
            case 0:
                auto.check_image();
                auto.set_timer(1.5);
                break;
            case 1:
                robot.drivetrain.teleMove(0.28, -0.25, 0);
                auto.set_timer(1.7);
                break;
            case 2:
                robot.drivetrain.teleMove(-.01,.23,0);
                auto.set_timer(1);
                break;
            case 3:
                robot.drivetrain.teleMove(0,0,0);
                robot.duck.spin(1); // Change if this is not right
                auto.set_timer(4);
                break;
            case 4:
                robot.duck.spin(0);
                robot.drivetrain.teleMove(-0.232,-0.232,0);
                auto.set_timer(2.5);
                break;
            case 5:
                robot.drivetrain.teleMove(0,0,0);
                robot.drivetrain.teleMove(0.05,-0.05,0);
                auto.set_timer(1);
                break;
            case 6:
                robot.drivetrain.teleMove(0,0,0);
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
                    case -1:
                        robot.lift.raise(Status.STAGES.get("high"));
                }
                break;
            case 7:
                switch (auto.shipping_height) {
                    case 1:
                        robot.lift.extend(Status.EXTENSIONS.get("low_left"));
                        break;
                    case 2:
                        robot.lift.extend(Status.EXTENSIONS.get("mid_left"));
                        break;
                    case 3:
                        robot.lift.extend(Status.EXTENSIONS.get("high_left"));
                        break;
                    case -1:
                        robot.lift.extend((Status.EXTENSIONS.get("high_left")));
                        break;
                }
                auto.set_timer(1.5);
                break;
            case 8:
                robot.lift.deposit(Status.DEPOSITS.get("left"));
                auto.set_timer(1);
                break;
            case 9:
                robot.lift.extend(Status.EXTENSIONS.get("center_from_left"));
                auto.set_timer(2.5);
                break;
            case 10:
                robot.lift.deposit(Status.DEPOSITS.get("center"));
                auto.set_timer(1);
                break;
            case 11:
                robot.lift.raise(0);
                auto.set_timer(1);
                break;
            case 12:
                robot.drivetrain.teleMove(-0.5,0.265,0);
                auto.set_timer(2);
                break;
            case 13:
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
