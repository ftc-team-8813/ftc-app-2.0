package org.firstinspires.ftc.teamcode.opmodes;

import static org.opencv.core.CvType.CV_8UC4;

import android.graphics.Bitmap;
import android.graphics.ImageFormat;

import com.arcrobotics.ftclib.geometry.Pose2d;
import com.arcrobotics.ftclib.geometry.Rotation2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.hardware.Drivetrain;
import org.firstinspires.ftc.teamcode.hardware.Lift;
import org.firstinspires.ftc.teamcode.hardware.navigation.OdometryNav;
import org.firstinspires.ftc.teamcode.hardware.navigation.PID;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.util.Logger;
import org.firstinspires.ftc.teamcode.util.LoopTimer;
import org.firstinspires.ftc.teamcode.vision.ConeInfoDetector;
import org.firstinspires.ftc.teamcode.vision.webcam.Webcam;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

@Autonomous(name = "Blue Auto")
public class ParkingAuto extends LoggingOpMode{

    private Drivetrain drivetrain;
    private Lift lift;
    private OdometryNav odometry;
    private Webcam camera;
    private Webcam.SimpleFrameHandler frameHandler;
    private Mat cvFrame;
    private volatile boolean serverFrameUsed = true;
    private Bitmap serverFrameCopy;
    private String result = "Nothing";

    private int main_id = 0;

    private final Logger log = new Logger("Parking Auto");

    static
    {
        OpenCVLoader.initDebug();
    }

    @Override
    public void init() {
        super.init();
        Robot robot = Robot.initialize(hardwareMap);
        drivetrain = robot.drivetrain;
        lift = robot.lift;
        odometry = robot.odometryNav;
        camera = Webcam.forSerial("3522DE6F");
        if (camera == null)
            throw new IllegalArgumentException("Could not find a webcam with serial number 3522DE6F");
        frameHandler = new Webcam.SimpleFrameHandler();
        camera.open(ImageFormat.YUY2, 1920, 1080, 30, frameHandler);
        cvFrame = new Mat(1920, 1080, CV_8UC4);
        Pose2d start_pose = new Pose2d(0,0,new Rotation2d(Math.toRadians(45.0)));
        odometry.updatePose(start_pose);
    }

    @Override
    public void init_loop() {
        super.init_loop();

        if (frameHandler.newFrameAvailable) {
            frameHandler.newFrameAvailable = false;
            Utils.bitmapToMat(frameHandler.currFramebuffer, cvFrame);
            if (serverFrameUsed) {
                if (serverFrameCopy != null) serverFrameCopy.recycle();
                serverFrameCopy = frameHandler.currFramebuffer.copy(Bitmap.Config.ARGB_8888, false);
                serverFrameUsed = false;
            }

            ConeInfoDetector detector = new ConeInfoDetector(cvFrame,log,-1006,255);

            if (!detector.detect().equals("Nothing Detected"))
            {
                result = detector.detect();
            }

            camera.requestNewFrame();
        }

        telemetry.addData("Detected", result);

        telemetry.update();
    }

    @Override
    public void start() {
        super.start();
        drivetrain.resetEncoders();
    }

    @Override
    public void loop() {

        odometry.updatePose();

        switch (main_id) {
            case 0:
                drivetrain.autoMove(24,0,0,0,2,6,10, odometry.getPose(),telemetry);
                if (drivetrain.hasReached()) {
                    main_id += 1;
                }
                break;
            case 1:
                switch (result) {
                    case "FTC8813: 1":
                        drivetrain.autoMove(26,-26,0,0,1,1,10, odometry.getPose(),telemetry);
                        if (drivetrain.hasReached()) {
                            main_id += 1;
                        }
                        break;
                    case "FTC8813: 3":
                        drivetrain.autoMove(26,26,0,0,1,1,10, odometry.getPose(),telemetry);
                        if (drivetrain.hasReached()) {
                            main_id += 1;
                        }
                        break;
                }
                break;
            case 2:
                drivetrain.stop();
        }

        telemetry.addData("Loop Time: ", LoopTimer.getLoopTime());
        telemetry.update();

        LoopTimer.resetTimer();

    }

    @Override
    public void stop() {
        super.stop();
    }

}
