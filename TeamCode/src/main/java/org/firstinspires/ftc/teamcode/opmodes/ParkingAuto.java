//package org.firstinspires.ftc.teamcode.opmodes;
//
//import com.arcrobotics.ftclib.geometry.Pose2d;
//import com.arcrobotics.ftclib.geometry.Rotation2d;
//import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
//
//import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
//import org.firstinspires.ftc.teamcode.hardware.Drivetrain;
//import org.firstinspires.ftc.teamcode.hardware.Lift;
//import org.firstinspires.ftc.teamcode.hardware.navigation.Odometry;
//import org.firstinspires.ftc.teamcode.hardware.Robot;
//import org.firstinspires.ftc.teamcode.util.Logger;
//import org.firstinspires.ftc.teamcode.util.LoopTimer;
//import org.firstinspires.ftc.teamcode.vision.AprilTagDetectionPipeline;
//import org.openftc.apriltag.AprilTagDetection;
//import org.openftc.easyopencv.OpenCvCamera;
//import org.openftc.easyopencv.OpenCvCameraFactory;
//import org.openftc.easyopencv.OpenCvCameraRotation;
//
//import java.util.ArrayList;
//
//@Autonomous(name = "!! Parking Auto !!")
//public class ParkingAuto extends LoggingOpMode{
//
//    private Drivetrain drivetrain;
//    private Odometry odometry;
//    private Lift lift;
//
//    private String result = "Nothing";
//
//    private int main_id = 0;
//
//    private OpenCvCamera camera;
//    private AprilTagDetectionPipeline aprilTagDetectionPipeline;
//
//    private static final double FEET_PER_METER = 3.28084;
//
//    private double fx = 578.272;
//    private double fy = 578.272;
//    private double cx = 402.145;
//    private double cy = 221.506;
//
//    private double tagsize = 0.166;
//
//    private final Logger log = new Logger("Parking Auto");
//
//    @Override
//    public void init() {
//        super.init();
//        Robot robot = Robot.initialize(hardwareMap);
//        drivetrain = robot.drivetrain;
//        odometry = robot.odometry;
//        lift = robot.lift;
//
//        odometry.Down();
//
//        Pose2d start_pose = new Pose2d(0,0,new Rotation2d(Math.toRadians(0)));
//        odometry.updatePose(start_pose);
//
//        odometry.resetEncoders();
//
//        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
//        camera = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam 1"), cameraMonitorViewId);
//        aprilTagDetectionPipeline = new AprilTagDetectionPipeline(tagsize, fx, fy, cx, cy);
//
//        camera.setPipeline(aprilTagDetectionPipeline);
//        camera.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener()
//        {
//            @Override
//            public void onOpened()
//            {
//                camera.startStreaming(800,448, OpenCvCameraRotation.UPRIGHT);
//            }
//
//            @Override
//            public void onError(int errorCode)
//            {
//
//            }
//        });
//
////        telemetry.setMsTransmissionInterval(50);
//
//        lift.setHolderPosition(0.12);
//
//    }
//
//    @Override
//    public void init_loop() {
//        super.init_loop();
//
//        ArrayList<AprilTagDetection> currentDetections = aprilTagDetectionPipeline.getLatestDetections();
//
//        if(currentDetections.size() != 0) {
//
//            for (AprilTagDetection tag : currentDetections) {
//                if (tag.id == 107) {
//                    result = "FTC8813: 1";
//                    break;
//                }
//                else if (tag.id == 350) {
//                    result = "FTC8813: 2";
//                    break;
//                }
//                else if (tag.id == 25) {
//                    result = "FTC8813: 3";
//                    break;
//                }
//                else {
//                    result = "Nothing";
//                }
//
//            }
//        }
//
//
//        telemetry.addData("Detected", result);
//
//        telemetry.update();
//    }
//
//    @Override
//    public void start() {
//        super.start();
//        drivetrain.resetEncoders();
//        camera.closeCameraDevice();
//    }
//
//    @Override
//    public void loop() {
//
//        drivetrain.updateHeading();
//
//        odometry.updatePose(-drivetrain.getHeading());
//
//        switch (main_id) {
//            case 0:
//                drivetrain.autoMove(-28,0,0,1,1,10, odometry.getPose(), telemetry);
//                if (drivetrain.hasReached()) {
//                    main_id += 1;
//                }
//                break;
//            case 1:
//                switch (result) {
//                    case "FTC8813: 1":
//                        drivetrain.autoMove(-28,24,0,1,1,10, odometry.getPose(), telemetry);
//                        if (drivetrain.hasReached()) {
//                            main_id += 1;
//                        }
//                        break;
//                    case "FTC8813: 3":
//                        drivetrain.autoMove(-28,-24,0,1,1,10, odometry.getPose(),telemetry);
//                        if (drivetrain.hasReached()) {
//                            main_id += 1;
//                        }
//                        break;
//                    default:
//                        main_id += 1;
//                        break;
//                }
//                break;
//        }
//
//        drivetrain.update(odometry.getPose(), telemetry,false, main_id, false, false,0);
//
//        telemetry.addData("Loop Time: ", LoopTimer.getLoopTime());
//        telemetry.update();
//
//        LoopTimer.resetTimer();
//
//    }
//
//    @Override
//    public void stop() {
//        super.stop();
//    }
//
//}