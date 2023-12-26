package org.firstinspires.ftc.teamcode.opmodes.auto;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.opmodes.LoggingOpMode;
import org.firstinspires.ftc.teamcode.vision.AprilTagDetectionPipeline;
import org.openftc.apriltag.AprilTagDetection;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;

import java.util.ArrayList;

public class CameraTest extends LoggingOpMode {


    private OpenCvCamera camera;
    private AprilTagDetectionPipeline aprilTagDetectionPipeline;
    private String result = "Nothing";
    private double fx = 578.272;
    private double fy = 578.272;
    private double cx = 402.145;
    private double cy = 221.506;
    private double tagsize = 0.166;


    @Override
    public void init() {
        super.init();
//        Robot robot = Robot.initialize(hardwareMap);
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        camera = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam 1"), cameraMonitorViewId);
        aprilTagDetectionPipeline = new AprilTagDetectionPipeline(tagsize, fx, fy, cx, cy);

        camera.setPipeline(aprilTagDetectionPipeline);
        camera.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {

            }

            @Override
            public void onError(int errorCode) {

            }
        });
    }

    @Override
    public void init_loop() {
        super.init_loop();

        ArrayList<org.openftc.apriltag.AprilTagDetection> currentDetections = aprilTagDetectionPipeline.getLatestDetections();

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

            telemetry.addData("Detected", result);

            telemetry.update();
        }


        telemetry.addData("Detected", result);

        telemetry.update();
    }

    @Override
    public void loop() {

    }
}
