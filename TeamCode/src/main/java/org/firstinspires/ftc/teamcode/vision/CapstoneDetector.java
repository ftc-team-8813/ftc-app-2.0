package org.firstinspires.ftc.teamcode.vision;

import org.firstinspires.ftc.teamcode.util.Logger;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CapstoneDetector {
    public Logger logger;
    public Mat detector_frame;
    public Mat stored_frame;
    public Scalar lower_hls;
    public Scalar upper_hls;
    public ArrayList<MatOfPoint> contours;

    public CapstoneDetector(Mat detector_frame, Logger logger){
        this.logger = logger;
        this.detector_frame = detector_frame;
        this.stored_frame = new Mat();
        this.lower_hls = new Scalar(40,110,0);
        this.upper_hls = new Scalar(60,130,255);
        contours = new ArrayList<>();
    }

    public int detect(){
        Mat resized = new Mat();
        Mat blurred = new Mat();
        Mat hls = new Mat();
        Mat masked = new Mat();
        Mat binary = new Mat();

        Imgproc.resize(detector_frame, resized, new Size(800, 400));
        Imgproc.blur(resized, blurred, new Size(5, 5));
        Imgproc.cvtColor(blurred, hls, Imgproc.COLOR_BGR2HLS);

        Core.inRange(hls, lower_hls, upper_hls, masked);
        logger.i("Original Type: %01d", detector_frame.type());
        logger.i("Masked Type: %01d", masked.type());
        Imgproc.findContours(masked, contours, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        logger.i("Contours Size: %02d", contours.size());

        stored_frame = masked;

        ArrayList<Double> areas = new ArrayList<>();
        for (int i = 0; i < contours.size(); i++){
            MatOfPoint contour = contours.get(i);
            double area = Imgproc.contourArea(contour);
            areas.add(area);
        }

        if (!areas.isEmpty()) {
            double max_area = Collections.max(areas);
            int index = areas.indexOf(max_area);

            Moments p = Imgproc.moments(contours.get(index), false);
            int x = (int) (p.get_m10() / p.get_m00());

            return x;
        }

        return -1;
    }
}
