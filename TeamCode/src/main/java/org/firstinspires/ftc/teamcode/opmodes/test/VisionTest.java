package org.firstinspires.ftc.teamcode.opmodes.test;

import android.graphics.Bitmap;
import android.graphics.ImageFormat;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.opmodes.LoggingOpMode;
import org.firstinspires.ftc.teamcode.util.websocket.Server;
import org.firstinspires.ftc.teamcode.vision.ImageDraw;
import org.firstinspires.ftc.teamcode.vision.RingDetector;
import org.firstinspires.ftc.teamcode.vision.webcam.Webcam;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

import static org.opencv.core.CvType.CV_8UC4;

@TeleOp(name="Vision Test")
public class VisionTest extends LoggingOpMode
{
    private Webcam cam;
    
    private static final String serial = "3522DE6F";
    private Webcam.SimpleFrameHandler frameHandler;
    private Bitmap serverFrameCopy;
    private volatile boolean serverFrameUsed = true;
    private ByteBuffer drawBuffer;
    private volatile boolean drawDataUsed = true;
    private Server server;
    private Mat cvFrame;
    private double[] exTelemetry = new double[1];
    
    private RingDetector detector;
    
    @Override
    public void init()
    {
        cam = Webcam.forSerial(serial);
        if (cam == null) throw new IllegalArgumentException("Could not find a webcam with serial number " + serial);
        frameHandler = new Webcam.SimpleFrameHandler();
        cam.open(ImageFormat.YUY2, 800, 448, 30, frameHandler);
        drawBuffer = ByteBuffer.allocate(65535);
    
        server = new Server(20000);
    
        server.registerProcessor(0x01, (cmd, payload, resp) -> { // Get frame
            if (serverFrameCopy == null || serverFrameUsed) return;
    
            ByteArrayOutputStream os = new ByteArrayOutputStream(16384);
            serverFrameCopy.compress(Bitmap.CompressFormat.JPEG, 80, os); // probably quite slow
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
            ByteBuffer outData = ByteBuffer.allocate(8 * exTelemetry.length);
            for (double d : exTelemetry)
            {
                outData.putDouble(d);
            }
            outData.flip();
            resp.respond(outData);
        });
        cvFrame = new Mat(800, 448, CV_8UC4);
        
        detector = new RingDetector(800, 448);
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
            // send Mat and ImageDraw to vision code
            double ringsArea = detector.detect(cvFrame, draw);
            exTelemetry[0] = ringsArea;
            
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
        
    }
    
    @Override
    public void stop()
    {
        cam.close();
        server.close();
        super.stop();
    }
}
