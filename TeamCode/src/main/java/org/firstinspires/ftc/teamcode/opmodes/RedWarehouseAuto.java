package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.hardware.Drivetrain;
import org.firstinspires.ftc.teamcode.hardware.Duck;
import org.firstinspires.ftc.teamcode.hardware.Intake;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.hardware.navigation.Odometry;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.opmodes.teleop.ControlMgr;
import org.firstinspires.ftc.teamcode.opmodes.teleop.DriveControl;
import org.firstinspires.ftc.teamcode.opmodes.teleop.DuckControl;
import org.firstinspires.ftc.teamcode.opmodes.teleop.IntakeControl;
import org.firstinspires.ftc.teamcode.opmodes.teleop.LiftControl;
import org.firstinspires.ftc.teamcode.opmodes.teleop.OdometryControl;
import org.firstinspires.ftc.teamcode.opmodes.teleop.ServerControl;
import org.firstinspires.ftc.teamcode.util.Logger;
import org.firstinspires.ftc.teamcode.util.Scheduler;
import org.firstinspires.ftc.teamcode.util.event.EventBus;
import org.firstinspires.ftc.teamcode.util.event.LifecycleEvent;
import org.opencv.android.OpenCVLoader;


import static org.firstinspires.ftc.teamcode.util.event.LifecycleEvent.START;

// we going to use the event bus system for this so that everything can be done on one thread
@Autonomous(name="Red Warehouse Auto")
public class RedWarehouseAuto extends LoggingOpMode
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

        odometry.podsUp();
    }

    @Override
    public void start() {

    }

    @Override
    public void loop() {
        switch (id){
            case 0:
                if (!moving){
                    timer.reset();
                }
                drivetrain.teleMove(.5, 0, 0);
                if (timer.seconds() >= 0.75){
                    moving = false;
                } else {
                    moving = true;
                }
                break;
            case 1:
                if (!moving){
                    timer.reset();
                }
                drivetrain.teleMove(0, -0.3, 0);
                if (timer.seconds() >= 2){
                    moving = false;
                } else {
                    moving = true;
                }
                break;
            case 2:
                moving = false;
                was_moving = false;
                drivetrain.teleMove(0, 0, 0);
        }

        if (!moving && was_moving){
            id += 1;
            was_moving = false;
        }else {
            was_moving = true;
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

        odometry.update();
        telemetry.update();
    }

    @Override
    public void stop()
    {
        super.stop();
    }
}
