package org.firstinspires.ftc.teamcode.hardware.autoshoot;

import android.graphics.ImageFormat;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.IMU;
import org.firstinspires.ftc.teamcode.hardware.navigation.Odometry;
import org.firstinspires.ftc.teamcode.util.Logger;
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
    private Odometry odometry;
    private IMU imu;
    private double turretHome;
    
    private double x_target;
    private double y_target;
    private double angle_off;
    private Logger log;

    private Mat detectorFrame;
    private Webcam.SimpleFrameHandler frameHandler;
    private Webcam webcam;

    private static final int DETECT_REQUEST_FRAME = 1;
    private static final int DETECT_PROCESS_FRAME = 2;
    private int detectStage = 0;
    private static final double PIXEL2eUNIT = 0.001;

    private Mat workImg;
    private Mat workImg2;
    private Mat binaryImg;
    private Mat contoured;

    private final int width = 800;
    private final int height = 448;
    private int cutoffY;
    private static final Scalar minColor = new Scalar(0,  50,  150);
    private static final Scalar maxColor = new Scalar(20, 100, 255);

    private static final String WEBCAM_SERIAL = "3522DE6F";

    public AutoAim(Odometry odometry, double turretHome){
        this.odometry = odometry;
        this.imu = odometry.getIMU();
        this.turretHome = turretHome;
        this.log = new Logger("Auto Aim");

        workImg = new Mat(width, height, CvType.CV_8UC3);
        workImg2 = new Mat(width, height, CvType.CV_8UC3);
        binaryImg = new Mat(width, height, CvType.CV_8UC1);
        contoured = new Mat(width, height, CvType.CV_8UC3);
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

    public double getTurretRotationVis(Telemetry telemetry){
        webcam = Webcam.forSerial(WEBCAM_SERIAL);
        if (webcam == null) throw new IllegalArgumentException("Could not find a webcam with serial number " + WEBCAM_SERIAL);
        frameHandler = new Webcam.SimpleFrameHandler();
        webcam.open(ImageFormat.YUY2, 800, 448, 30, frameHandler);
        detectorFrame = new Mat(800, 448, CV_8UC4);

        if (detectStage == DETECT_REQUEST_FRAME){
            frameHandler.newFrameAvailable = false;
            webcam.requestNewFrame();
            detectStage = DETECT_PROCESS_FRAME;
        } else if (detectStage == DETECT_PROCESS_FRAME && frameHandler.newFrameAvailable){
            frameHandler.newFrameAvailable = false;
            detectStage = 0;
            Utils.bitmapToMat(frameHandler.currFramebuffer, detectorFrame);
        }

        double pixel_turn = calcPixelTurn(detectorFrame);
        return pixel_turn * PIXEL2eUNIT;
    }

    public double calcPixelTurn(Mat inputImg){
        Imgproc.resize(workImg, workImg, new Size(800, 448));
        Imgproc.cvtColor(workImg, workImg, Imgproc.COLOR_RGBA2BGR);
        Imgproc.cvtColor(workImg, workImg2, Imgproc.COLOR_BGR2HLS);
        Imgproc.blur(workImg2, workImg, new Size(1, 1));

        Core.inRange(workImg, minColor, maxColor, binaryImg);

        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(binaryImg, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        hierarchy.release();

        double maxArea = -1;
        int maxContourIndex = -1;
        for (int i = 0; i < contours.size(); i++){
            double area = Imgproc.contourArea(contours.get(i));
            if (area > maxArea)
            {
                maxContourIndex = i;
                maxArea = area;
            }
        }
        contoured = workImg;
        Imgproc.drawContours(contoured, contours, maxContourIndex, new Scalar(0, 255, 0), 2);

        final MatOfPoint biggest = contours.get(maxContourIndex);
        List<Point> corners = getCornersFromPoints(biggest.toList());
        return 400 - ((corners.get(0).x + corners.get(3).x)/2);
    }

    private List<Point> getCornersFromPoints(final List<Point> points) {
        double minX = 0;
        double minY = 0;
        double maxX = 0;
        double maxY = 0;

        for (Point point : points) {
            double x = point.x;
            double y = point.y;

            if (minX == 0 || x < minX) {
                minX = x;
            }
            if (minY == 0 || y < minY) {
                minY = y;
            }
            if (maxX == 0 || x > maxX) {
                maxX = x;
            }
            if (maxY == 0 || y > maxY) {
                maxY = y;
            }
        }

        List<Point> corners = new ArrayList<>(4);
        corners.add(new Point(minX, minY));
        corners.add(new Point(minX, maxY));
        corners.add(new Point(maxX, minY));
        corners.add(new Point(maxX, maxY));

        return corners;
    }
}