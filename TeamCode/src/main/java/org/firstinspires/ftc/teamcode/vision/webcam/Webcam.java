package org.firstinspires.ftc.teamcode.vision.webcam;

import android.graphics.Bitmap;

import com.qualcomm.robotcore.util.ThreadPool;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.android.util.Size;
import org.firstinspires.ftc.robotcore.external.function.Continuation;
import org.firstinspires.ftc.robotcore.external.hardware.camera.Camera;
import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraCaptureRequest;
import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraCaptureSession;
import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraCharacteristics;
import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraException;
import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraManager;
import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.util.Logger;
import org.firstinspires.ftc.teamcode.util.event.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

// TODO: WORK IN PROGRESS
public class Webcam
{
    public static final int NEVER_OPENED = 0;
    public static final int OPENING = 1;
    public static final int OPENED = 2;
    public static final int RUNNING = 3;
    public static final int CLOSED = -1;
    public static final int ERROR = -2;
    
    private WebcamName name;
    private CameraCharacteristics characteristics; // this is not cached internally; cache it here
    private volatile int state = NEVER_OPENED;
    private volatile String error = null;
    private volatile boolean frameProcessed = true;
    private volatile boolean stopCapture = false;
    
    private Logger log = new Logger("Webcam");
    
    private static HashMap<Integer, String> formats;
    private static HashMap<Integer, String> statuses;
    
    static
    {
        statuses = new HashMap<>();
        statuses.put(-2, "Error");
        statuses.put(-1, "Closed");
        statuses.put(0, "Never Opened");
        statuses.put(1, "Opening");
        statuses.put(2, "Starting");
        statuses.put(3, "Running");
        
        formats = new HashMap<>();
        formats.put(1144402265, "IF_DEPTH16");
        formats.put(1768253795, "IF_DEPTH_JPEG");
        formats.put(257, "IF_DEPTH_POINT_CLOUD");
        formats.put(42, "IF_FLEX_RGBA_8888");
        formats.put(41, "IF_FLEX_RGB_888");
        formats.put(1212500294, "IF_HEIC");
        formats.put(256, "IF_JPEG");
        formats.put(16, "IF_NV16");
        formats.put(17, "IF_NV21");
        formats.put(34, "IF_PRIVATE");
        formats.put(37, "IF_RAW10");
        formats.put(38, "IF_RAW12");
        formats.put(36, "IF_RAW_PRIVATE");
        formats.put(32, "IF_RAW_SENSOR");
        formats.put(4, "IF_RGB_565");
        formats.put(0, "IF_UNKNOWN");
        formats.put(538982489, "IF_Y8");
        formats.put(35, "IF_YUV_420_888");
        formats.put(39, "IF_YUV_422_888");
        formats.put(40, "IF_YUV_444_888");
        formats.put(20, "IF_YUY2");
        formats.put(842094169, "IF_YV12");
        
        formats.put(8, "PF_A8"); // deprecated pixel format
        // 256 -> PF_JPEG -- already covered by IF_JPEG
        formats.put(10, "PF_LA_88"); // deprecated pixel format
        formats.put(9, "PF_L_8"); // deprecated pixel format
        formats.put(-1, "PF_OPAQUE");
        formats.put(43, "PF_RGBA_1010102");
        formats.put(7, "PF_RGBA_4444"); // deprecated
        formats.put(6, "PF_RGBA_5551"); // deprecated
        formats.put(1, "PF_RGBA_8888");
        formats.put(22, "PF_RGBA_F16");
        formats.put(2, "PF_RGBX_8888");
        formats.put(11, "PF_RGB_332"); // deprecated
        // 4 -> PF_RGB_565 -- already covered
        formats.put(3, "PF_RGB_888");
        formats.put(-3, "PF_TRANSLUCENT");
        formats.put(-2, "PF_TRANSPARENT");
        // NV12/16/YUV422 already covered
    }
    
    public static Webcam[] getConnected()
    {
        Logger log = new Logger("Webcam Initializer");
        CameraManager mgr = ClassFactory.getInstance().getCameraManager();
        List<WebcamName> names = mgr.getAllWebcams();
        log.i("Found %d webcams", names.size());
        Webcam[] cameras = new Webcam[names.size()];
        for (int i = 0; i < names.size(); i++)
        {
            cameras[i] = new Webcam(names.get(i));
        }
        return cameras;
    }
    
    public static Webcam forSerial(String serial)
    {
        Webcam[] cams = getConnected();
        for (Webcam w : cams)
        {
            if (w.getSerialNumber().equals(serial)) return w;
        }
        return null;
    }
    
    private Webcam(WebcamName name)
    {
        this.name = name;
        this.characteristics = name.getCameraCharacteristics();
        log.v("Webcam s/n %s at %s:", name.getSerialNumber(), name.getConnectionInfo());
        for (CameraCharacteristics.CameraMode mode : characteristics.getAllCameraModes())
        {
            String defFlag = mode.isDefaultSize ? " [default size]" : "";
            String fmtName = formats.get(mode.androidFormat);
            log.v("- available mode: %s at %dx%d%s at %d fps",
                    fmtName, mode.size.getWidth(), mode.size.getHeight(), defFlag, mode.fps);
        }
        log.v("");
    }
    
    private int lastState = state;
    
