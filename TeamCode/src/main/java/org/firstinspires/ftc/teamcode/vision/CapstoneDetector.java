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
import java.util.HashMap;
import java.util.List;

public class CapstoneDetector {
    private final Logger logger;
    private final Mat detector_frame;
    private Mat stored_frame;
    private final Scalar lower_hls;
    private final Scalar upper_hls;
    private ArrayList<MatOfPoint> contours;
    private String name = "Blue";
    private ArrayList<Double> zoned_areas;
    private ArrayList<int[]> zoned_areas_data;

    public CapstoneDetector(Mat detector_frame, Logger logger){
        this.logger = logger;
        this.detector_frame = detector_frame;
        this.stored_frame = new Mat();
        this.lower_hls = new Scalar(50,30,10);
        this.upper_hls = new Scalar(120,145,100);

        contours = new ArrayList<>();
        zoned_areas = new ArrayList<>();
        zoned_areas_data = new ArrayList<>();
    }

    public int[] detect(){
        Mat resized = new Mat();
        Mat blurred = new Mat();
        Mat hls = new Mat();
        Mat masked = new Mat();

        Imgproc.resize(detector_frame, resized, new Size(800, 400));
        Imgproc.blur(resized, blurred, new Size(5, 5));
        Imgproc.cvtColor(blurred, hls, Imgproc.COLOR_BGR2HLS);

        Core.inRange(hls, lower_hls, upper_hls, masked);
        Imgproc.findContours(masked, contours, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        stored_frame = masked;

        // Returns center of contour
        if (contours.isEmpty()) {
            return new int[]{0, -1};
        }

        for (int i = 0; i < contours.size(); i++){
            double area = Imgproc.contourArea(contours.get(i));
            Moments p = Imgproc.moments(contours.get(i), false);
            int x = (int) (p.get_m10() / p.get_m00());
            int y = (int) (p.get_m01() / p.get_m00());

            if (name.startsWith("Blue")) {
                if (75 < y && y < 132) {
                    int spot = 0;
                    if (139 < x && x < 193) {
                        spot = 1;
                    } else if (353 < x && x < 422) {
                        spot = 2;
                    } else if (539 < x && x < 593) {
                        spot = 3;
                    }

                    if (spot != 0) {
                        zoned_areas.add(area);
                        zoned_areas_data.add(new int[]{spot, x});
                    }
                }
            } else if (name.startsWith("Red")){
                // TODO Update with Red side boundaries
                if (70 < y && y < 127) {
                    int spot = 0;
                    if (135 < x && x < 187) {
                        spot = 1;
                    } else if (332 < x && x < 381) {
                        spot = 2;
                    } else if (526 < x && x < 571) {
                        spot = 3;
                    }

                    if (spot != 0) {
                        zoned_areas.add(area);
                        zoned_areas_data.add(new int[]{spot, x});
                    }
                }
            }
        }
        if (zoned_areas.isEmpty()) {
            return new int[]{0, -1};
        }

        double max_area = Collections.max(zoned_areas);
        return zoned_areas_data.get(zoned_areas.indexOf(max_area));
    }

    public Mat getStoredFrame(){
        return stored_frame;
    }

    public void setName(String name){
        this.name = name;
    }
}
