package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.AutoDrive;
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
    private final String name = "Blue Warehouse Auto";
    private int id = 0;
    public int state = 0; // 0 = driving, 1 = lifting, 2 = waiting for timer, 3 = sensing freight
    private final int direction = 1;


    @Override
    public void init() {
        super.init();
        this.robot = Robot.initialize(hardwareMap, name, direction);
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
        int state = 0; // STATE: 0=driving, 1=lifting, 2=waiting 3=detecting freight
        switch (id){
            case 0:
                auto.check_image(false); state = 2;
                auto.set_timer(0.5);
                break;
            case 1:
                robot.lift.extend(Status.STAGES.get("pitstop"), true); state = 1;
                robot.navigation.moveToPosition(0.0, -25.0, 0.0, 3.0, true); state = 0;
                break;
            case 2:
                switch(auto.shipping_height){
                    case 1:
                        robot.lift.rotate(Status.ROTATIONS.get("low_out")); state = 2;
                        break;
                    case 2:
                        robot.lift.rotate(Status.ROTATIONS.get("mid_out")); state = 2;
                        break;
                    case 3:
                        robot.lift.rotate(Status.ROTATIONS.get("high_out")); state = 2;
                        break;
                    case 0:
                        robot.lift.rotate(Status.ROTATIONS.get("high_out")); state = 2;
                        break;
                }
                auto.set_timer(.7);
                break;
            case 3:
                switch(auto.shipping_height){
                    case 1:
                        robot.lift.extend(Status.STAGES.get("low"), true); state = 2;
                        auto.set_timer(.7);
                        break;
                    case 2:
                        robot.lift.extend(Status.STAGES.get("mid"), true); state = 2;
                        auto.set_timer(.7);
                        break;
                    case 3:
                        robot.lift.extend(Status.STAGES.get("high"), true); state = 2;
                        auto.set_timer(.7);
                        break;
                    case 0:
                        robot.lift.extend(Status.STAGES.get("high"), true); state = 2;
                        auto.set_timer(.7);
                        break;
                }
                break;
            case 4:
                robot.intake.deposit(Status.DEPOSITS.get("dump")); state = 2;
                auto.set_timer(0.5);
                break;
            case 5:
                robot.intake.deposit(Status.DEPOSITS.get("carry")); state = 2;
                auto.set_timer(.2);
                break;
            case 6:
                robot.lift.extend(Status.STAGES.get("pitstop"), true); state = 2;
                auto.set_timer(.5);
                break;
            case 7:
                robot.lift.rotate(Status.ROTATIONS.get("in")); state = 2;
                auto.set_timer(.7);
                break;
            case 8:
                robot.lift.extend(0, true); state = 1;
                break;
            case 9:
                robot.intake.deposit(Status.DEPOSITS.get("front"));
                robot.intake.setIntakeFront(1);
                robot.navigation.moveToPosition(4.2,40.0,0.0, .4, true); state = 0;
                break;
            case 10:
                robot.intake.detectFreight();
                robot.drivetrain.move(0.6, 0,0); state = 3;
                break;
            case 11:
                robot.intake.deposit(Status.DEPOSITS.get("carry"));
                robot.intake.setIntakeFront(-1);
                robot.navigation.moveToPosition(4.2,24.0,0.0, 0.5, true); state = 0;
                break;
            case 12:
                robot.lift.extend(Status.STAGES.get("pitstop"), true);
                robot.navigation.moveToPosition(4.2,0.0,0.0, 0.6, true); state = 0;
                break;
            case 13:
                robot.lift.extend(Status.STAGES.get("high"), true);
                robot.navigation.moveToPosition(4.2,-24.0,0.0, 0.6, true); state = 0;
                break;
            case 14:
                robot.lift.rotate(Status.ROTATIONS.get("high_out")); state = 2;
                auto.set_timer(0.9);
                break;
            case 15:
                robot.intake.deposit(Status.DEPOSITS.get("dump")); state = 2;
                auto.set_timer(0.5);
                break;
        }
        id = auto.update(state);
        robot.eventBus.update();
        robot.scheduler.loop();
    }

    @Override
    public void stop() {
        auto.stop();
    }
}
