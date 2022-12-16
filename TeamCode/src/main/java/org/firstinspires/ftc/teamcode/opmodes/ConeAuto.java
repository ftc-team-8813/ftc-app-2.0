package org.firstinspires.ftc.teamcode.opmodes;

import com.arcrobotics.ftclib.geometry.Pose2d;
import com.arcrobotics.ftclib.geometry.Rotation2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.hardware.Drivetrain;
import org.firstinspires.ftc.teamcode.hardware.Intake;
import org.firstinspires.ftc.teamcode.hardware.Lift;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.hardware.navigation.OdometryNav;
import org.firstinspires.ftc.teamcode.hardware.navigation.PID;
import org.firstinspires.ftc.teamcode.util.Logger;
import org.firstinspires.ftc.teamcode.util.LoopTimer;
import org.firstinspires.ftc.teamcode.vision.AprilTagDetectionPipeline;
import org.openftc.apriltag.AprilTagDetection;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

import java.util.ArrayList;

@Autonomous(name = "Cone Auto")
public class ConeAuto extends LoggingOpMode{

    private Drivetrain drivetrain;
    private Lift lift;
    private Intake intake;
    private OdometryNav odometry;

    private String result = "Nothing";

    private int main_id = 0;

    private OpenCvCamera camera;
    private AprilTagDetectionPipeline aprilTagDetectionPipeline;

    private static final double FEET_PER_METER = 3.28084;

    private double fx = 578.272;
    private double fy = 578.272;
    private double cx = 402.145;
    private double cy = 221.506;

    private double tagsize = 0.166;

    private final PID arm_PID = new PID(0.1, 0, 0, 0, 0, 0);
    private final PID horizontal_PID = new PID(0.1, 0, 0, 0, 0, 0);
    private final PID lift_PID = new PID(0.02, 0, 0, 0, 0, 0);

    private ElapsedTime timer = new ElapsedTime();

    private double lift_target = 0;
    private double horizontal_target = 0;
    private double arm_target = 0;

    private final Logger log = new Logger("Cone Auto");

    @Override
    public void init() {
        super.init();
        Robot robot = Robot.initialize(hardwareMap);
        drivetrain = robot.drivetrain;
        lift = robot.lift;
        intake = robot.intake;
        odometry = robot.odometryNav;

        Pose2d start_pose = new Pose2d(0,0,new Rotation2d(Math.toRadians(0)));
        odometry.updatePose(start_pose);

        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        camera = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam 1"), cameraMonitorViewId);
        aprilTagDetectionPipeline = new AprilTagDetectionPipeline(tagsize, fx, fy, cx, cy);

        camera.setPipeline(aprilTagDetectionPipeline);
        camera.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener()
        {
            @Override
            public void onOpened()
            {
                camera.startStreaming(800,448, OpenCvCameraRotation.UPRIGHT);
            }

            @Override
            public void onError(int errorCode)
            {

            }
        });

        telemetry.setMsTransmissionInterval(50);


    }

    @Override
    public void init_loop() {
        super.init_loop();

        ArrayList<AprilTagDetection> currentDetections = aprilTagDetectionPipeline.getLatestDetections();

        if(currentDetections.size() != 0) {

            for (AprilTagDetection tag : currentDetections) {
                if (tag.id == 107) {
                    result = "FTC8813: 1";
                    break;
                }
                else if (tag.id == 350) {
                    result = "FTC8813: 2";
                    break;
                }
                else if (tag.id == 25) {
                    result = "FTC8813: 3";
                    break;
                }
                else {
                    result = "Nothing";
                }

            }
        }

//        intake.setArmPow(0.3);
//        lift.setLiftPower(0.3);
//        intake.setHorizPow(0.3);
//
//        if(intake.getArmLimit() && intake.getHorizLimit() && lift.getLift_limit()){
//            lift.resetLiftEncoder();
//            intake.resetIntakeEncoders();
//        }


        telemetry.addData("Detected", result);

        telemetry.update();
    }

    @Override
    public void start() {
        super.start();
        drivetrain.resetEncoders();
//        lift_target = 660; //change
    }

    @Override
    public void loop() {

        odometry.updatePose();

        switch (main_id) {
            case 0:
                drivetrain.autoMove(-24,-26,90,0,1,1,7,odometry.getPose(),telemetry);
                if (drivetrain.hasReached()) {
                    main_id += 1;
                }
                break;
            case 1:
                drivetrain.autoMove(-24,-29,90,0,1,1,7,odometry.getPose(),telemetry);
                if (drivetrain.hasReached()) {
                    main_id += 1;
                }
                break;
            case 2:
                drivetrain.autoMove(-36,-29,90,0,1,1,7,odometry.getPose(),telemetry);
                if (drivetrain.hasReached()) {
                    main_id += 1;
//                    timer.reset();
                }
                break;
            case 3:
                drivetrain.stop();
                break;

            case -2:
                drivetrain.autoMove(-36,-24,90,0,1,1,1,odometry.getPose(),telemetry);
                lift.setDumper(0); // change
                if (timer.seconds() > 1) {
                    lift.setDumper(1); //change
                }
                break;
            case -10:
                drivetrain.autoMove(-24,0,0,0,1,1,7, odometry.getPose(),telemetry);
                if (drivetrain.hasReached()) {
                    main_id += 1;
                }
                break;
            case -1:
                switch (result) {
                    case "FTC8813: 1":
                        drivetrain.autoMove(-24,24,0,0,1,1,7, odometry.getPose(),telemetry);
                        if (drivetrain.hasReached()) {
                            main_id += 1;
                        }
                        break;
                    case "FTC8813: 3":
                        drivetrain.autoMove(-24,-24,0,0,1,1,7, odometry.getPose(),telemetry);
                        if (drivetrain.hasReached()) {
                            main_id += 1;
                        }
                        break;
                    default:
                        main_id += 1;
                        break;
                }
                break;
            case -3:
                drivetrain.stop();
                break;
        }


//        double lift_power = lift_PID.getOutPut(lift_target,lift.getEncoderVal(),1); //change
//        double horizontal_power = lift_PID.getOutPut(horizontal_target,intake.getHorizCurrent(),0); //change
//        double arm_power = lift_PID.getOutPut(arm_target,intake.getArmCurrent(),Math.cos(Math.toRadians(intake.getArmCurrent() + 0))); //change
//
//
//        lift.setLiftPower(lift_power); //change

        telemetry.addData("Loop Time: ", LoopTimer.getLoopTime());
        telemetry.update();

        LoopTimer.resetTimer();

    }

    @Override
    public void stop() {
        super.stop();
    }

}