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
public class RedWarehouseAutoRR extends LoggingOpMode
{
    private Robot robot;
    private Logger log;
    private AutonomousTemplate auto;
    private final String name = "Red Warehouse Auto RR";

    private SampleMecanumDrive drive;
    private TrajectorySequence ts1;
    private int sequence = 1;
    private Pose2d startPose;
    
    private boolean running_roadrunner = true;

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
        startPose = new Pose2d(12, 0, Math.toRadians(180));
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
        robot.lineFinder.alpha_init = robot.lineFinder.line_finder.alpha();
    }

    @Override
    public void loop() {
//        double start_time = loop_timer.seconds();
        auto.dump_trigger = !drive.isBusy();

        switch (sequence){
            case 1:
                ts1 = drive.trajectorySequenceBuilder(startPose)
                        .lineTo(new Vector2d(-12, 0))
                        .addTemporalMarker(0.25, () -> {
                            if (auto.shipping_height < 1) {
                                auto.height = 3;
                            } else auto.height = auto.shipping_height;
                        })
                        .build();
                drive.followTrajectorySequenceAsync(ts1);
                sequence = 2;
                break;
            case 2:
                if (!drive.isBusy()) {
                    robot.drivetrain.resetEncoders();
                    running_roadrunner = false;
                    sequence = 3;
                }
                break;
            case 3:
                drive.setDrivePower(new Pose2d(-0.7, 0.3, 0));
                robot.intake.setIntakeBack(1);
                back_to_goal_timer.reset();
                back_to_warehouse_timer.reset();
                sequence = 4;
                break;
            case 4:
                if (back_to_warehouse_timer.seconds() > 1.2) {
                    robot.intake.deposit(Status.DEPOSITS.get("back"));
                    drive.setDrivePower(new Pose2d(-0.17, -0.14));
                    if (robot.intake.freightDetected() || back_to_warehouse_timer.seconds() > 2.2) {
                        robot.intake.deposit(Status.DEPOSITS.get("back_tilt"));
                        if (back_to_warehouse_timer.seconds() > 2.5){
                            robot.intake.setIntakeBack(-1);
                            robot.intake.setIntakeFront(-1);
                            robot.drivetrain.resetEncoders();
                            if (back_to_warehouse_timer.seconds() < 3.3) {
                                drive.setDrivePower(new Pose2d(0.7, 0.45, 0.012));
                            } else {
                                sequence = 5;
                            }
                        }
                    }
                }
                break;
            case 5:
                drive.setDrivePower(new Pose2d(0.55,0.3,0));
                robot.intake.deposit(Status.DEPOSITS.get("carry"));
                if (robot.lineFinder.lineFound()) {
                    back_to_goal_timer.reset();
                    back_to_warehouse_timer.reset();
                    robot.drivetrain.resetEncoders();
                    sequence = 6;
                }
                break;
            case 6:
                if (auto.distance() < 15) {
                    if (auto.distance() > 5){
                        auto.height = 3;
                        auto.adjusted_cycle = 100;
                    }
                    drive.setDrivePower(new Pose2d(.6, .25, 0));
                } else {
                    robot.drivetrain.resetEncoders();
                    sequence = 8;
                }
                break;
            case 8:
                if (auto.distance() >= 15) {
                    drive.setDrivePower(new Pose2d(0, 0.2, 0));
                    back_to_goal_timer.reset();
                    sequence = 9;
                }
                break;
            case 9:
                if (back_to_goal_timer.seconds() > .85) {
                    robot.drivetrain.resetEncoders();
                    sequence = 10;
                }
                break;
            case 10:
                robot.drivetrain.resetEncoders();
                sequence = 11;
                break;
            case 11:
                drive.setDrivePower(new Pose2d(-0.7, 0.3, 0));
                robot.intake.setIntakeBack(1);
                back_to_warehouse_timer.reset();
                sequence = 12;
                break;
            case 12:
                if (back_to_warehouse_timer.seconds() > 1.1) {
                    robot.intake.deposit(Status.DEPOSITS.get("back"));
                    drive.setDrivePower(new Pose2d(-0.19, -0.13));
                    if (robot.intake.freightDetected() || back_to_warehouse_timer.seconds() > 2.5) {
                        robot.intake.deposit(Status.DEPOSITS.get("back_tilt"));
                        if (back_to_warehouse_timer.seconds() > 3){
                            robot.intake.setIntakeBack(-1);
                            robot.intake.setIntakeFront(-1);
                            robot.drivetrain.resetEncoders();
                            if (back_to_warehouse_timer.seconds() < 3.5) {
                                drive.setDrivePower(new Pose2d(0.6, 0.6, 0.012));
                            } else {
                                sequence = 13;
                            }
                        }
                    }
                }
                break;
            case 13:
                drive.setDrivePower(new Pose2d(0.55,0.3,0));
                robot.intake.deposit(Status.DEPOSITS.get("carry"));
                if (robot.lineFinder.lineFound()) {
                    back_to_goal_timer.reset();
                    back_to_warehouse_timer.reset();
                    robot.drivetrain.resetEncoders();
                    sequence = 14;
                }
                break;
            case 14:
                if (auto.distance() < 23) {
                    drive.setDrivePower(new Pose2d(.6, .25, 0));
                } else {
                    sequence = 15;
                }
                break;
            case 15:
                if (auto.distance() >= 5) {
                    auto.height = 3;
                    auto.adjusted_cycle = 100;
                    sequence = 16;
                }
                break;
            case 16:
                if (auto.distance() >= 23) {
                    drive.setDrivePower(new Pose2d(0, 0.2, 0));
                    back_to_goal_timer.reset();
                    sequence = 17;
                }
                break;
            case 17:
                if (back_to_goal_timer.seconds() > .85) {
                    robot.drivetrain.resetEncoders();
                    sequence = 18;
                }
                break;
            case 18:
                robot.drivetrain.resetEncoders();
                sequence = 19;
                break;
            case 19:
                drive.setDrivePower(new Pose2d(-0.7, 0.3, 0));
                robot.intake.setIntakeBack(1);
                back_to_warehouse_timer.reset();
                sequence = 20;
                break;
            case 20:
                if (back_to_warehouse_timer.seconds() > 1.23) {
                    robot.intake.deposit(Status.DEPOSITS.get("back"));
                    drive.setDrivePower(new Pose2d(-0.25, -0.19));
                    if (robot.intake.freightDetected() || back_to_warehouse_timer.seconds() > 2.45) {
                        robot.intake.deposit(Status.DEPOSITS.get("back_tilt"));
                        if (back_to_warehouse_timer.seconds() > 3){
                            robot.intake.deposit(Status.DEPOSITS.get("carry"));
                            robot.intake.setIntakeBack(-1);
                            robot.intake.setIntakeFront(-1);
                            robot.drivetrain.resetEncoders();
                            drive.setDrivePower(new Pose2d(0.6, 0.6, 0));
                            sequence = 21;
                        }
                    }
                }
                break;
            case 21:
                //if (intake_timer.seconds() > Status.AUTO_INTAKE_DELAY) {
                robot.intake.setIntakeBack(-1);
                robot.intake.setIntakeFront(-1);
                //}
                if (robot.lineFinder.lineFound()) {
                    back_to_goal_timer.reset();
                    robot.drivetrain.resetEncoders();
                    sequence = 22;
                }
                break;
            case 22:
                if (auto.distance() < 25) {
                    drive.setDrivePower(new Pose2d(.6, .4, 0));
                } else {
                    sequence = 23;
                }
                break;
            case 23:
                if (auto.distance() >= 5) {
                    auto.height = 3;
                    auto.adjusted_cycle = 100;
                    sequence = 24;
                }
                break;
            case 24:
                if (auto.distance() >= 25) {
                    drive.setDrivePower(new Pose2d(0, 0.2, 0));
                    back_to_goal_timer.reset();
                    sequence = 25;
                }
                break;
            case 25:
                if (back_to_goal_timer.seconds() > .85) {
                    robot.drivetrain.resetEncoders();
                    sequence = 26;
                }
                break;
            case 26:
                robot.drivetrain.resetEncoders();
                sequence = 27;
                break;
            case 27:
                drive.setDrivePower(new Pose2d(-0.7, 0.3, 0));
                back_to_warehouse_timer.reset();
                sequence = 28;
                break;
            case 28:
                if (back_to_warehouse_timer.seconds() > 1.25) {
                    robot.intake.deposit(Status.DEPOSITS.get("back"));
                    drive.setDrivePower(new Pose2d(-0.23, -0.13));
                    if (robot.intake.freightDetected() || back_to_warehouse_timer.seconds() > 2.8) {
                        robot.intake.deposit(Status.DEPOSITS.get("back_tilt"));
                        if (back_to_warehouse_timer.seconds() > 3.2){
                            robot.intake.deposit(Status.DEPOSITS.get("carry"));
                            robot.intake.setIntakeBack(-1);
                            robot.intake.setIntakeFront(-1);
                            robot.drivetrain.resetEncoders();
                            drive.setDrivePower(new Pose2d(0.6, 0.41, 0));
                            sequence = 29;
                        }
                    }
                }
                break;
            case 29:
                robot.intake.setIntakeBack(-1);
                robot.intake.setIntakeFront(-1);
                if (robot.lineFinder.lineFound()) {
                    back_to_goal_timer.reset();
                    robot.drivetrain.resetEncoders();
                    sequence = 30;
                }
                break;
            case 30:
                if (auto.distance() < 27) {
                    drive.setDrivePower(new Pose2d(.6, .4, 0));
                } else {
                    sequence = 31;
                }
                break;
            case 31:
                if (auto.distance() >= 5) {
                    auto.height = 3;
                    auto.adjusted_cycle = 100;
                    sequence = 32;
                }
                break;
            case 32:
                if (auto.distance() >= 27) {
                    drive.setDrivePower(new Pose2d(0, 0.2, 0));
                    back_to_goal_timer.reset();
                    sequence = 33;
                }
                break;
            case 33:
                if (back_to_goal_timer.seconds() > .85) {
                    robot.drivetrain.resetEncoders();
                    sequence = 34;
                }
                break;
            case 34:
                robot.drivetrain.resetEncoders();
                sequence = 35;
                break;
            case 35:
                drive.setDrivePower(new Pose2d(-0.7, 0.3, 0));
                back_to_warehouse_timer.reset();
                sequence = 36;
                break;
            case 36:
                if (back_to_warehouse_timer.seconds() > 1) {
                    robot.intake.deposit(Status.DEPOSITS.get("back"));
                    drive.setDrivePower(new Pose2d(-0.35, 0));
                    if (robot.intake.freightDetected() || back_to_warehouse_timer.seconds() > 2.2) {
                        robot.intake.deposit(Status.DEPOSITS.get("carry"));
                        drive.setDrivePower(new Pose2d(0, 0, 0));
                        sequence = 37;
                    }
                }
                break;
            }

        auto.update();
        if (running_roadrunner) {
            drive.update();
        }
//        log.i("Loop Time: %f", loop_timer.milliseconds());
//        loop_timer.reset();
        // Loop Times around 40ms, sometimes spikes to 90ms
    }

    @Override
    public void stop() {
        robot.intake.dist.close();
        auto.stop();
    }
}
