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

@Autonomous(name="Red Warehouse Auto RR", group="Reds")
public class RedWarehouseAuto extends LoggingOpMode
{
    private Robot robot;
    private Logger log;
    private AutonomousTemplate auto;
    private final String name = "Red Warehouse Auto RR";

    private SampleMecanumDrive drive;
    private TrajectorySequence ts1;
    private TrajectorySequence ts2;
    private TrajectorySequence ts3;
    private int sequence = 0;
    private Pose2d startPose;
    private Pose2d intakePoseOffset;

    private ElapsedTime timer;
    private boolean waiting = false;
    private boolean checking_image = true;
    private ElapsedTime intake_timer;
    private ElapsedTime back_to_goal_timer;
    private ElapsedTime back_to_warehouse_timer;

    private boolean collecting_freight = false;

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
        startPose = new Pose2d(12, 0, Math.toRadians(180));
        drive.setPoseEstimate(startPose);
        log = new Logger(name);

        auto.init_camera();
        auto.init_lift();
        timer = new ElapsedTime();
        intake_timer = new ElapsedTime();
        back_to_goal_timer = new ElapsedTime();
        back_to_warehouse_timer = new ElapsedTime();
    }

    public void start(){
        robot.lineFinder.alpha_init = robot.lineFinder.line_finder.alpha();
    }

    @Override
    public void loop() {
        if (checking_image) {
            auto.check_image();
            if (!waiting) {timer.reset(); waiting = true;}
            if (timer.seconds() > 0.3) {
                if (auto.shipping_height < 1) auto.shipping_height = 3;
                waiting = false;
                checking_image = false;
                sequence = 1;//advances to first move
            }
        }

        if (!drive.isBusy()) {
            auto.dump_trigger = true;
        } else auto.dump_trigger = false;

        if (sequence == 1 && !drive.isBusy()) {
            ts1 = drive.trajectorySequenceBuilder(startPose)
                    .lineTo(new Vector2d(-12, 0))
                    .addDisplacementMarker(() -> {
                        if (auto.shipping_height < 1) {
                            auto.height = 3;
                        } else auto.height = auto.shipping_height;
                    })
                    .build();
            drive.followTrajectorySequenceAsync(ts1);
            sequence = 2;
        } else if (sequence == 2 && auto.lift_dumped && auto.lift_dumped_timer.seconds() > Status.AUTO_DUMP_DRIVE_OFFSET && !drive.isBusy()) {
            auto.lift_dumped = false;
            sequence = 3;
        } else if (sequence == 3) {
            drive.setDrivePower(new Pose2d(-0.7, 0.3, 0));
            robot.intake.setIntakeBack(1);
            back_to_warehouse_timer.reset();
            sequence = 4;
        } else if (sequence == 4) {
            if (back_to_warehouse_timer.seconds() > 1) {
                robot.intake.deposit(Status.DEPOSITS.get("back"));
                drive.setDrivePower(new Pose2d(-0.3, -0.1));
                if (robot.intake.freightDetected() || back_to_warehouse_timer.seconds() > 3) {
                    robot.intake.deposit(Status.DEPOSITS.get("carry"));
                    drive.setDrivePower(new Pose2d(0.7, 0.4, 0));
                    sequence = 5;
                    intake_timer.reset();
                }
            }
        } else if (sequence == 5) {
            //if (intake_timer.seconds() > Status.AUTO_INTAKE_DELAY) {
            robot.intake.setIntakeBack(-1);
            robot.intake.setIntakeFront(-1);
            //}
            if (robot.lineFinder.lineFound()) {
                back_to_goal_timer.reset();
                sequence = 6;
            }
        } else if (sequence == 6) {
            auto.height = 3;
            if (back_to_goal_timer.seconds() > 0.58) {
                drive.setDrivePower(new Pose2d(0, 0.2, 0));
                sequence = 7;
            }
        } else if (sequence == 7 && auto.lift_dumped && auto.lift_dumped_timer.seconds() > Status.AUTO_DUMP_DRIVE_OFFSET && !drive.isBusy()) {
            auto.lift_dumped = false;
            sequence = 8;
        } else if (sequence == 8) {
            drive.setDrivePower(new Pose2d(-0.7, 0.3, 0));
            robot.intake.setIntakeBack(1);
            back_to_warehouse_timer.reset();
            sequence = 9;
        } else if (sequence == 9) {
            if (back_to_warehouse_timer.seconds() > 1) {
                robot.intake.deposit(Status.DEPOSITS.get("back"));
                drive.setDrivePower(new Pose2d(-0.3, 0));
                if (robot.intake.freightDetected() || back_to_warehouse_timer.seconds() > 3.3) {
                    robot.intake.deposit(Status.DEPOSITS.get("carry"));
                    drive.setDrivePower(new Pose2d(0.7, 0.45, 0));
                    sequence = 10;
                    intake_timer.reset();
                }
            }
        } else if (sequence == 10) {
            //if (intake_timer.seconds() > Status.AUTO_INTAKE_DELAY) {
            robot.intake.setIntakeBack(-1);
            robot.intake.setIntakeFront(-1);
            //}
            if (robot.lineFinder.lineFound()) {
                back_to_goal_timer.reset();
                sequence = 11;
            }
        } else if (sequence == 11) {
            auto.height = 3;
            if (back_to_goal_timer.seconds() > 0.58) {
                drive.setDrivePower(new Pose2d(0, 0.2, 0));
                sequence = 12;
            }
        }  else if (sequence == 12 && auto.lift_dumped && auto.lift_dumped_timer.seconds() > Status.AUTO_DUMP_DRIVE_OFFSET && !drive.isBusy()) {
            auto.lift_dumped = false;
            sequence = 13;
        } else if (sequence == 13) {
            drive.setDrivePower(new Pose2d(-0.7, 0.3, 0));
            robot.intake.setIntakeBack(1);
            back_to_warehouse_timer.reset();
            sequence = 14;
        } else if (sequence == 14) {
            if (back_to_warehouse_timer.seconds() > 1) {
                robot.intake.deposit(Status.DEPOSITS.get("back"));
                drive.setDrivePower(new Pose2d(-0.35, 0.1));
                if (robot.intake.freightDetected() || back_to_warehouse_timer.seconds() > 3.5) {
                    robot.intake.deposit(Status.DEPOSITS.get("carry"));
                    drive.setDrivePower(new Pose2d(0.7, 0.4, 0));
                    sequence = 15;
                    intake_timer.reset();
                }
            }
        } else if (sequence == 15) {
            //if (intake_timer.seconds() > Status.AUTO_INTAKE_DELAY) {
            robot.intake.setIntakeBack(-1);
            robot.intake.setIntakeFront(-1);
            //}
            if (robot.lineFinder.lineFound()) {
                back_to_goal_timer.reset();
                sequence = 16;
            }
        } else if (sequence == 16) {
            auto.height = 3;
            if (back_to_goal_timer.seconds() > 0.58) {
                drive.setDrivePower(new Pose2d(0, 0.2, 0));
                sequence = 17;
            }
        } else if (sequence == 17 && auto.lift_dumped && auto.lift_dumped_timer.seconds() > Status.AUTO_DUMP_DRIVE_OFFSET && !drive.isBusy()) {
            auto.lift_dumped = false;
            sequence = 18;
        } else if (sequence == 18) {
            drive.setDrivePower(new Pose2d(-0.7, 0.3, 0));
            robot.intake.setIntakeFront(0);
            robot.intake.setIntakeBack(0);
            back_to_warehouse_timer.reset();
            sequence = 19;
        } else if (sequence == 19) {
            if (back_to_warehouse_timer.seconds() > 1) {
                robot.intake.deposit(Status.DEPOSITS.get("back"));
                drive.setDrivePower(new Pose2d(-0.35, 0));
                if (robot.intake.freightDetected() || back_to_warehouse_timer.seconds() > 2.7) {
                    robot.intake.deposit(Status.DEPOSITS.get("carry"));
                    drive.setDrivePower(new Pose2d(0, 0, 0));
                    sequence = 20;
                    intake_timer.reset();
                }
            }
        }

        //if (robot.lineFinder.lineFound()) {
        //    log.i("Line Finder Alpha Init: %03d", robot.lineFinder.alpha_init);
        //    log.i("Line Finder Alpha Value: %03d", robot.lineFinder.line_finder.alpha());
        //}

        auto.update();
        drive.update();
    }

    @Override
    public void stop() {
        auto.stop();
    }
}
