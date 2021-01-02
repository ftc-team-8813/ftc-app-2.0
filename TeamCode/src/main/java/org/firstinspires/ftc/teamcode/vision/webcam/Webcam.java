package org.firstinspires.ftc.teamcode.vision.webcam;

import com.qualcomm.robotcore.util.ThreadPool;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.function.Continuation;
import org.firstinspires.ftc.robotcore.external.hardware.camera.Camera;
import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraManager;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.internal.system.AppUtil;
import org.firstinspires.ftc.robotcore.internal.system.Deadline;

import java.util.List;
import java.util.concurrent.TimeUnit;

// TODO: WORK IN PROGRESS
public class Webcam
{
    public static final int NEVER_OPENED = 0;
    public static final int OPENING      = 1;
    public static final int OPENED       = 2;
    public static final int RUNNING      = 3;
    public static final int CLOSED       = -1;
    public static final int ERROR        = -2;
    
    private WebcamName name;
    private volatile int state;
    
    public static Webcam[] getConnected()
    {
        CameraManager mgr = ClassFactory.getInstance().getCameraManager();
        List<WebcamName> names = mgr.getAllWebcams();
        Webcam[] cameras = new Webcam[names.size()];
        for (int i = 0; i < names.size(); i++)
        {
            cameras[i] = new Webcam(names.get(i));
        }
        return cameras;
    }
    
    private Webcam(WebcamName name)
    {
        this.name = name;
    }
    
    public void open()
    {
        state = OPENING;
        CameraManager mgr = ClassFactory.getInstance().getCameraManager();
        // mgr.asyncOpenCameraAssumingPermission(name, Continuation.create(ThreadPool.getDefault(), ));
    }
}
