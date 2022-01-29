package org.firstinspires.ftc.teamcode.opmodes;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.opmodes.auto.AutonomousTemplate;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;
import org.firstinspires.ftc.teamcode.util.event.EventBus;

@Autonomous(name="Blue Warehouse Auto RR", group="Blues")
public class BlueWarehouseAutoRR extends LoggingOpMode
{
    private Robot robot;
    private AutonomousTemplate auto;
    private final String name = "Blue Warehouse Auto RR";
    private ElapsedTime timer;
    private boolean waiting = false;
    private boolean checking_image = true;
    private SampleMecanumDrive drive;
    private TrajectorySequence ts1;
    private TrajectorySequence ts2;
    private int sequence = 0;
    private Pose2d startPose;

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
                telemetry,
                drive
        );
        startPose = new Pose2d(12, 0, Math.toRadians(0));
        drive.setPoseEstimate(startPose);
        ts1 = drive.trajectorySequenceBuilder(startPose)
                .lineTo(new Vector2d(0, 0))
                .addTemporalMarker(() -> {scorePreload();})
                .build();
        timer = new ElapsedTime();
        auto.init_camera();
        auto.init_lift();
    }

    @Override
    public void start() {
        timer.reset();
    }

    @Override
    public void loop() {
        if (checking_image) {
            auto.check_image(false);
            if (!waiting) {timer.reset(); waiting = true;}
            if (timer.seconds() > 0.3) {waiting = false; checking_image = false; sequence = 1;} //advances to first move
        }

        if (!drive.isBusy()) {
            if (sequence == 1) {drive.followTrajectorySequenceAsync(ts1); sequence = 2;}
            else if (sequence == 2) {
                ts2 = drive.trajectorySequenceBuilder(ts1.end())
                        .lineTo(new Vector2d(30, 0))
                        //.addTemporalMarker(() -> {collectFreight();})
                        //.lineToLinearHeading(new Pose2d(44, -8, Math.toRadians(350)))
                        .splineToLinearHeading(new Pose2d(44, -8, Math.toRadians(340)), Math.toRadians(350))
                        .build();
                drive.followTrajectorySequenceAsync(ts2);
                sequence = 3;
            }
        }

        auto.update();
        //auto.tape_localize();
        //robot.eventBus.update();
        drive.update();
    }

    public void scorePreload() {}

    public void collectFreight() {}

    @Override
    public void stop() {
        auto.stop();
    }
}
