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
        this.lower_range = new Scalar(98, 121, 48);
        this.upper_range = new Scalar(132, 155, 98);
        contours = new ArrayList<>();
    }

    public double detect(Mat detector_frame){
        Mat resized = new Mat();
        Mat blurred = new Mat();
        Mat rgb = new Mat();
        Mat masked = new Mat();
        Mat drawn = new Mat();

        Imgproc.resize(detector_frame, resized, new Size(800, 400));
        Imgproc.blur(resized, blurred, new Size(5, 5));
        Imgproc.cvtColor(blurred, rgb, Imgproc.COLOR_BGR2RGB);

        Core.inRange(rgb, lower_range, upper_range, masked);
        stored_frame = masked;
        Imgproc.findContours(masked, contours, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        ArrayList<Double> areas = new ArrayList<>();
        for (int i = 0; i < contours.size(); i++){
            MatOfPoint contour = contours.get(i);
            double y_coord = contour.get(0,0)[1];
            if (y_coord < 126 && y_coord > 56){
                double area = Imgproc.contourArea(contour);
                areas.add(area);
            }
        }

        if (!areas.isEmpty()) {
            double max_area = Collections.max(areas);
            int index = areas.indexOf(max_area);

            MatOfPoint contour = contours.get(index);
            Imgproc.cvtColor(masked, drawn, Imgproc.COLOR_GRAY2RGB);
            Imgproc.drawContours(drawn, contours, index, new Scalar(0, 255, 0), 2);
            stored_frame = drawn;
            double x_coord = contour.get(0,0)[0];

            return x_coord;
        }

        return -1;
    }
}
