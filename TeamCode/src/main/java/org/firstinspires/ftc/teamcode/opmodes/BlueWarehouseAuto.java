package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

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
        auto.init_lift();
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
                auto.set_timer(1.5);
                break;
            case 1:
                robot.drivetrain.teleMove(-.34,0,0);
                auto.set_timer(1.5);
                break;
            case 2:
                robot.drivetrain.teleMove(0,0,0);
                robot.lift.extend(Status.STAGES.get("pitstop"), true);
                break;
            case 3:
                robot.lift.rotate(Status.ROTATIONS.get("out"));
                auto.set_timer(.7);
                break;
            case 4:
                switch(auto.shipping_height){
                    case 1:
                        robot.lift.extend(Status.STAGES.get("low"), true);
                        auto.set_timer(2);
                        break;
                    case 2:
                        robot.lift.extend(Status.STAGES.get("mid"), true);
                        auto.set_timer(2);
                        break;
                    case 3:
                        robot.lift.extend(Status.STAGES.get("high"), true);
                        auto.set_timer(2);
                        break;
                    case 0:
                        robot.lift.extend(Status.STAGES.get("high"), true);
                        auto.set_timer(2);
                        break;
                }
                break;
            case 5:
                robot.intake.deposit(Status.DEPOSITS.get("dump"));
                auto.set_timer(2);
                break;
            case 6:
                robot.intake.deposit(Status.DEPOSITS.get("carry"));
                auto.set_timer(.5);
                break;
            case 7:
                robot.lift.extend(Status.STAGES.get("pitstop"), true);
                break;
            case 8:
                robot.lift.rotate(Status.ROTATIONS.get("in"));
                auto.set_timer(1);
                break;
            case 9:
                robot.lift.extend(0, true);
                break;
            case 10:
                robot.intake.deposit(Status.DEPOSITS.get("front"));
                robot.drivetrain.teleMove(0,0,0);
                auto.set_timer(.5);
                break;
            case 11:
                robot.intake.setIntakeFront(1);
                robot.drivetrain.teleMove(.37,0,0);
                auto.set_timer(3);
                break;
            case 12:
                robot.intake.setIntakeFront(1);
                robot.drivetrain.teleMove(.18,0,0);
                auto.set_timer(1); // Freight Detector
                break;
            case 13:
                robot.drivetrain.teleMove(.25,0,0);
                auto.set_timer(0); // Refactor Later
                break;
            case 14:
                robot.intake.deposit(Status.DEPOSITS.get("carry"));
                auto.set_timer(.5);
                break;
            case 15:
                robot.intake.stop();
                robot.drivetrain.teleMove(0,0,0);
                auto.set_timer(.25);
            case 16:
                robot.intake.setIntakeFront(-1);
                robot.drivetrain.teleMove(-.45,0,0);
                auto.set_timer(1.3);
                break;
            case 17:
                robot.intake.stop();
                robot.drivetrain.teleMove(0,0,0);
                robot.lift.extend(Status.STAGES.get("pitstop"), true);
                break;
            case 18:
                robot.lift.rotate(Status.ROTATIONS.get("out"));
                auto.set_timer(1);
                break;
            case 19:
                robot.lift.extend(Status.STAGES.get("high"), true);
                auto.set_timer(1);
                break;
            case 20:
                robot.intake.deposit(Status.DEPOSITS.get("dump"));
                auto.set_timer(2);
                break;
            case 21:
                robot.intake.deposit(Status.DEPOSITS.get("carry"));
                auto.set_timer(.5);
                break;
            case 22:
                robot.lift.extend(Status.STAGES.get("pitstop"), true);
                break;
            case 23:
                robot.lift.rotate(Status.ROTATIONS.get("in"));
                auto.set_timer(1.5);
                break;
            case 24:
                robot.lift.extend(0, true);
                break;
            case 25:
                robot.intake.deposit(Status.DEPOSITS.get("front"));
                robot.drivetrain.teleMove(0,0,0);
                auto.set_timer(.5);
                break;
            case 26:
                robot.intake.setIntakeFront(1);
                robot.drivetrain.teleMove(.5,0,0);
                auto.set_timer(1.5);
                break;
                /*
            case 27:
                robot.drivetrain.teleMove(.25,0,0);
                auto.set_timer(2);
                break;
            case 28:
                robot.intake.deposit(Status.DEPOSITS.get("carry"));
                auto.set_timer(.5);
                break;
            case 29:
                robot.intake.stop();
                robot.drivetrain.teleMove(0,0,0);
                auto.set_timer(.25);
            case 30:
                robot.intake.setIntakeFront(-1);
                robot.drivetrain.teleMove(-.45,0,0);
                auto.set_timer(3);
                break;
            case 31:
                robot.intake.stop();
                robot.drivetrain.teleMove(0,0,0);
                break;

                 */
        }

        id = auto.update();
    }

    @Override
    public void stop() {
        auto.stop();
    }
}
