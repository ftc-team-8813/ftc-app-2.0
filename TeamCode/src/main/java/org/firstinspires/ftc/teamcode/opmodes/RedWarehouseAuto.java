package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.opmodes.auto.AutonomousTemplate;
import org.firstinspires.ftc.teamcode.util.Status;
import org.firstinspires.ftc.teamcode.util.event.EventBus;

@Autonomous(name="Red Warehouse Auto", group="Reds")
public class RedWarehouseAuto extends LoggingOpMode
{
    private Robot robot;
    private AutonomousTemplate auto;
    private String name = "Red Warehouse Auto";
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
                auto.set_timer(2);
                break;
            case 1:
                robot.drivetrain.move(.4,0,0);
                auto.set_timer(.8);
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
                        break;
                    case 2:
                        robot.lift.extend(Status.STAGES.get("mid"), true);
                        break;
                    case 3:
                        robot.lift.extend(Status.STAGES.get("high"), true);
                        break;
                    case 0:
                        robot.lift.extend(Status.STAGES.get("high"), true);
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
                robot.intake.deposit(Status.DEPOSITS.get("back"));
                robot.drivetrain.move(0,0,0);
                auto.set_timer(.5);
                break;
            case 11:
                robot.intake.setIntakeBack(1);
                robot.drivetrain.move(-.28,0,0);
                auto.set_timer(3.3);
                break;
            case 12:
                robot.intake.setIntakeBack(-1);
                robot.drivetrain.move(0,0,0);
                auto.set_timer(1.7);
                break;
            case 13:
                robot.intake.deposit(Status.DEPOSITS.get("carry"));
                auto.set_timer(.5);
                break;
            case 14:
                robot.intake.stop();
                robot.drivetrain.move(0,0,0);
                auto.set_timer(.25);
            case 15:
                robot.intake.setIntakeBack(1);
                robot.drivetrain.move(.34,0,0);
                auto.set_timer(.8);
                break;
            case 16:
                robot.intake.stop();
                robot.drivetrain.move(0,0,0);
                robot.lift.extend(Status.STAGES.get("pitstop"), true);
                break;
            case 17:
                robot.lift.rotate(Status.ROTATIONS.get("high_out"));
                auto.set_timer(1);
                break;
            case 18:
                robot.lift.extend(Status.STAGES.get("high"), true);
                break;
            case 19:
                robot.intake.deposit(Status.DEPOSITS.get("dump"));
                auto.set_timer(2);
                break;
            case 20:
                robot.intake.deposit(Status.DEPOSITS.get("carry"));
                auto.set_timer(.5);
                break;
            case 21:
                robot.lift.extend(Status.STAGES.get("pitstop"), true);
                break;
            case 22:
                robot.lift.rotate(Status.ROTATIONS.get("in"));
                auto.set_timer(1.5);
                break;
            case 23:
                robot.lift.extend(0, true);
                break;
            case 24:
                robot.intake.deposit(Status.DEPOSITS.get("back"));
                robot.drivetrain.move(0,0,0);
                auto.set_timer(.5);
                break;
            case 25:
                robot.drivetrain.move(-.3,0,0);
                auto.set_timer(3.5);
                break;
            case 26:
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

