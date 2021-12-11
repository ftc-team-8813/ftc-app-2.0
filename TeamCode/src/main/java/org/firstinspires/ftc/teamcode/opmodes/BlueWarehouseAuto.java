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
                robot.drivetrain.teleMove(-.39,0,0);
                auto.set_timer(1);
                break;
            case 2:
                robot.drivetrain.teleMove(0,0,0);
                robot.lift.extend(Status.STAGES.get("pitstop"), true);
//                auto.set_timer(2);
                break;
            case 3:
                robot.lift.rotate(Status.EXTENSIONS.get("out"));
                auto.set_timer(1);
                break;
            case 4:
                switch(auto.shipping_height){
                    case 1:
                        robot.lift.extend(Status.STAGES.get("low"), true);
                        auto.set_timer(1);
                        break;
                    case 2:
                        robot.lift.extend(Status.STAGES.get("mid"), true);
                        auto.set_timer(1);
                        break;
                    case 3:
                        robot.lift.extend(Status.STAGES.get("high"), true);
                        auto.set_timer(1);
                        break;
                    case 0:
                        robot.lift.extend(Status.STAGES.get("high"), true);
                        auto.set_timer(1);
                        break;
                }
                break;
            case 5:
                robot.intake.deposit(Status.DEPOSITS.get("dump"));
                auto.set_timer(.75);
                break;
            case 6:
                robot.intake.deposit(Status.DEPOSITS.get("carry"));
                auto.set_timer(.5);
                break;
            case 7:
                robot.lift.extend(Status.STAGES.get("pitstop"), true);
                break;
            case 8:
                robot.lift.rotate(Status.EXTENSIONS.get("in"));
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
                robot.drivetrain.teleMove(.5,0,0);
                auto.set_timer(1.5);
                break;
            case 12:
                robot.drivetrain.teleMove(.25,0,0);
                auto.set_timer(2);
                break;
            case 13:
                robot.intake.deposit(Status.DEPOSITS.get("carry"));
                auto.set_timer(.5);
                break;
            case 14:
                robot.intake.stop();
                robot.drivetrain.teleMove(0,0,0);
                auto.set_timer(.25);
            case 15:
                robot.intake.setIntakeFront(-1);
                robot.drivetrain.teleMove(-.45,0,0);
                auto.set_timer(3);
                break;
            case 16:
                robot.intake.stop();
                robot.drivetrain.teleMove(0,0,0);
                break;
        }

        id = auto.update();
    }

    @Override
    public void stop() {
        auto.stop();
    }
}