    public void loop(EventBus bus)
    {
        if (state != lastState)
        {
            lastState = state;
            bus.pushEvent(new WebcamEvent(state));
        }
    }
    
    public interface FrameCallback
    {
        void setBuffer(Bitmap frameBuffer);
        
        void onFrame(int droppedFrames);
        
        void onClose(long lastFrameNum, int droppedFrames);
        
        void onError(String err);
    }
    
    public static class SimpleFrameHandler implements FrameCallback
    {
        public Bitmap currFramebuffer = null;
        public volatile boolean newFrameAvailable = false;
        public volatile boolean closed = false;
        
        @Override
        public void setBuffer(Bitmap frameBuffer)
        {
            currFramebuffer = frameBuffer;
        }
        
        @Override
        public void onFrame(int droppedFrames)
        {
            newFrameAvailable = true;
        }
        
        @Override
        public void onClose(long lastFrameNum, int droppedFrames)
        {
            closed = true;
        }
        
        @Override
        public void onError(String err)
        {
            closed = true;
        }
    }
    
    public String getSerialNumber()
    {
        return name.getSerialNumber().toString();
    }
    
    public int[] getAvailableFormats()
    {
        return characteristics.getAndroidFormats();
    }
    
    public void requestNewFrame()
    {
        frameProcessed = true;
    }
    
    public String getStatus()
    {
        String status = statuses.get(state);
        if (state == ERROR) status += " -- " + error;
        return status;
    }
    
    public int getState()
    {
        return state;
    }
    
    public void open(int format, int w, int h, int fps, FrameCallback cb)
    {
        log.i("Attempting to open camera asynchronously");
        state = OPENING;
        CameraManager mgr = ClassFactory.getInstance().getCameraManager();
        mgr.asyncOpenCameraAssumingPermission(name, Continuation.create(ThreadPool.getDefault(),
                // subclass layer 1
                new Camera.StateCallback()
                {
                    @Override
                    public void onOpened(Camera camera)
                    {
                        state = OPENED;
                        log.i("Camera opened successfully; requesting stream");
                        try
                        {
                            log.i("Request: format=%s, size=%dx%d, fps=%d", formats.get(format), w, h, fps);
                            CameraCaptureRequest req = camera.createCaptureRequest(
                                    format,
                                    new Size(w, h),
                                    fps
                            );
                            camera.createCaptureSession(Continuation.create(ThreadPool.getDefault(),
                                    // subclass layer 2
                                    new CameraCaptureSession.StateCallback()
                                    {
                                        private Bitmap frameBuffer;
                                        private int frameDrops = 0;
                                        
                                        {
                                            frameBuffer = req.createEmptyBitmap(); // creates ARGB_8888 bitmap
                                            cb.setBuffer(frameBuffer);
                                        }
                                        
                                        @Override
                                        public void onConfigured(CameraCaptureSession session)
                                        {
                                            log.i("Capture session created; starting capture");
                                            try
                                            {
                                                session.startCapture(req, (s, req, frame) -> {
                                                            // subclass layer 3
                                                            if (frameProcessed)
                                                            {
                                                                // log.d("New frame %d", frame.getFrameNumber());
                                                                frameProcessed = false;
                                                                frame.copyToBitmap(frameBuffer);
                                                                cb.onFrame(frameDrops);
                                                            }
                                                            else
                                                            {
                                                                frameDrops++;
                                                            }
                                                            if (stopCapture) s.stopCapture();
                                                        },
                                                        Continuation.create(ThreadPool.getDefault(),
                                                                (s, seqId, lastFrameNum) -> {
                                                                    // subclass layer 3
                                                                    log.i("Stream closed");
                                                                    cb.onClose(lastFrameNum, frameDrops);
                                                                    state = CLOSED;
                                                                }));
                                                state = RUNNING;
                                            }
                                            catch (CameraException e)
                                            {
                                                String err = String.format("Error creating capture session: %s", e.getMessage());
                                                log.w(err);
                                                log.w(e);
                                                error = err;
                                                cb.onError(err);
                                                state = ERROR;
                                            }
                                        }
                                        
                                        @Override
                                        public void onClosed(CameraCaptureSession session)
                                        {
                                            log.i("Session closed");
                                            if (state != CLOSED) cb.onClose(-1, 0);
                                            state = CLOSED;
                                        }
                                    }
                            ));
                        }
                        catch (CameraException e)
                        {
                            String err = String.format("Error creating capture request: %s", e.getMessage());
                            log.w(err);
                            log.w(e);
                            cb.onError(err);
                            error = err;
                            state = ERROR;
                        }
                    }
                    
                    @Override
                    public void onOpenFailed(CameraName cameraName, Camera.OpenFailure reason)
                    {
                        String err = String.format("Failed to open '%s': %s", cameraName, reason);
                        log.w(err);
                        state = ERROR;
                    }
                    
                    @Override
                    public void onClosed(Camera camera)
                    {
                        log.i("Camera closed");
                        state = CLOSED;
                    }
                    
                    @Override
                    public void onError(Camera camera, Camera.Error error)
                    {
                        String err = String.format("Camera error: %s", error);
                        ;
                        log.w(err);
                        Webcam.this.error = err;
                        state = ERROR;
                    }
                }
        ), 30, TimeUnit.MINUTES);
    }
    
    public void close()
    {
        stopCapture = true;
    }
}
