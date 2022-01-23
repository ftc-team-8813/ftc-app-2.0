package org.firstinspires.ftc.teamcode.opmodes.test;

import android.graphics.Bitmap;
import android.graphics.ImageFormat;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.opmodes.LoggingOpMode;
import org.firstinspires.ftc.teamcode.opmodes.teleop.ControlMgr;
import org.firstinspires.ftc.teamcode.util.Logger;
import org.firstinspires.ftc.teamcode.util.Util;
import org.firstinspires.ftc.teamcode.util.websocket.InetSocketServer;
import org.firstinspires.ftc.teamcode.util.websocket.Server;
import org.firstinspires.ftc.teamcode.vision.CapstoneDetector;
import org.firstinspires.ftc.teamcode.vision.ImageDraw;
import org.firstinspires.ftc.teamcode.vision.webcam.Webcam;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.opencv.core.CvType.CV_8UC4;

@TeleOp(name = "Vision Test")
public class VisionTest extends LoggingOpMode
{
    private Webcam cam;

    private static final String serial = "3522DE6F";
    private Webcam.SimpleFrameHandler frameHandler;
    private Bitmap serverFrameCopy;
    private volatile boolean serverFrameUsed = true;
    private ByteBuffer drawBuffer;
    private volatile boolean drawDataUsed = false;
    private Server server;
    private Mat cvFrame;
    private ByteBuffer exTelemetry;
    private boolean telemDataUsed = false;

    private ControlMgr mgr;

    private Robot robot;

    private final Logger log = new Logger("Vision Test");

    static
    {
        OpenCVLoader.initDebug();
    }

    @Override
    public void init()
    {
        super.init();
        cam = Webcam.forSerial(serial);
        if (cam == null)
            throw new IllegalArgumentException("Could not find a webcam with serial number " + serial);
        frameHandler = new Webcam.SimpleFrameHandler();
        cam.open(ImageFormat.YUY2, 800, 448, 30, frameHandler);
        drawBuffer = ByteBuffer.allocate(65535);
        exTelemetry = ByteBuffer.allocate(8);

        try
        {
            server = new Server(new InetSocketServer(20000));
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        robot = Robot.initialize(hardwareMap, "Vision Test", 0);

        server.registerProcessor(0x01, (cmd, payload, resp) -> { // Get frame
            if (serverFrameCopy == null || serverFrameUsed) return;

            CapstoneDetector detector = new CapstoneDetector(cvFrame, log);
            double x_coord = detector.detect();
            log.i("X Coord: %f", x_coord);

//            Bitmap bmp = Bitmap.createBitmap(detector.stored_frame.cols(), detector.stored_frame.rows(), Bitmap.Config.ARGB_8888);
//            Utils.matToBitmap(detector.stored_frame, bmp);
            Bitmap bmp = Bitmap.createBitmap(cvFrame.cols(), cvFrame.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(cvFrame, bmp);

            ByteArrayOutputStream os = new ByteArrayOutputStream(16384);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, os); // probably quite slow
            serverFrameUsed = true;
            byte[] data = os.toByteArray();
            resp.respond(ByteBuffer.wrap(data));
        });
        server.registerProcessor(0x02, (cmd, payload, resp) -> { // Get draw overlay
            if (drawDataUsed) return;

            drawBuffer.flip();
            resp.respond(drawBuffer);
            drawDataUsed = true;
        });
        server.registerProcessor(0x03, (cmd, payload, resp) -> { // Get external telemetry
            if (telemDataUsed) return;

            exTelemetry.flip();
            resp.respond(exTelemetry);
            telemDataUsed = true;
        });
        cvFrame = new Mat(800, 448, CV_8UC4);

        server.startServer();
    }

    @Override
    public void loop()
    {
        if (frameHandler.newFrameAvailable)
        {
            frameHandler.newFrameAvailable = false;
            Utils.bitmapToMat(frameHandler.currFramebuffer, cvFrame);
            if (serverFrameUsed)
            {
                if (serverFrameCopy != null) serverFrameCopy.recycle();
                serverFrameCopy = frameHandler.currFramebuffer.copy(Bitmap.Config.ARGB_8888, false);
                serverFrameUsed = false;
            }
            ImageDraw draw = new ImageDraw();

            if (telemDataUsed)
            {
                exTelemetry.clear();
                exTelemetry.putDouble(0);
                telemDataUsed = false;
            }

            if (drawDataUsed)
            {
                drawBuffer.clear();
                draw.write(drawBuffer);
                drawDataUsed = false;
            }

            cam.requestNewFrame();
        }

        telemetry.addData("Camera status", cam.getStatus());
        telemetry.addData("Server status", server.getStatus());
        // telemetry.addData("Contour area", "%.3f", exTelemetry[0]);
    }

    @Override
    public void stop()
    {
        cam.close();
        server.close();
        super.stop();
    }
}
