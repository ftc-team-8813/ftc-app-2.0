package org.firstinspires.ftc.teamcode.opmodes;

import static org.opencv.core.CvType.CV_8UC4;

import android.graphics.Bitmap;
import android.graphics.ImageFormat;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.arcrobotics.ftclib.geometry.Pose2d;
import com.arcrobotics.ftclib.geometry.Rotation2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.hardware.Drivetrain;
import org.firstinspires.ftc.teamcode.hardware.Intake;
import org.firstinspires.ftc.teamcode.hardware.Lift;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.hardware.navigation.OdometryNav;
import org.firstinspires.ftc.teamcode.hardware.navigation.PID;
import org.firstinspires.ftc.teamcode.util.Logger;
import org.firstinspires.ftc.teamcode.util.LoopTimer;
import org.firstinspires.ftc.teamcode.vision.ConeInfoDetector;
import org.firstinspires.ftc.teamcode.vision.webcam.Webcam;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

@Autonomous(name = "Left Cone Auto")
public class LeftConeAuto extends LoggingOpMode{

    private Robot robot;

    private Drivetrain drivetrain;
    private Lift lift;
    private Intake intake;
    private OdometryNav odometry;
    private Webcam camera;
    private Webcam.SimpleFrameHandler frameHandler;
    private Mat cvFrame;
    private volatile boolean serverFrameUsed = true;
    private Bitmap serverFrameCopy;
    private String result = "Nothing";

    private int main_id = 0;
    private int lift_id = -1;
    private int emergency_park_id = 0;

    private final double ARM_LOWER_LENGTH = 488.89580;
    private final double ARM_UPPER_LENGTH = 424.15230;

    private ElapsedTime timer = new ElapsedTime();
    private ElapsedTime game_timer = new ElapsedTime();

    private double x = 0;
    private double y = 250;

    double al_error = 0;
    double au_error = 0;

    private boolean pressed_reset = false;

    private final double AL_DEGREES_PER_TICK = -(360.0/8192.0);
    private final double AU_DEGREES_PER_TICK = (360.0/8192.0);
    private final double WRIST_DEGREES_PER_TICK = (360.0/128.0);

    private final PID arm_lower = new PID(0.0346,0.000307,0.00091, 0.167,110,0.8);
    private final PID arm_upper = new PID(0.0571,0.00200,0.0026,0.11,100,0.8); // 0.029, 0.0022, 0.001 then 0.027, 0.00228
    private final PID wrist = new PID(0.02,0,0,0,0,0);

    private final Logger log = new Logger("Left Auto");

    static
    {
        OpenCVLoader.initDebug();
    }

    @Override
    public void init() {
        super.init();
//        ftcDash = new FTCDashboardValues();
        robot = Robot.initialize(hardwareMap);
        drivetrain = robot.drivetrain;
        lift = robot.lift;
        intake = robot.intake;
        odometry = robot.odometryNav;
        camera = Webcam.forSerial("3522DE6F");
        if (camera == null)
            throw new IllegalArgumentException("Could not find a webcam with serial number 3522DE6F");
        frameHandler = new Webcam.SimpleFrameHandler();
        camera.open(ImageFormat.YUY2, 1920, 1080, 30, frameHandler);
        cvFrame = new Mat(1920, 1080, CV_8UC4);
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        Pose2d start_pose = new Pose2d(0,3.5,new Rotation2d(Math.toRadians(45.0)));
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
        intake.setClaw(0.63);
        game_timer.reset();
        camera.close();
    }

