package org.firstinspires.ftc.teamcode.hardware.autoshoot;

import android.graphics.ImageFormat;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.IMU;
import org.firstinspires.ftc.teamcode.hardware.navigation.Odometry;
import org.firstinspires.ftc.teamcode.util.Logger;
import org.firstinspires.ftc.teamcode.vision.GoalDetector;
import org.firstinspires.ftc.teamcode.vision.webcam.Webcam;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import static org.opencv.core.CvType.CV_8UC4;

public class AutoAim
{
    private GoalDetector goalDetector;
    private Odometry odometry;
    private IMU imu;
    private double turretHome;
    private Logger log;

    private Webcam.SimpleFrameHandler frameHandler;
    private Webcam webcam;
    
    private double x_target;
    private double y_target;
    private double angle_off;

    private static final int DETECT_REQUEST_FRAME = 1;
    private static final int DETECT_PROCESS_FRAME = 2;
    public int detectStage = 0;
    public final double PIXEL2eUNIT = 65 * 0.00001;

    public AutoAim(){
        goalDetector = new GoalDetector();
        goalDetector.initializeWebcam();
        frameHandler = goalDetector.frameHandler;
        webcam = goalDetector.webcam;
    }

    public AutoAim(Odometry odometry, double turretHome){
        this.odometry = odometry;
        this.imu = odometry.getIMU();
        this.turretHome = turretHome;
        this.log = new Logger("Auto Aim");
    }
    
    public void setTarget(double xTarget, double yTarget)
    {
        this.x_target = xTarget;
        this.y_target = yTarget;
    }
    
    public double getTargetX()
    {
        return x_target;
    }
    
    public double getTargetY()
    {
        return y_target;
    }
    
    public void setAngleOffset(double angleOff)
    {
        this.angle_off = angleOff;
    }
    
    public double getAngleOffset()
    {
        return angle_off;
    }

    @Deprecated
    public double getTurretRotationOdo(Telemetry telemetry){
        double x_dist = x_target - odometry.x;
        double y_dist = y_target - odometry.y;

        // calculate target heading
        // CCW for imu is positive
        double robot_heading = imu.getHeading();
        double field_heading = Math.toDegrees(Math.atan2(y_dist, x_dist));
        
        double turret_heading = field_heading - robot_heading + 180;
        double rotation = turret_heading / 360.0;
        double rotation_pos = turretHome + rotation + angle_off;
        
        // wrap to between 0 and 1
        rotation_pos %= 1; // -1 to 1
        if (rotation_pos < 0) rotation_pos += 1; // 0 to 1

        telemetry.addData("Tracker Target Heading: ", turret_heading);
        telemetry.addData("Tracker Target Position: ", rotation_pos);
        
        return rotation_pos;
    }

    public double getTurretRotationVis(Telemetry telemetry) {
        Mat detectorFrame = new Mat(800, 448, CV_8UC4);
        if (detectStage == DETECT_REQUEST_FRAME) {
            goalDetector.frameHandler.newFrameAvailable = false;
            webcam.requestNewFrame();
            detectStage = DETECT_PROCESS_FRAME;
        } else if (detectStage == DETECT_PROCESS_FRAME && frameHandler.newFrameAvailable) {
            frameHandler.newFrameAvailable = false;
            detectStage = 0;
            Utils.bitmapToMat(frameHandler.currFramebuffer, detectorFrame);
            double pixel_turn = goalDetector.calcPixelTurn(detectorFrame, null);
            return pixel_turn * PIXEL2eUNIT;
        }
        return 0;
    }
}