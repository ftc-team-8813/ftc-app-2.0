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

    /**
     * Updates result to the masked input image
     */
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

    /**
     * Uses result to find determine ring position by looking for color boxes in certain regions
     * Should be run after processImage()
     * @return Number of rings
     */
    public int findRing(){
        Mat result = this.result;
        final int[] one_top_corner = new int[]{265, 210};
        final int[] one_bottom_corner = new int[]{279, 215};
        final int[] four_top_corner = new int[]{153, 165};
        final int[] four_bottom_corner = new int[]{270, 172};
        int one_total_values = (one_bottom_corner[0] - one_top_corner[0]) * (one_bottom_corner[1] - one_top_corner[1]);
        int four_total_values = (four_bottom_corner[0] - four_top_corner[0]) * (four_bottom_corner[1] - four_top_corner[1]);
        int aggregator = 0;
        for (int y = one_top_corner[0]; y <= one_bottom_corner[0]; y++){
            for (int x = one_top_corner[1]; x <= one_bottom_corner[1]; y++){
                if (!Arrays.equals(result.get(x, y), new double[]{0, 0, 0})){
                    aggregator++;
                }
            }
        }
        double one_or_none = aggregator/one_total_values;
        aggregator = 0;
        if (one_or_none >= 0.9){
            for (int y = four_top_corner[0]; y <= four_bottom_corner[0]; y++){
                for (int x = four_top_corner[1]; x <= four_bottom_corner[1]; y++){
                    if (!Arrays.equals(result.get(x, y), new double[]{0, 0, 0})){
                        aggregator++;
                    }
                }
            }
            double one_or_four = aggregator/four_total_values;
            if (one_or_four >= 0.9) {
                return 4;
            } else {
                return 1;
            }
        } else {
            return 0;
        }
    }
}
