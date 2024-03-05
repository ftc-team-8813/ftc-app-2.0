package org.firstinspires.ftc.teamcode.hardware;

import android.util.Size;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import org.opencv.android.Camera2Renderer;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class Camera {
    private CameraName camera;
    private AprilTagProcessor apriltags;
    private VisionPortal portal;

    public Camera(CameraName camera){
        this.camera = camera;
        apriltags = AprilTagProcessor.easyCreateWithDefaults();
        this.portal = new VisionPortal.Builder()
            .setCamera(camera)
            .setCameraResolution(new Size(640, 480))
            .enableLiveView(true)
            .addProcessor(apriltags)
            .build();

    }

    public void allDetections(Telemetry telemetry){
        List<AprilTagDetection> detections = this.apriltags.getDetections();

        for(AprilTagDetection detection : detections){
            telemetry.addData("Detection", detection.id);
            telemetry.addData("Detection x", detection.ftcPose.x);
            telemetry.addData("Detection y", detection.ftcPose.y);
            telemetry.addData("Detection z", detection.ftcPose.z);
        }
    }

    public AprilTagDetection getDetection(int ID){
        List<AprilTagDetection> detections = this.apriltags.getDetections();

        for(AprilTagDetection detection : detections){
           if(detection.id == ID){
               return detection;
           }

        }
        return null;
    }


}
