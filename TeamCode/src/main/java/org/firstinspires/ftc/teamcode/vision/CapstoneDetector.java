package org.firstinspires.ftc.teamcode.vision;

import org.firstinspires.ftc.teamcode.util.Logger;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import java.util.ArrayList;
import java.util.Collections;

public class CapstoneDetector {
    public Logger logger;
    public Mat stored_frame;
    public Scalar lower_range;
    public Scalar upper_range;
    public ArrayList<MatOfPoint> contours;

    public CapstoneDetector(Logger logger){
        this.logger = logger;
        this.stored_frame = new Mat();
        this.lower_range = new Scalar(100,50,70);
        this.upper_range = new Scalar(150,150,105);
        contours = new ArrayList<>();
    }

    public int detect(Mat detector_frame){
        Mat resized = new Mat();
        Mat blurred = new Mat();
        Mat rgb = new Mat();
        Mat masked = new Mat();

        Imgproc.resize(detector_frame, resized, new Size(800, 400));
        Imgproc.blur(resized, blurred, new Size(5, 5));
        Imgproc.cvtColor(blurred, rgb, Imgproc.COLOR_BGR2RGB);

        Core.inRange(rgb, lower_range, upper_range, masked);
        stored_frame = masked;
        Imgproc.findContours(masked, contours, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

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
