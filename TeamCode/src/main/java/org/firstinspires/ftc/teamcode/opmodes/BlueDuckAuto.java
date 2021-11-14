package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.hardware.Drivetrain;
import org.firstinspires.ftc.teamcode.hardware.Duck;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.hardware.navigation.Odometry;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.opmodes.teleop.ControlMgr;
import org.firstinspires.ftc.teamcode.opmodes.teleop.ServerControl;
import org.firstinspires.ftc.teamcode.util.event.EventBus;
import org.opencv.android.OpenCVLoader;

// we going to use the event bus system for this so that everything can be done on one thread
@Autonomous(name="Blue Duck Auto")
public class BlueDuckAuto extends LoggingOpMode
{
    private Robot robot;
    private Drivetrain drivetrain;
    private Odometry odometry;
    private Duck duck;
    private ControllerMap controllerMap;
    private ControlMgr controlMgr;
    private EventBus evBus;
    private ElapsedTime timer;

    private int id = 0;
    private boolean was_moving = false;
    private boolean moving = false;
    private boolean spinning = false;
    private boolean was_spinning = false;
    
    
    static
    {
        OpenCVLoader.initDebug();
    }
    
    @Override
    public void init()
    {
        super.init();
        robot = Robot.initialize(hardwareMap, "Autonomous");
        controllerMap = new ControllerMap(gamepad1, gamepad2, evBus);
        controlMgr = new ControlMgr(robot, controllerMap);
        timer = new ElapsedTime();

//        controlMgr.addModule(new ServerControl("Server Control"));
//        controlMgr.initModules();

        drivetrain = robot.drivetrain;
        odometry = robot.odometry;
        duck = robot.duck;

        odometry.podsDown();
    }
    
    @Override
    public void start() {

    }
    
    @Override
    public void loop() {
        switch (id){
            case 0:
                was_spinning = false;
                drivetrain.goToPosition(19.5, -15, 0, 0.2);
                break;
            case 1:
                was_spinning = false;
                drivetrain.goToPosition(21, -5, 0, 0.2);
                break;
            case 2:
                was_moving = false;
                if (!spinning){
                    timer.reset();
                }
                duck.spin(1);
                if (timer.seconds() >= 7.0){
                    spinning = false;
                } else {
                    spinning = true;
                }
                break;
            case 3:
                was_spinning = false;
                duck.spin(0);
                drivetrain.goToPosition(19.5, -25, 0, 0.2);
                break;
        }

        if (!moving && was_moving){
            id += 1;
            was_moving = false;
        } else if (!spinning && was_spinning){
            id += 1;
            was_spinning = false;
        }
        else {
            was_moving = true;
            was_spinning = true;
        }

        double[] odo_data = odometry.getOdoData();
        telemetry.addData("Y: ", odo_data[0]);
        telemetry.addData("X: ", odo_data[1]);
        telemetry.addData("Heading: ", odo_data[2]);

        double[] delta_positions = drivetrain.getPositionDeltas();
        telemetry.addData("Forward Power: ", delta_positions[0]);
        telemetry.addData("Strafe Power: ", delta_positions[1]);
        telemetry.addData("Turn Power: ", delta_positions[2]);

        telemetry.addData("Moving: ", moving);
        telemetry.addData("Timer: ", timer.seconds());

        moving = !drivetrain.updatePosition();
        odometry.update();
        telemetry.update();
    }
    
    @Override
    public void stop()
    {
        super.stop();
    }
}
