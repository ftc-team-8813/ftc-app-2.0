package org.firstinspires.ftc.teamcode.vision;

import android.graphics.ImageFormat;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.util.Logger;
import org.firstinspires.ftc.teamcode.vision.webcam.Webcam;
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

public class GoalDetector {
    public Webcam.SimpleFrameHandler frameHandler;
    public Webcam webcam;
    private Logger log;

    private static final String WEBCAM_SERIAL = "AABFDF4E";

    private Mat workImg;
    private Mat workImg2;
    private Mat workImg3;
    private Mat workImg4;
    private Mat binaryImg;
    public Mat contoured;

    private final int width = 800;
    private final int height = 448;
    private static final Scalar minColor = new Scalar(105,  50,  100);
    private static final Scalar maxColor = new Scalar(115, 100, 255);


    public GoalDetector(){
        log = new Logger("Goal Detector");

        workImg = new Mat(width, height, CvType.CV_8UC3);
        workImg2 = new Mat(width, height, CvType.CV_8UC3);
        workImg3 = new Mat(width, height, CvType.CV_8UC3);
        workImg4 = new Mat(width, height, CvType.CV_8UC3);
        binaryImg = new Mat(width, height, CvType.CV_8UC1);
        contoured = new Mat(width, height, CvType.CV_8UC3);
    }


    public void initializeWebcam(){
        webcam = Webcam.forSerial(WEBCAM_SERIAL);
        if (webcam == null) throw new IllegalArgumentException("Could not find a webcam with serial number " + WEBCAM_SERIAL);
        frameHandler = new Webcam.SimpleFrameHandler();
        webcam.open(ImageFormat.YUY2, 800, 448, 30, frameHandler);
        frameHandler.newFrameAvailable = false;
    }

    public double calcPixelTurn(Mat inputImg, ImageDraw draw){
        Imgproc.resize(inputImg, workImg, new Size(800, 448));
        Imgproc.cvtColor(workImg, workImg2, Imgproc.COLOR_RGBA2BGR);
        Imgproc.cvtColor(workImg2, workImg3, Imgproc.COLOR_BGR2HLS);
        Imgproc.blur(workImg3, workImg4, new Size(1, 1));

        Core.inRange(workImg4, minColor, maxColor, binaryImg);

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

        try {
            MatOfPoint biggest_contour = contours.get(maxContourIndex);
            List<Point> corners = getCornersFromPoints(biggest_contour.toList());
            if (draw != null){
                Point[] points = biggest_contour.toArray();
                ImageDraw.Point[] contourPoints = ImageDraw.Point.fromContour(points);
                draw.draw(new ImageDraw.Lines(ImageDraw.GREEN, 2, contourPoints));
            }
            return 400 - ((corners.get(0).x + corners.get(3).x)/2);
        }
        catch (ArrayIndexOutOfBoundsException e){
            return 0;
        }
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
