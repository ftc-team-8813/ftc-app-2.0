package org.firstinspires.ftc.teamcode.hardware.navigation;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ColorPipeline extends OpenCvPipeline {

    private int x1;
    private int y1;
    private int x2;
    private int y2;
    private String side;
    String location = "left";
    String color = "";
    String color_2 = "";
    String hsv_color = "";
    String hsv_color_2 = "";

    private Mat hsv = new Mat();

    public ColorPipeline(int x1, int y1, int x2, int y2, String side) {
        this.x1 = x1;
        this.y1 = y1;

        this.x2 = x2;
        this.y2 = y2;

        this.side = side;
    }

    @Override
    public Mat processFrame(Mat input) {
        Imgproc.cvtColor(input, hsv, Imgproc.COLOR_RGB2HSV);

        if (hsv.empty()) {
            location = "center";
            return input;
        }

        if (side.equals("far red")) {
            if (hsv.get(y1,x1)[1] > 200) {
                location = "center";
            }
            else if (hsv.get(y2,x2)[1] > 200) {
                location = "left";
            }
            else {
                location = "right";
            }
        }

        if (side.equals("far blue")) {
            if (hsv.get(y1,x1)[1] > 100) {
                location = "center";
            }
            else if (hsv.get(y2,x2)[1] > 100) {
                location = "right";
            }
            else {
                location = "left";
            }
        }

        if (side.equals("close blue")) {
            if (hsv.get(y1,x1)[1] > 100) {
                location = "center";
            }
            else if (hsv.get(y2,x2)[1] > 100) {
                location = "left";
            }
            else {
                location = "right";
            }
        }

        if (side.equals("close red")) {
            if (hsv.get(y1,x1)[1] > 100) {
                location = "center";
            }
            else if (hsv.get(y2,x2)[1] > 100) {
                location = "right";
            }
            else {
                location = "left";
            }
        }

        color = Arrays.toString(input.get(y1,x1));
        color_2 = Arrays.toString(input.get(y2,x2));

        hsv_color = Arrays.toString(hsv.get(y1,x1));
        hsv_color_2 = Arrays.toString(hsv.get(y2,x2));
        return input;
    }

    public String getLocation() {
        return this.location;
    }
    public String getColorValue() {
        return this.color;
    }
    public String getColorValue2() {
        return this.color_2;
    }
    public String getColorHSVValue() {
        return this.hsv_color;
    }
    public String getColorHSVValue2() {
        return this.hsv_color_2;
    }
//
//    public String getColorHSVValue() {
//        return this.hsv_color;
//    }
//    public String getColorHSVValue2() {
//        return this.hsv_color_2;
//    }
}




//

//        Rect leftRect = new Rect(1, 1, 319, 359);
//        Rect rightRect = new Rect(320, 1, 319, 359);
//
//        input.copyTo(output);
//        Imgproc.rectangle(output, leftRect, rectColor, 2);
//        Imgproc.rectangle(output, rightRect, rectColor, 2);
//
//        leftCrop = YCbCr.submat(leftRect);
//        rightCrop = YCbCr.submat(rightRect);
//
//        Core.extractChannel(leftCrop, leftCrop,2);
//        Core.extractChannel(rightCrop, rightCrop,2);
//
//        Scalar leftavg = Core.mean(leftCrop);
//        Scalar rightavg = Core.mean(rightCrop);
//
//        leftavgfin = leftavg.val[0];
//        rightavgfin = rightavg.val[0];
//
//        if(leftavgfin > rightavgfin){
//            location = "middle";
//        }else if(rightavgfin > leftavgfin){
//            location = "right";
//        }else{
//            location = "left";
//        }
//
//        avg = Math.abs(leftavgfin-rightavgfin);
//
//        return(output);
