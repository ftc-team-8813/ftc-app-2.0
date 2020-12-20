package org.firstinspires.ftc.teamcode.vision;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.security.SecureClassLoader;
import java.util.Arrays;

public class RingDetector {
    Mat image;
    Mat result;

    public RingDetector(Mat image){
        this.image = image;
    }

    public void processImage(){
        Mat hsv = new Mat();
        Imgproc.cvtColor(image, hsv, Imgproc.COLOR_BGR2HSV);

        Scalar lower_range = new Scalar(13, 50, 50);
        Scalar upper_range = new Scalar(255, 255, 255);
        Mat mask = new Mat();
        Core.inRange(hsv, lower_range, upper_range, mask);

        Mat masked = new Mat();
        Core.bitwise_and(mask, mask, masked);

        Mat result = new Mat();
        Imgproc.cvtColor(masked, result, Imgproc.COLOR_HSV2BGR);

        this.result = result;
    }

    public boolean findRing(){
        Mat result = this.result;
        final int[] top_left = new int[]{265, 210};
        final int[] bottom_right = new int[]{279, 215};
        int total_values = (bottom_right[0] - top_left[0]) * (bottom_right[1] - top_left[1]);
        int aggregator = 0;
        for (int y = top_left[0]; y <= bottom_right[0]; y++){
            for (int x = top_left[1]; x <= bottom_right[1]; y++){
                if (!Arrays.equals(result.get(x, y), new double[]{0, 0, 0})){
                    aggregator++;
                }
            }
        }
        this.result = null;
        return aggregator == total_values;
    }
}
