package org.firstinspires.ftc.teamcode.opmodes.test;

import android.graphics.ImageFormat;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.opmodes.LoggingOpMode;
import org.firstinspires.ftc.teamcode.util.Logger;
import org.firstinspires.ftc.teamcode.vision.webcam.Webcam;
import org.opencv.android.OpenCVLoader;

@TeleOp(name="ConeVisionTest")
public class ConeVisionTest extends LoggingOpMode {
    private Webcam camera;
    private Webcam.SimpleFrameHandler frameHandler;

    private final Logger log = new Logger("Cone Vision Test");

    static
    {
        OpenCVLoader.initDebug();
    }

    @Override
    public void init() {
        super.init();
        camera = Webcam.forSerial("3522DE6F");
        if (camera == null)
            throw new IllegalArgumentException("Could not find a webcam with serial number 3522DE6F");
        frameHandler = new Webcam.SimpleFrameHandler();
        camera.open(ImageFormat.YUY2, 800, 448, 30, frameHandler);
    }

    @Override
    public void loop() {

    }
}
