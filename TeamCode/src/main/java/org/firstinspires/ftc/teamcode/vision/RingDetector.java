package org.firstinspires.ftc.teamcode.vision;

import org.firstinspires.ftc.teamcode.vision.ImageDraw.Color;
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

public class RingDetector
{
    private Mat workImg;
    private Mat workImg2;
    private Mat binaryImg;
    
    private static final Scalar minColor = new Scalar(3, 18, 41);
    private static final Scalar maxColor = new Scalar(19, 231, 255);
    
    private int cutoffY;
    
    public RingDetector(int width, int height)
    {
        cutoffY = (int) (height * 0.5);
        workImg = new Mat(width, height - cutoffY, CvType.CV_8UC3);
        workImg2 = new Mat(width, height - cutoffY, CvType.CV_8UC3);
        binaryImg = new Mat(width, height - cutoffY, CvType.CV_8UC1);
    }
    
    public double detect(Mat inputImg, ImageDraw draw)
    {
        inputImg.submat(cutoffY, inputImg.height(), 0, inputImg.width()).copyTo(workImg);
        Imgproc.cvtColor(workImg, workImg, Imgproc.COLOR_RGBA2BGR);
        Imgproc.cvtColor(workImg, workImg2, Imgproc.COLOR_BGR2HLS);
        Imgproc.blur(workImg2, workImg, new Size(5, 5));
        
        Core.inRange(workImg, minColor, maxColor, binaryImg);
        
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(binaryImg, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        hierarchy.release();
        
        double maxArea = -1;
        int maxContourIndex = -1;
        
        for (int i = 0; i < contours.size(); i++)
        {
            double area = Imgproc.contourArea(contours.get(i));
            if (area > maxArea)
            {
                maxContourIndex = i;
                maxArea = area;
            }
        }
        
        if (draw != null)
        {
            Color notChosen = ImageDraw.RED;
            Color chosen = ImageDraw.GREEN;
            for (int i = 0; i < contours.size(); i++)
            {
                Color c = notChosen;
                if (i == maxContourIndex) c = chosen;
                MatOfPoint contour = contours.get(i);
                Point[] points = contour.toArray();
                for (Point p : points)
                {
                    p.y += cutoffY;
                }
                ImageDraw.Point[] contourPoints = ImageDraw.Point.fromContour(points);
                if (contourPoints.length >= 2) draw.draw(new ImageDraw.Lines(c, 2, contourPoints));
            }
        }
        
        return maxArea;
    }
}
