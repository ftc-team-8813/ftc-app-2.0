package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.opmodes.auto.AutonomousTemplate;
import org.firstinspires.ftc.teamcode.util.Status;
import org.firstinspires.ftc.teamcode.util.event.EventBus;

// we going to use the event bus system for this so that everything can be done on one thread
@Autonomous(name="Blue Warehouse Auto")
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
//        auto.init_server();
//        auto.init_odometry(0, 0, 0);
    }

    @Override
    public void start() {
        robot.odometry.resetEncoders();
    }

    @Override
    public void loop() {
        // DON'T FORGET BREAKS
        // NEXT CASE SHOULD BE +1
        switch (id){
            case 0:
                // Sets powers and moves for set time
                robot.drivetrain.teleMove(0, 0, 0);
                auto.set_timer(2);
                break;
            case 1:
                // Lift heights based on detected height
                if (auto.shipping_height == -1){ // Waits for detection to not be run
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
                }
                break;
            case 2:
                // Moves to left position (can be adjusted to different positions in Status.java) within 2 secs
                robot.lift.extend(Status.EXTENSIONS.get("left"));
                auto.set_timer(2);
                break;
        }

        auto.check_image();
        id = auto.update();
    }

    @Override
    public void stop() {
        auto.stop();
    }
}