    @Override
    public void loop() {

        odometry.updatePose();

        double[] angles = new double[2];

        if (Math.sqrt(Math.pow(x,2) + Math.pow(y,2)) >= (488.89580+424.15230-5)) {
            angles[0] = Math.toDegrees(Math.atan2(y,x));
            angles[1] = angles[0];
        }
        else {
            angles = lift.get_ang(ARM_LOWER_LENGTH, ARM_UPPER_LENGTH, x, y, 90, -90);
        }

        double[] cur_angles = lift.getEncoderValue();
        cur_angles[0] *= AL_DEGREES_PER_TICK;
        cur_angles[1] *= AU_DEGREES_PER_TICK;
        cur_angles[2] *= -WRIST_DEGREES_PER_TICK;
        cur_angles[0] += 0;//149.39559808298318;
        cur_angles[1] += 0;//-165.8935546875;
        cur_angles[2] += 0;//-2.8125;

        double al_f = Math.cos(Math.toRadians(cur_angles[0]));
        double au_f = Math.cos(Math.toRadians(cur_angles[0]) + Math.toRadians(cur_angles[1]));


        double al_pow = arm_lower.getOutPut(angles[0], cur_angles[0], al_f);
        double au_pow = -1 * arm_upper.getOutPut((-angles[0] + angles[1]), cur_angles[1], au_f);
        double wrist_pow = wrist.getOutPut((-angles[1]), cur_angles[2], 0);

        al_error = Math.abs(angles[0] - cur_angles[0]);
        au_error = Math.abs((-angles[0] + angles[1]) - cur_angles[1]);
        double wr_error = Math.abs((-angles[1]) - cur_angles[2]);


        lift.setLiftPower(Range.clip(al_pow, -0.321, 0.321), Range.clip(au_pow, -0.303, 0.303), wrist_pow);


        if (game_timer.seconds() > 26) {
            switch (emergency_park_id) {
                case 0:
                    x = -200;
                    y = 800;
                    drivetrain.autoMove(25, 0, 0, 0, 1, 1, 0.5, odometry.getPose(), telemetry);
                    if (drivetrain.hasReached()) {
                        emergency_park_id += 1;
                    }
                    break;
                case 1:
                    switch (result) {
                        case "FTC8813: 1":
                            drivetrain.autoMove(25,-20,0,0,1,1,10, odometry.getPose(),telemetry);
                            if (drivetrain.hasReached()) {
                                emergency_park_id += 1;
                            }
                            break;
                        case "FTC8813: 3":
                            drivetrain.autoMove(25,20,0,0,1,1,10, odometry.getPose(),telemetry);
                            if (drivetrain.hasReached()) {
                                emergency_park_id += 1;
                            }
                            break;
                        default:
                            drivetrain.autoMove(25,0,0,0,1,1,10, odometry.getPose(),telemetry);
                            if (drivetrain.hasReached()) {
                                emergency_park_id += 1;
                            }
                            break;
                    }
                    break;
                case 5:
                    drivetrain.stop();
                    break;
            }
        }
        else {
            switch (main_id) {
                case 0:
                    x = 0;
                    y = 860;
                    drivetrain.autoMove(59, 0, 0, 0, 1, 1, 3, odometry.getPose(), telemetry);
                    if (drivetrain.hasReached()) {
                        main_id += 1;
                    }
                    break;
                case 1:
                    drivetrain.autoMove(59, 4.8, 0, 0, 1, 1, 3, odometry.getPose(), telemetry);
                    if (drivetrain.hasReached()) {
                        main_id += 1;
                    }
                    break;
                case 2:
                    drivetrain.autoMove(59, 4.8, 96.85, 0, 1, 1, 0.5, odometry.getPose(), telemetry);
                    if (drivetrain.hasReached()) {
                        lift_id += 1;
                        main_id += 1;
                        timer.reset();
                    }
                    break;
                case 3:
                    drivetrain.autoMove(59, 4.8, 96.85, 0, 1, 1, 0.07, odometry.getPose(), telemetry);
                    break;
                case 4:
                    x = 0;
                    y = 860;
                    drivetrain.autoMove(25, 0, 0, 0, 1, 1, 0.5, odometry.getPose(), telemetry);
                    if (drivetrain.hasReached()) {
                        main_id += 1;
                    }
                    break;
                case 5:
                    switch (result) {
                        case "FTC8813: 1":
                            drivetrain.autoMove(25,-20,0,0,1,1,10, odometry.getPose(),telemetry);
                            if (drivetrain.hasReached()) {
                                main_id += 1;
                            }
                            break;
                        case "FTC8813: 3":
                            drivetrain.autoMove(25,20,0,0,1,1,10, odometry.getPose(),telemetry);
                            if (drivetrain.hasReached()) {
                                main_id += 1;
                            }
                            break;
                        default:
                            drivetrain.autoMove(25,0,0,0,1,1,10, odometry.getPose(),telemetry);
                            if (drivetrain.hasReached()) {
                                main_id += 1;
                            }
                            break;
                    }
                    break;
                case 6:
                    drivetrain.stop();
                    break;


//            case 2:
//                intake.setClaw(0.11);
//                drivetrain.autoMove(59.26,4,96.85,0,1,1,0.07, odometry.getPose(),telemetry);
//                x = 650;
//                y = 45.5;
//                if (al_error < 2.6 && au_error < 2.6 && wr_error < 5) {
//                    main_id += 1;
//                }
//                break;
//            case 3:
//                intake.setClaw(0.11);
//                drivetrain.autoMove(59.26,4,96.85,0,1,1,0.07, odometry.getPose(),telemetry);
//                x = 757.8;
//                y = 45.5;
//                if (al_error < 2.6 && au_error < 2.6 && wr_error < 5) {
//                    main_id += 1;
//                }
//                break;
//            case 4:
//                drivetrain.autoMove(59.26,4,96.85,0,1,1,0.07, odometry.getPose(),telemetry);
//                intake.setClaw(0.63);
//                if (intake.getClawPosition() == 0.63) {
//                    main_id += 1;
//                }
//                break;
//            case 5:
//                drivetrain.autoMove(59.26,4,96.85,0,1,1,0.07, odometry.getPose(),telemetry);
//                x = 300;
//                y = 60;
//                if (al_error < 2.6 && au_error < 2.6 && wr_error < 5) {
//                    main_id += 1;
//                }
//                break;
//            case 11:
//                drivetrain.autoMove(59.26,4,96.85,0,1,1,0.07, odometry.getPose(),telemetry);
//                x = 757.8;
//                y = 60;
//                if (al_error < 2.6 && au_error < 2.6 && wr_error < 5) {
//                    main_id += 1;
//                }
//                break;
//            case 12:
//                drivetrain.autoMove(59.26,4,96.85,0,1,1,0.07, odometry.getPose(),telemetry);
//                x = -375.5;
//                y = 823.3;
//                if (al_error < 2.6 && au_error < 2.6 && wr_error < 5) {
//                    main_id += 1;
//                }
//                break;
//            case 13:
//                drivetrain.autoMove(59.26,4,96.85,0,1,1,0.07, odometry.getPose(),telemetry);
//                intake.setClaw(0.11);
//                if ((intake.getClawPosition() == 0.11) && (intake.getDistance() > 20)) {
//                    main_id += 1;
//                }
//                break;


//            case 4:
//                drivetrain.autoMove(23,0,0,0,1,1,3, odometry.getPose(),telemetry);
//                if (drivetrain.hasReached()) {
//                    main_id += 1;
//                }
//                break;
//            case 5:
//                drivetrain.autoMove(44.1,0,0,0,1,1,3, odometry.getPose(),telemetry);
//                if (drivetrain.hasReached()) {
//                    main_id += 1;
//                }
//                break;
//            case 6:
//                drivetrain.autoMove(44.1,10,0,0,1,1,3, odometry.getPose(),telemetry);
//                if (drivetrain.hasReached()) {
//                    main_id += 1;
//                }
//                break;
//            case 7:
//                drivetrain.autoMove(44.1,10,82,0,0.5,0.5,0.03, odometry.getPose(),telemetry);
//                if (drivetrain.hasReached()) {
//                    main_id += 1;
//                }
//                break;
//            case 8:
//                x = 1000;
//                y = -40;
//                drivetrain.autoMove(44.1,10,82,0,0.5,0.5,0.03, odometry.getPose(),telemetry);
//                if (al_error < 3 && au_error < 3 && wr_error < 10) {
//                    main_id += 1;
//                }
//                break;
//            case 9:
////            case 15:
//                drivetrain.autoMove(44.1,10,82,0,0.5,0.5,0.03, odometry.getPose(),telemetry);
//                if (intake.getDistance() < 20) {
//                    intake.setClaw(0.63);
//                }
//                if ((intake.getClawPosition() == 0.63) && (intake.getDistance() < 20)){
//                    main_id += 1;
//                }
//                break;
//            case 10:
////            case 16:
//                x = -380;
//                y = 870;
//                drivetrain.autoMove(44.1,35,82,0,0.5,0.5,0.03, odometry.getPose(),telemetry);
//                if (drivetrain.hasReached()) {
//                    main_id += 1;
//                }
//                break;
//            case 11:
//                drivetrain.autoMove(44.1,35,3,0,0.5,0.5,0.03, odometry.getPose(),telemetry);
//                if (drivetrain.hasReached()) {
//                    main_id += 1;
//                }
//                break;
//            case 14:
//                x = 0;
//                y = 250;
//                drivetrain.autoMove(44.1,10,3,0,0.5,0.5,0.03, odometry.getPose(),telemetry);
//                if (drivetrain.hasReached()) {
//                    main_id += 1;
//                }
//                break;
//            case 15:
//                x = 1000;
//                y = -25;
//                drivetrain.autoMove(44.1,10,82,0,0.5,0.5,0.03, odometry.getPose(),telemetry);
//                if (drivetrain.hasReached()) {
//                    main_id += 1;
//                }
//                break;

//            case 14:
//                x = 1000;
//                y = -25;
//                drivetrain.autoMove(44.1,10,82,0,0.5,0.5,0.03, odometry.getPose(),telemetry);
//                if (al_error < 3 && au_error < 3 && wr_error < 10) {
//                    main_id += 1;
//                }

            }

            switch (lift_id) {
                case 0:
                    x = -382;
                    y = 860;
                    if(timer.seconds() > 0.3) {
                        if (al_error < 2.6 && au_error < 2.8 && wr_error < 5) {
                            lift_id += 1;
                            timer.reset();
                        }
                    }
                    break;
                case 1:
                    intake.setClaw(0.11);
                    if ((intake.getClawPosition() == 0.11) && (intake.getDistance() > 20)) {
                        lift_id += 1;
                    }
                    break;
                case 2:
                    x = 650;
                    y = 46;
                    if (al_error < 2.6 && au_error < 2.6 && wr_error < 5) {
                        lift_id += 1;
                    }
                    break;
                case 3:
                    x = 770;
                    y = 46;
                    if (al_error < 2.6 && au_error < 2.6 && wr_error < 5) {
                        lift_id += 1;
                        timer.reset();
                    }
                    break;
                case 4:
                    if (timer.seconds() > 0.1) {
                        intake.setClaw(0.63);
                        if ((intake.getClawPosition() == 0.63) && (timer.seconds() > 0.4)) {
                            x = 757.9;
                            y = 250;
                            lift_id += 1;
                            timer.reset();
                        }
                    }
                    break;
                case 5:
                    if (timer.seconds() > 0.35) {
                        x = -382;
                        y = 860;
                        if (al_error < 2.6 && au_error < 2.6 && wr_error < 5) {
                            lift_id += 1;
                            timer.reset();
                        }
                    }
                    break;
                case 6:
                    if (timer.seconds() > 0.1 && (al_error < 2.6 && au_error < 2.6 && wr_error < 5)) {
                        intake.setClaw(0.11);
                        if ((intake.getClawPosition() == 0.11) && (intake.getDistance() > 20)) {
                            lift_id += 1;
                        }
                    }
                    break;
                case 7:
                    intake.setClaw(0.11);
                    x = 650;
                    y = 28;
                    if (al_error < 2.6 && au_error < 2.6 && wr_error < 5) {
                        lift_id += 1;
                    }
                    break;
                case 8:
                    x = 770;
                    y = 28;
                    if (al_error < 2.6 && au_error < 2.6 && wr_error < 5) {
                        lift_id += 1;
                        timer.reset();
                    }
                    break;
                case 9:
                    if (timer.seconds() > 0.1) {
                        intake.setClaw(0.63);
                        if ((intake.getClawPosition() == 0.63) && (timer.seconds() > 0.4)) {
                            x = 757.9;
                            y = 250;
                            lift_id += 1;
                            timer.reset();
                        }
                    }
                    break;
                case 10:
                    if (timer.seconds() > 0.35) {
                        x = -382;
                        y = 860;
                        if (al_error < 2.6 && au_error < 2.6 && wr_error < 5) {
                            lift_id += 1;
                            timer.reset();
                        }
                    }
                    break;
                case 11:
                    if (timer.seconds() > 0.1 && (al_error < 2.6 && au_error < 2.6 && wr_error < 5)) {
                        intake.setClaw(0.11);
                        if ((intake.getClawPosition() == 0.11) && (intake.getDistance() > 20)) {
                            lift_id += 1;
                        }
                    }
                    break;
                case 12:
                    intake.setClaw(0.11);
                    x = 650;
                    y = 0;
                    if (al_error < 2.6 && au_error < 2.6 && wr_error < 5) {
                        lift_id += 1;
                    }
                    break;
                case 13:
                    x = 770;
                    y = 0;
                    if (al_error < 2.6 && au_error < 2.6 && wr_error < 5) {
                        lift_id += 1;
                        timer.reset();
                    }
                    break;
                case 14:
                    if (timer.seconds() > 0.1) {
                        intake.setClaw(0.63);
                        if ((intake.getClawPosition() == 0.63) && (timer.seconds() > 0.4)) {
                            x = 757.9;
                            y = 250;
                            lift_id += 1;
                            timer.reset();
                        }
                    }
                    break;
                case 15:
                    if (timer.seconds() > 0.35) {
                        x = -382;
                        y = 860;
                        if (al_error < 2.6 && au_error < 2.6 && wr_error < 5) {
                            lift_id += 1;
                            timer.reset();
                        }
                    }
                    break;
                case 16:
                    if (timer.seconds() > 0.1 && (al_error < 2.6 && au_error < 2.6 && wr_error < 5)) {
                        intake.setClaw(0.11);
                        if ((intake.getClawPosition() == 0.11) && (intake.getDistance() > 20)) {
                            lift_id += 1;
                        }
                    }
                    break;
                case 17:
                    intake.setClaw(0.11);
                    x = 650;
                    y = -17;
                    if (al_error < 2.6 && au_error < 2.6 && wr_error < 5) {
                        lift_id += 1;
                    }
                    break;
                case 18:
                    x = 770;
                    y = -17;
                    if (al_error < 2.6 && au_error < 2.6 && wr_error < 5) {
                        lift_id += 1;
                        timer.reset();
                    }
                    break;
                case 19:
                    if (timer.seconds() > 0.1) {
                        intake.setClaw(0.63);
                        if ((intake.getClawPosition() == 0.63) && (timer.seconds() > 0.4)) {
                            x = 757.9;
                            y = 250;
                            lift_id += 1;
                            timer.reset();
                        }
                    }
                    break;
                case 20:
                    if (timer.seconds() > 0.35) {
                        x = -382;
                        y = 860;
                        if (al_error < 2.6 && au_error < 2.6 && wr_error < 5) {
                            lift_id += 1;
                            timer.reset();
                        }
                    }
                    break;
                case 21:
                    if (timer.seconds() > 0.1 && (al_error < 2.6 && au_error < 2.6 && wr_error < 5)) {
                        intake.setClaw(0.11);
                        if ((intake.getClawPosition() == 0.11) && (intake.getDistance() > 20)) {
                            lift_id += 1;
                        }
                    }
                    break;
                case 22:
                    intake.setClaw(0.11);
                    x = 650;
                    y = -57;
                    if (al_error < 2.6 && au_error < 2.6 && wr_error < 5) {
                        lift_id += 1;
                    }
                    break;
                case 23:
                    x = 770;
                    y = -57;
                    if (al_error < 2.6 && au_error < 2.6 && wr_error < 5) {
                        lift_id += 1;
                        timer.reset();
                    }
                    break;
                case 24:
                    if (timer.seconds() > 0.1) {
                        intake.setClaw(0.63);
                        if ((intake.getClawPosition() == 0.63) && (timer.seconds() > 0.4)) {
                            x = 757.9;
                            y = 250;
                            lift_id += 1;
                            timer.reset();
                        }
                    }
                    break;
                case 25:
                    if (timer.seconds() > 0.35) {
                        x = -382;
                        y = 860;
                        if (al_error < 2.6 && au_error < 2.6 && wr_error < 5) {
                            lift_id += 1;
                            timer.reset();
                        }
                    }
                    break;
                case 26:
                    if (timer.seconds() > 0.1 && (al_error < 2.6 && au_error < 2.6 && wr_error < 5)) {
                        intake.setClaw(0.11);
                        if ((intake.getClawPosition() == 0.11) && (intake.getDistance() > 20)) {
                            lift_id += 1;
                            main_id += 1;
                        }
                    }
                    break;


            }
        }


        telemetry.addData("Time",game_timer.seconds());
        telemetry.addData("AL Target Angle",angles[0]);
        telemetry.addData("AU Target Angle",(-1*(angles[0] - angles[1])));
        telemetry.addData("WR Target Angle",-angles[1]);

        telemetry.addData("AL Angle",cur_angles[0]);
        telemetry.addData("AU Angle",cur_angles[1]);
        telemetry.addData("WR Angle",cur_angles[2]);

        telemetry.addData("AL Power",al_pow);
        telemetry.addData("AU Power",au_pow);
        telemetry.addData("WR Power",wrist_pow);


        telemetry.addData("X", x);
        telemetry.addData("Y", y);

        telemetry.addData("Loop Time: ", LoopTimer.getLoopTime());
        telemetry.addData("Error AU: ", au_error);
        telemetry.addData("Error AL: ", al_error);

        telemetry.addData("Main Id: ", main_id);
        telemetry.addData("Lift Id",lift_id);
        telemetry.update();

        LoopTimer.resetTimer();

    }

    @Override
    public void stop() {
        super.stop();
//        camera.close();
    }

}
