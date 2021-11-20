package org.firstinspires.ftc.teamcode.vision;

import org.firstinspires.ftc.teamcode.util.Logger;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class CapstoneDetector {
    public Logger logger;
    public Mat detector_frame;
    public Scalar lower_hls;
    public Scalar upper_hls;
    public List<MatOfPoint> contours;

    public CapstoneDetector(Mat detector_frame){
        logger = new Logger("Capstone Detector");
        this.detector_frame = detector_frame;
        this.lower_hls = new Scalar(21,174,9);
        this.upper_hls = new Scalar(100,253,61);
        contours = new ArrayList<>();
    }

    public int detect(){
        Mat hls = new Mat();
        Mat bitwise = new Mat();
        Mat gray = new Mat();
        Mat binary = new Mat();
        Mat hierarchy = new Mat();

        Core.inRange(detector_frame, lower_hls, upper_hls, hls);
        Core.bitwise_and(hls, hls, bitwise);
        Imgproc.cvtColor(bitwise, gray, Imgproc.COLOR_BayerBG2GRAY);
        Imgproc.threshold(gray, binary, 100, 255, Imgproc.THRESH_BINARY);
        Imgproc.findContours(binary, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        double max_area = 0;
        int contour_id = 0;
        List<Moments> mu = new ArrayList<Moments>(contours.size());
        for (int contour_idx = 0; contour_idx < contours.size(); contour_idx++){
            double contour_area = Imgproc.contourArea(contours.get(contour_idx));
            if (contour_area > max_area){
                max_area = contour_area;
                contour_id = contour_idx;
            }
        }

        logger.i(String.format("Contour Area: %06f", max_area));
        mu.add(contour_id, Imgproc.moments(contours.get(contour_id), false));
        Moments p = mu.get(contour_id);
        int x = (int) (p.get_m10() / p.get_m00());

        return x;
    }
}
