package org.firstinspires.ftc.teamcode.opmodes;

import com.acmerobotics.roadrunner.drive.Drive;
import com.acmerobotics.roadrunner.drive.DriveSignal;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.constraints.TrajectoryVelocityConstraint;
import com.acmerobotics.roadrunner.trajectory.constraints.TranslationalVelocityConstraint;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.opmodes.auto.AutonomousTemplate;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;
import org.firstinspires.ftc.teamcode.util.Logger;
import org.firstinspires.ftc.teamcode.util.Status;
import org.firstinspires.ftc.teamcode.util.event.EventBus;

import java.util.Vector;

@Autonomous(name="Red Park Auto", group="Blues")
public class RedParkRed extends LoggingOpMode
{
    private Robot robot;
    private Logger log;
    private AutonomousTemplate auto;
    private final String name = "Red Park Auto";

    private SampleMecanumDrive drive;
    private TrajectorySequence ts1;
    private int sequence = 1;
    private Pose2d startPose;

    private boolean running_roadrunner = true;
    private boolean driving = false;

    //    private ElapsedTime loop_timer = new ElapsedTime();
    private ElapsedTime timer;
    private ElapsedTime intake_timer;
    private ElapsedTime back_to_goal_timer;
    private ElapsedTime back_to_warehouse_timer;

    @Override
    public void init() {
        super.init();
        this.robot = Robot.initialize(hardwareMap, name, 1);
        drive = new SampleMecanumDrive(hardwareMap);
        this.auto = new AutonomousTemplate(
                name,
                this.robot,
                hardwareMap,
                new ControllerMap(gamepad1, gamepad2, new EventBus()),
                telemetry
        );
        startPose = new Pose2d(0, 0, Math.toRadians(180));
        drive.setPoseEstimate(startPose);
        log = new Logger(name);

        auto.init_lift();
        auto.init_server();
        timer = new ElapsedTime();
        intake_timer = new ElapsedTime();
        back_to_goal_timer = new ElapsedTime();
        back_to_warehouse_timer = new ElapsedTime();
    }

    public void start(){
//        loop_timer.reset();
        timer.reset();
        back_to_goal_timer.reset();
        back_to_warehouse_timer.reset();

        auto.getShippingHeight();
        log.i("Shipping Height: %d", auto.shipping_height);
        robot.lineFinder.alpha_init = robot.lineFinder.line_finder.alpha();
    }

    @Override
    public void loop() {
        switch (sequence) {
            case 0:
                if (timer.seconds() > 8){
                    sequence = 1;
                }
                break;
            case 1:
                ts1 = drive.trajectorySequenceBuilder(startPose)
                        .lineTo(new Vector2d(20, 0))
                        .addTemporalMarker(0.255, () -> {
                            if (auto.shipping_height < 1) {
                                auto.height = 3;
                            } else auto.height = auto.shipping_height;
                        })
                        .build();
                drive.followTrajectorySequenceAsync(ts1);
                sequence = 2;
                break;
            case 2:
                if (!drive.isBusy()){
                    sequence = 3;
                    timer.reset();
                }
                break;
            case 3:
                robot.drivetrain.move(0.4, 0.15, 0);
                if (timer.seconds() > 2){
                    timer.reset();
                    sequence = 4;
                }
                break;
            case 4:
                robot.drivetrain.move(0, 0.3, 0);
                if (timer.seconds() > 2){
                    robot.drivetrain.stop();
                    timer.reset();
                    sequence = 5;
                }
        }
        drive.update();
        auto.update();
    }

    @Override
    public void stop() {
        robot.intake.dist.close();
        auto.stop();
    }
}
