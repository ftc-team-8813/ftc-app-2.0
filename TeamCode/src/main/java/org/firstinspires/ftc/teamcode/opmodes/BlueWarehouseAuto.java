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
    private int id = 0;


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
                auto.check_image(false);
                auto.set_timer(1.5);
                break;
            case 1:
                robot.drivetrain.move(-.54,0,0);
                auto.set_timer(.6);
                break;
            case 2:
                robot.drivetrain.move(0,0,0);
                robot.lift.extend(Status.STAGES.get("pitstop"), true);
                break;
            case 3:
                switch(auto.shipping_height){
                    case 1:
                        robot.lift.rotate(Status.ROTATIONS.get("low_out"));
                        break;
                    case 2:
                        robot.lift.rotate(Status.ROTATIONS.get("mid_out"));
                        break;
                    case 3:
                        robot.lift.rotate(Status.ROTATIONS.get("high_out"));
                        break;
                    case 0:
                        robot.lift.rotate(Status.ROTATIONS.get("high_out"));
                        break;
                }
                auto.set_timer(.9);
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
                        auto.set_timer(1.7);
                        break;
                }
                break;
            case 5:
                robot.intake.deposit(Status.DEPOSITS.get("dump"));
                auto.set_timer(1.75);
                break;
            case 6:
                robot.intake.deposit(Status.DEPOSITS.get("carry"));
                auto.set_timer(.5);
                break;
            case 7:
                robot.lift.extend(Status.STAGES.get("pitstop"), true);
                auto.set_timer(.5);
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
                robot.drivetrain.move(0,0,0);
                auto.set_timer(.5);
                break;
            case 11:
                robot.intake.setIntakeFront(1);
                robot.drivetrain.move(.7,.03,0);
                auto.set_timer(1.2);
                break;
            case 12:
                robot.drivetrain.move(.2,0,0);
                auto.set_timer(1); // Freight Detector
                break;
            case 13:
                robot.drivetrain.move(.1,0,0);
                auto.set_timer(0.25); // Refactor Later
                break;
            case 14:
                robot.intake.setIntakeFront(-1);
                auto.set_timer(.5);
                break;
            case 15:
                robot.drivetrain.move(0,0,0);
                auto.set_timer(.25);
            case 16:
                robot.intake.deposit(Status.DEPOSITS.get("carry"));
                robot.drivetrain.move(-.7,-.04,0);
                auto.set_timer(.61);
                break;
            case 17:
                robot.intake.stop();
                robot.drivetrain.move(0,0,0);
                robot.lift.extend(Status.STAGES.get("pitstop"), true);
                auto.set_timer(.5);
                break;
            case 18:
                robot.lift.rotate(Status.ROTATIONS.get("high_out"));
                auto.set_timer(1);
                break;
            case 19:
                robot.lift.extend(Status.STAGES.get("high"), true);
                auto.set_timer(1);
                break;
            case 20:
                robot.intake.deposit(Status.DEPOSITS.get("dump"));
                auto.set_timer(1.8);
                break;
            case 21:
                robot.intake.deposit(Status.DEPOSITS.get("carry"));
                auto.set_timer(.5);
                break;
            case 22:
                robot.lift.extend(Status.STAGES.get("pitstop"), true);
                auto.set_timer(.5);
                break;
            case 23:
                robot.lift.rotate(Status.ROTATIONS.get("in"));
                auto.set_timer(1);
                break;
            case 24:
                robot.lift.extend(0, true);
                break;
            case 25:
                robot.intake.deposit(Status.DEPOSITS.get("front"));
                robot.drivetrain.move(0,0,0);
                auto.set_timer(.5);
                break;
            case 26:
                robot.intake.setIntakeFront(1);
                robot.drivetrain.move(.7,.03,0);
                auto.set_timer(1.3);
                break;
            case 27:
                robot.drivetrain.move(.2,0,0);
                auto.set_timer(1); // Freight Detector
                break;
            case 28:
                robot.drivetrain.move(.1,0,0);
                auto.set_timer(0.25); // Refactor Later
                break;
            case 29:
                robot.intake.setIntakeFront(-1);
                auto.set_timer(.5);
                break;
            case 30:
                robot.drivetrain.move(0,0,0);
                auto.set_timer(.25);
            case 31:
                robot.intake.deposit(Status.DEPOSITS.get("carry"));
                robot.drivetrain.move(-.7,-.04,0);
                auto.set_timer(.65);
                break;
            case 32:
                robot.intake.stop();
                robot.drivetrain.move(0,0,0);
                robot.lift.extend(Status.STAGES.get("pitstop"), true);
                auto.set_timer(.5);
                break;
            case 33:
                robot.lift.rotate(Status.ROTATIONS.get("high_out"));
                auto.set_timer(1);
                break;
            case 34:
                robot.lift.extend(Status.STAGES.get("high"), true);
                auto.set_timer(1);
                break;
            case 35:
                robot.intake.deposit(Status.DEPOSITS.get("dump"));
                auto.set_timer(1.8);
                break;
            case 36:
                robot.intake.deposit(Status.DEPOSITS.get("carry"));
                auto.set_timer(.5);
                break;
            case 37:
                robot.lift.extend(Status.STAGES.get("pitstop"), true);
                auto.set_timer(.5);
                break;
            case 38:
                robot.lift.rotate(Status.ROTATIONS.get("in"));
                auto.set_timer(1);
                break;
            case 39:
                robot.lift.extend(0, true);
                break;
            case 40:
                robot.intake.deposit(Status.DEPOSITS.get("front"));
                robot.drivetrain.move(0,0,0);
                auto.set_timer(.5);
                break;
            case 41:
                robot.drivetrain.move(.7,0,0);
                auto.set_timer(1.05);
                break;
            case 42:
                robot.drivetrain.move(0, 0, 0);
                auto.set_timer(0.5);
                break;
        }

        id = auto.update();
    }

    @Override
    public void stop() {
        auto.stop();
    }
}
