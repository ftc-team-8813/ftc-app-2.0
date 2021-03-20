package org.firstinspires.ftc.teamcode.opmodes.test;

import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.text.TextUtils;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.opmodes.LoggingOpMode;
import org.firstinspires.ftc.teamcode.util.Logger;
import org.firstinspires.ftc.teamcode.util.websocket.InetSocketServer;
import org.firstinspires.ftc.teamcode.util.websocket.Server;
import org.firstinspires.ftc.teamcode.vision.webcam.Webcam;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

@TeleOp(name="Websocket Test")
public class WebsocketTest extends LoggingOpMode
{
    private Server server;
    private Webcam[] connectedWebcams;
    private Logger log = new Logger("Websocket Test");
    
    private Webcam currWebcam;
    private Bitmap currFb;
    private volatile boolean newFrame = false;
    
    private class FrameHandler implements Webcam.FrameCallback
    {
    
        @Override
        public void setBuffer(Bitmap frameBuffer)
        {
            currFb = frameBuffer;
        }
    
        @Override
        public void onFrame(int droppedFrames)
        {
            newFrame = true;
        }
    
        @Override
        public void onClose(long lastFrameNum, int droppedFrames)
        {
        
        }
    
        @Override
        public void onError(String err)
        {
        
        }
    }
    
    @Override
    public void init()
    {
        try
        {
            server = new Server(new InetSocketServer(23456));
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        connectedWebcams = Webcam.getConnected();
        // register commands here
        server.registerProcessor(0x01, (cmd, payload, resp) -> { // GET_WEBCAMS
            String[] serials = new String[connectedWebcams.length];
            for (int i = 0; i < connectedWebcams.length; i++)
            {
                serials[i] = connectedWebcams[i].getSerialNumber();
            }
            String out = TextUtils.join(",", serials);
            byte[] data = out.getBytes(StandardCharsets.UTF_8);
            resp.respond(ByteBuffer.wrap(data));
        });
        server.registerProcessor(0x02, (cmd, payload, resp) -> { // OPEN_WEBCAM
            String out = "";
            if (currWebcam != null) out = "!Webcam already opened";
            else
            {
                byte[] payloadData = new byte[payload.remaining()];
                payload.get(payloadData);
                String serial = new String(payloadData, StandardCharsets.UTF_8);
                for (Webcam w : connectedWebcams)
                {
                    if (serial.equals(w.getSerialNumber()))
                    {
                        currWebcam = w;
                        break;
                    }
                }
                if (currWebcam == null) out = "!Bad serial number";
                else
                {
                    currWebcam.open(ImageFormat.YUY2, 800, 448, 30, new FrameHandler());
                    out = "OK";
                }
            }
            byte[] outData = out.getBytes(StandardCharsets.UTF_8);
            resp.respond(ByteBuffer.wrap(outData));
        });
        server.registerProcessor(0x03, (cmd, payload, resp) -> { // REQUEST_FRAME
            if (currWebcam == null || currFb == null || !newFrame) return; // respond with nothing
            newFrame = false;
            // push existing frame
            ByteArrayOutputStream os = new ByteArrayOutputStream(16384);
            currFb.compress(Bitmap.CompressFormat.JPEG, 80, os); // probably quite slow
            byte[] data = os.toByteArray();
            currWebcam.requestNewFrame();
            resp.respond(ByteBuffer.wrap(data));
        });
    }
    
    @Override
    public void start()
    {
        server.startServer();
    }
    
    @Override
    public void loop()
    {
        telemetry.addData("Server status", server.getStatus());
        if (currWebcam != null) telemetry.addData("Webcam status", currWebcam.getStatus());
    }
    
    @Override
    public void stop()
    {
        super.stop();
        server.close();
        if (currWebcam != null) currWebcam.close();
    }
}
