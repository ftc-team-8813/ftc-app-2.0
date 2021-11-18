package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.hardware.Drivetrain;
import org.firstinspires.ftc.teamcode.hardware.Duck;
import org.firstinspires.ftc.teamcode.hardware.Lift;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.hardware.navigation.Odometry;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.opmodes.teleop.ControlMgr;
import org.firstinspires.ftc.teamcode.util.Logger;
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
    private Lift lift;
    private ControllerMap controllerMap;
    private ControlMgr controlMgr;
    private ElapsedTime timer;
    private Logger logger;

    private int id = 0;
    private int timer_delay = 1000; // Set high to not trigger next move
    private boolean waiting = false;


    static
    {
        OpenCVLoader.initDebug();
    }

    @Override
    public void init()
    {
        super.init();
        robot = Robot.initialize(hardwareMap, "Autonomous");
        logger = new Logger("Autonomous");
        controllerMap = new ControllerMap(gamepad1, gamepad2, new EventBus());
        controlMgr = new ControlMgr(robot, controllerMap);
        timer = new ElapsedTime();

//        controlMgr.addModule(new ServerControl("Server Control"));
//        controlMgr.initModules();

        drivetrain = robot.drivetrain;
        odometry = robot.odometry;
        duck = robot.duck;
        lift = robot.lift;

        odometry.podsDown();
    }

    @Override
    public void start() {

    }

    @Override
    public void loop() {
        switch (id){
            case 0:
                duck.spin(1);
                drivetrain.goToPosition(19, -14, 0, 0.1);
                break;
            case 1:
                drivetrain.goToPosition(19, -7.5, 0, 0.1);
                break;
            case 2:
                timer_delay = 5;
                waiting = true;
                break;
            case 3:
                duck.spin(0);
                drivetrain.goToPosition(19.5, -27, 0, 0.1);
                break;
        }

        double[] odo_data = odometry.getOdoData();
        telemetry.addData("Y: ", odo_data[0]);
        telemetry.addData("X: ", odo_data[1]);
        telemetry.addData("Heading: ", odo_data[2]);

        double[] target_positions = drivetrain.getTargets();
        telemetry.addData("Target Y: ", target_positions[0]);
        telemetry.addData("Target X: ", target_positions[1]);
        telemetry.addData("Target Heading: ", target_positions[2]);

        double[] delta_positions = drivetrain.getPositionDeltas();
        telemetry.addData("Delta Y: ", delta_positions[0]);
        telemetry.addData("Delta X: ", delta_positions[1]);
        telemetry.addData("Delta Heading: ", delta_positions[2]);

        telemetry.addData("Timer: ", timer.seconds());
        telemetry.addData("Id: ", id);
        telemetry.addData("Reached: ", drivetrain.reached);


        drivetrain.updatePosition();
        odometry.update();
        telemetry.update();

        if (!waiting){
            timer.reset();
        }
        if (drivetrain.ifReached()){ // Checks after updates to get values for deltas
            logger.i("Drivetrain Reached");
            id += 1;
        } else if (timer.seconds() > timer_delay){
            logger.i("Timer Reached");
            id += 1;
            waiting = false;
        }
    }

    @Override
    public void stop()
    {
        super.stop();
    }
}
