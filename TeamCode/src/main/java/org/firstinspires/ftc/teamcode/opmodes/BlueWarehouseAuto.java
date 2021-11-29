package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.opmodes.auto.AutonomousTemplate;
import org.firstinspires.ftc.teamcode.util.Status;
import org.firstinspires.ftc.teamcode.util.event.EventBus;

@Autonomous(name="Blue Warehouse Auto", group="Blues")
public class BlueWarehouseAuto extends LoggingOpMode
{
    private Robot robot;
    private AutonomousTemplate auto;
    private String name = "Blue Warehouse Auto";
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
        auto.check_image();
        switch (id){
            case 0:
                robot.drivetrain.teleMove(0,0,0);
                auto.set_timer(1.5);
                break;
            case 1:
                robot.drivetrain.teleMove(-.17, .3375, 0);
                auto.set_timer(1.5);
                break;
            case 2:
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
                        robot.lift.raise(Status.STAGES.get("high")); // Incase no image detected
                        break;
                }
                break;
            case 3:
                switch (auto.shipping_height) {
                    case 1:
                        robot.lift.extend(Status.EXTENSIONS.get("low_right"));
                        break;
                    case 2:
                        robot.lift.extend(Status.EXTENSIONS.get("mid_right"));
                        break;
                    case 3:
                        robot.lift.extend(Status.EXTENSIONS.get("high_right"));
                        break;
                    case -1:
                        robot.lift.extend((Status.EXTENSIONS.get("high_right")));
                        break;
                }
                auto.set_timer(1.5);
                break;
            case 4:
                robot.lift.deposit(Status.DEPOSITS.get("right"));
                auto.set_timer(.5);
                break;
            case 5:
                robot.lift.extend(Status.EXTENSIONS.get("center_from_right"));
                auto.set_timer(2.5);
                break;
            case 6:
                robot.lift.deposit(Status.DEPOSITS.get("center"));
                auto.set_timer(.5);
                break;
            case 7:
                robot.lift.raise(0);
                auto.set_timer(1);
                break;
            case 8:
                robot.drivetrain.teleMove(.21, -.36, 0);
                auto.set_timer(2);
                break;
            case 9:
                robot.drivetrain.teleMove(.37,0,0);
                robot.intake.intake();
                auto.set_timer(1.5);
                break;
            case 10:
                robot.drivetrain.teleMove(0,0,0);
                auto.set_timer(1);
                break;
            case 11:
                robot.intake.stop();
                auto.set_timer(.25);
                break;
            case 12:
                robot.intake.outtake();
                auto.set_timer(.5);
                break;
            case 13:
                robot.intake.stop();
                auto.set_timer(.25);
                break;
            case 14:
                robot.drivetrain.teleMove(-0.45,-0.02,0);
                auto.set_timer(1.5);
                break;
            case 15:
                robot.drivetrain.teleMove(0,0,0);
                auto.set_timer(.25);
                break;
            case 16:
                robot.drivetrain.teleMove(-0.09,0.31,0);
                auto.set_timer(2);
                break;
            case 17:
                robot.drivetrain.teleMove(0,0,0);
                robot.lift.raise(Status.STAGES.get("high"));
                break;
            case 18:
                robot.lift.extend(Status.EXTENSIONS.get("high_right"));
                auto.set_timer(1.5);
                break;
            case 19:
                robot.lift.deposit(Status.DEPOSITS.get("right"));
                auto.set_timer(.5);
                break;
            case 20:
                robot.lift.extend(Status.EXTENSIONS.get("center_from_right"));
                auto.set_timer(2.5);
                break;
            case 21:
                robot.lift.deposit(Status.DEPOSITS.get("center"));
                auto.set_timer(.5);
                break;
            case 22:
                robot.lift.raise(0);
                auto.set_timer(1);
                break;
            case 23:
                robot.drivetrain.teleMove(.15, -.36, 0);
                auto.set_timer(2);
                break;
            case 24:
                robot.drivetrain.teleMove(0,0,0);
                robot.drivetrain.teleMove(.35,0,0);
                auto.set_timer(2);
                break;
            case 25:
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
