package org.firstinspires.ftc.teamcode.vision;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.lang.reflect.Array;
import java.security.SecureClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RingDetector {
    Mat image;
    Mat result;

    public RingDetector(Mat image) {
        this.image = image;
    }

    /**
     * Updates result to the masked input image
     */
    public void processImage() {
        // TODO Scale image by 50%
        Mat hsv = new Mat();
        Imgproc.cvtColor(image, hsv, Imgproc.COLOR_BGR2HSV);

        Scalar lower_range = new Scalar(13, 50, 50);
        Scalar upper_range = new Scalar(255, 255, 255);
        Mat mask = new Mat();
        Core.inRange(hsv, lower_range, upper_range, mask);

        Mat masked = new Mat();
        Core.bitwise_and(mask, mask, masked);

        Mat bgr = new Mat();
        Imgproc.cvtColor(masked, bgr, Imgproc.COLOR_HSV2BGR);

        Mat gray = new Mat();
        Imgproc.cvtColor(bgr, gray, Imgproc.COLOR_BGR2GRAY);

        Mat blur = new Mat();
        Imgproc.blur(gray, blur, new Size(3, 3));

        Mat thresh = new Mat();
        Imgproc.threshold(gray, thresh, 0, 150, Imgproc.THRESH_BINARY);

        this.result = thresh;
    }

    /**
     * Uses result to determine ring position by looking for color boxes in certain regions
     * Should be run after processImage()
     *
     * @return Number of rings
     */
    public void findRing() {
        int four_ring_area = 4400;
        int one_ring_area = 1200;

        Mat thresh = this.result;

        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchey = new Mat();
        Imgproc.findContours(thresh, contours, hierarchey, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
    }
}
