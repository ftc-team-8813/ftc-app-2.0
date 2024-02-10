package org.firstinspires.ftc.teamcode.opmodes.auto;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.controller.PIDController;
import com.arcrobotics.ftclib.controller.PIDFController;
import com.arcrobotics.ftclib.geometry.Pose2d;
import com.arcrobotics.ftclib.geometry.Rotation2d;
import com.arcrobotics.ftclib.kinematics.HolonomicOdometry;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.R;
import org.firstinspires.ftc.teamcode.hardware.Camera;
import org.firstinspires.ftc.teamcode.hardware.Deposit;
import org.firstinspires.ftc.teamcode.hardware.DistanceSensors;
import org.firstinspires.ftc.teamcode.hardware.Drivetrain;
import org.firstinspires.ftc.teamcode.hardware.Horizontal;
import org.firstinspires.ftc.teamcode.hardware.Intake;
import org.firstinspires.ftc.teamcode.hardware.Lift;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.hardware.navigation.ColorPipeline;
import org.firstinspires.ftc.teamcode.hardware.navigation.PID;
import org.firstinspires.ftc.teamcode.opmodes.LoggingOpMode;
import org.firstinspires.ftc.teamcode.vision.AprilTagDetectionPipeline;
import org.openftc.apriltag.AprilTagDetection;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

import java.util.ArrayList;

@Config
@Autonomous(name = "!!Detection Auto Left Red!!")

public class DetectionAutoLeftRed extends LoggingOpMode {
    //    private PIDController xCont = new PIDController(0.03, 0, 0);
//    private PIDController yCont = new PIDController(0.07, 0, 0.07);
//    private PIDController headingCont = new PIDController(0.07, 0.125, 0.005);
    private HolonomicOdometry odometry;
    private final double PIVOTUP = 0.263;
    private final double LOWPIVOTUP = 0.045;
    //    0.077
    private final double HIGHPIVOTUP = 0.17;
    private final double LIFTSERVSUP = 0.257;
    private final double LIFTSERVPRE = 0.51;
    private final double PIVOTPRE = 0.025;
    private final double LIFTSERVFULL = 0.65;
    private final double PIVOTFULL = 0.12;
    private final double PIVOTINIT = 0.95;
    private final double LIFTSERVINIT = 0.710;
    private final double MICROCLOSED = 0.85;
    private final double MICROOPENED = 0.429;
    private final double INTAKELOCKOPENED = 0;
    private final double INTAKELOCKClOSED = 0.2;
    private final double LIFTDOWNPOS = 0;
    private final double LIFTMIDPOS = 190;
    private final double LIFTHIGHPOS = 310;
    private Lift lift;
    private Drivetrain drivetrain;
    private Deposit deposit;
    private Intake intake;
    private Robot robot;
    private boolean alreadyOpened;
    private double liftTarget;
    private int ID;
//    public static double kpX = 0.85;
//    public static double kiX = 0.5;
//    public static double kdX = 1.85;
//    public static double kpY = 0.0295;
//    public static double kiY = 0.9;
//    public static double kdY = 1.5;
//    public static double kpH = 0.6;
//    public static double kiH;
//    public static double kdH = 0.4;

    private double kpX = 0.06;
    private double kiX = 0;
    private double kdX = 0.02;
    private double kpY = 0.07;
    private double kiY = 0;
    private double kdY = 0.02;
    private double kpH = 0.8;
    private double kiH = 0.007;
    private double kdH = 0.12;
//    public static double targetX;
//    public static double targetY;
//    public static double targetHeading;
    private Pose2d currentPose;
    private Pose2d targetPose;
    private FtcDashboard dashboard;
    public PIDController xCont = new PIDController(kpX, kiX, kdX);
    public PIDController yCont = new PIDController(kpY, kiY, kdY);
    private PIDController headingCont = new PIDController(kpH, kiH, kdH);
    private OpenCvCamera camera;
    private boolean onPart2;
    private ColorPipeline color_pipeline;
    private boolean resetted;
    private String result = "Nothing";
    private PIDFController horizPID = new PIDFController(0.027, 0, 0, 0);
    private PIDFController liftPID = new PIDFController(0.02, 0, 0, 0);
    private ElapsedTime timer1;
    private ElapsedTime timer2;
    private ElapsedTime timer3;
    private ElapsedTime timer4;
    private ElapsedTime timer5;
    private ElapsedTime timer6;
    private ElapsedTime timer7;
    private ElapsedTime timer8;
    private boolean resetted4;
    private boolean resetted5;
    private boolean resetted6;
    private boolean resetted7;
    private boolean resetted8;

    private double horizCurrent;
    private double liftCurrent;
    private boolean resetted1;
    private boolean resetted2;
    private boolean resetted3;
    private double intakePower;
    private Horizontal horiz;
    private int num;
    @Override
    public void init() {
        super.init();
        dashboard = FtcDashboard.getInstance();
        robot = Robot.initialize(hardwareMap);
        odometry = robot.odometry;
        drivetrain = robot.drivetrain;
        intake = robot.intake;
        horiz = robot.horiz;
        deposit = robot.deposit;
        lift = robot.lift;
        currentPose = new Pose2d(0, 0, new Rotation2d(0));
        targetPose = new Pose2d(0, 0, new Rotation2d(0));
        odometry.update(0, 0, 0);
        odometry.updatePose();
        robot.center_odometer.reset();
        robot.right_odometer.reset();
        robot.left_odometer.reset();
        resetted = false;
        intake.setLock(INTAKELOCKClOSED);
        num = 0;
        alreadyOpened = false;
        horiz.resetEncoders();
        lift.resetEncoders();


        onPart2 = false;
        timer1 = new ElapsedTime();
        timer2 = new ElapsedTime();
        timer3 = new ElapsedTime();
        timer4 = new ElapsedTime();
        timer5 = new ElapsedTime();
        timer6 = new ElapsedTime();
        timer7 = new ElapsedTime();
        timer8 = new ElapsedTime();


        resetted1 = false;
        resetted2 = false;
        resetted3 = false;
        resetted4 = false;
        resetted5 = false;
        resetted6 = false;
        resetted7 = false;
        resetted8 = false;

        liftTarget = 0;


//        color_pipeline = new ColorPipeline();

        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        camera = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam 1"), cameraMonitorViewId);
        color_pipeline = new ColorPipeline(410, 200, 190, 230, "far red");  //close red and far blue

        camera.setPipeline(color_pipeline);

        camera.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {
                camera.startStreaming(640, 480, OpenCvCameraRotation.UPRIGHT);
            }

            @Override
            public void onError(int errorCode) {

            }
        });
    }

    @Override
    public void init_loop() {

        result = color_pipeline.getLocation();

        if (result.equals("left")) {
            ID = 4;
        } else if (result.equals("right")) {
            ID = 6;
        } else if (result.equals("center")) {
            ID = 5;
        }


        telemetry.addData("Result", result);
//        telemetry.addData("color", color_pipeline.getColorValue());
//        telemetry.addData("color", color_pipeline.getColorHSVValue());
//        telemetry.addData("color 2", color_pipeline.getColorHSVValue2());
        telemetry.update();

    }

    @Override
    public void loop() {
        if (!alreadyOpened) {
            deposit.setDepoPivot(0.63);
            alreadyOpened = true;
        }
        odometry.updatePose();
        currentPose = odometry.getPose();
        lift.update();
        horiz.update();

        currentPose = odometry.getPose();
        liftCurrent = lift.getCurrentPosition();
        horizCurrent = horiz.getCurrentPosition();

        xCont = new PIDController(kpX, kiX, kdX);
        yCont = new PIDController(kpY, kiY, kdY);
        headingCont = new PIDController(kpH, kiH, kdH);
//        targetPose = new Pose2d(targetX, targetY, Rotation2d.fromDegrees(targetHeading));

        switch (num) {
            case 0:
                if (!resetted2) {
                    timer2.reset();
                    resetted2 = true;
                }
                if (timer2.seconds() > 4) {
                    num += 1;
                    resetted = false;
                    resetted1 = false;
                    resetted2 = false;
                    resetted3 = false;
                }

                if (result == "left") {
                    targetPose = new Pose2d(12.3, 5, Rotation2d.fromDegrees(25));
                    if (closeToPosition(targetPose, currentPose, 1, 1.25, 1)) {
                        if (!resetted1) {
                            timer1.reset();
                            resetted1 = true;
                        }
                        if (timer1.seconds() > 0.6) {
                            num += 1;
                            resetted = false;
                            resetted1 = false;
                            resetted2 = false;
                            resetted3 = false;
                        }
                    }
                } else if (result == "center") {
                    targetPose = new Pose2d(22.4, 0, Rotation2d.fromDegrees(0));
                    if (closeToPosition(targetPose, currentPose, 1, 1.25, 1)) {
                        if (!resetted1) {
                            timer1.reset();
                            resetted1 = true;
                        }
                        if (timer1.seconds() > 0.6) {
                            num += 1;
                            resetted1 = false;
                            resetted2 = false;
                            resetted3 = false;
                            resetted = false;
                        }
                    }
                } else {
                    if (onPart2) {
                        targetPose = new Pose2d(-4, -10, Rotation2d.fromDegrees(-29));
                        if (closeToPosition(new Pose2d(-3, -10, Rotation2d.fromDegrees(-50)), currentPose, 1, 1.25, 1)) {
                            if (!resetted1) {
                                timer1.reset();
                                resetted1 = true;
                            }
                            if (timer1.seconds() > 0.6) {
                                num += 1;
                                resetted1 = false;
                                resetted2 = false;
                                resetted3 = false;
                                resetted = false;
                            }
                        }
                    } else {
                        targetPose = new Pose2d(18, -5, Rotation2d.fromDegrees(0));
                        if (!resetted3) {
                            timer3.reset();
                            resetted3 = true;
                        }
                        if (timer3.seconds() > 3.5) {
                            resetOdo(robot);
                            onPart2 = true;
                        }
                        if (closeToPosition(new Pose2d(19, -5, Rotation2d.fromDegrees(0)), currentPose, 1, 1.25, 1)) {
                            resetOdo(robot);
                            onPart2 = true;
                        }
                    }
                }
                break;
            case 1:
                deposit.setDepoLock(MICROOPENED);
                if (!resetted1) {
                    timer1.reset();
                    resetted1 = true;
                }
                if (timer1.seconds() > 0.5) {
                    deposit.setDepoLock(MICROCLOSED);
                    deposit.setDepoPivot(PIVOTINIT);
                    num += 1;
                    resetted1 = false;
                }
                break;
            case 2:
                if (!resetted1) {
                    timer1.reset();
                    resetted1 = true;
                }
                if (timer1.seconds() > 1.5) {
                    num += 1;
                    resetted1 = false;
                }
                targetPose = new Pose2d(6, 6, Rotation2d.fromDegrees(0));
                if (closeToPosition(new Pose2d(6, 6, Rotation2d.fromDegrees(0)), currentPose, 1, 1.25, 1)) {
                    num += 1;
                }
                break;
            case 3:
                if (!resetted1) {
                    timer1.reset();
                    resetted1 = true;
                }
                if (timer1.seconds() > 2.6) {
                    num += 1;
                    resetted1 = false;
                }
                if (result.equals("center") || result.equals("left")) {
                    targetPose = new Pose2d(51, 17, Rotation2d.fromDegrees(0));
                    if (closeToPosition(new Pose2d(51, 17, Rotation2d.fromDegrees(0)), currentPose, 1, 1.25, 1)) {
                        num += 1;
                    }
                }
                break;
            case 4:
                if (!resetted) {
                    resetOdo(robot);
                    resetted = true;
                }
                if (!resetted1) {
                    timer1.reset();
                    resetted1 = true;
                }
                if (timer1.seconds() > 2.5) {
//                    odometry.updatePose(new Pose2d(0, 0, Rotation2d.fromDegrees(0)));
                    resetOdo(robot);
                    xCont = new PIDController(kpX, kiX, kdX);
                    yCont = new PIDController(kpY, kiY, kdY);
                    resetted1 = false;
                    num += 1;
                }
                xCont = new PIDController(0, 0, 0);
                yCont = new PIDController(0, 0, 0);
                targetPose = new Pose2d(0, 0, Rotation2d.fromDegrees(-85));
                if (closeToPosition(new Pose2d(0, 0, Rotation2d.fromDegrees(-85)), currentPose, 1, 1.25, 1)) {
//                    odometry.updatePose(new Pose2d(0, 0, Rotation2d.fromDegrees(0)));
                    resetOdo(robot);
                    xCont = new PIDController(kpX, kiX, kdX);
                    yCont = new PIDController(kpY, kiY, kdY);
                    resetted1 = false;
                    num += 1;
                }
                break;
            case 5:
                targetPose = new Pose2d(90, -4, Rotation2d.fromDegrees(0));
                if (closeToPosition(new Pose2d(90, -4, Rotation2d.fromDegrees(0)), currentPose, 4, 1.5, 2)) {
//                    odometry.updatePose(new Pose2d(0, 0, Rotation2d.fromDegrees(0)));
                    resetOdo(robot);
                    xCont = new PIDController(0, 0, 0);
                    yCont = new PIDController(0, 0, 0);
                    num += 1;
                }
                break;
//            case 6:
//                targetPose = new Pose2d(90, -10, Rotation2d.fromDegrees(0));
//                if (closeToPosition(new Pose2d(90, 0, Rotation2d.fromDegrees(0)), currentPose, 3, 1.25, 1)) {
//                    resetOdo(robot);
//                    xCont = new PIDController(0, 0, 0);
//                    yCont = new PIDController(0, 0, 0);
//                    num += 1;
//                }
//                break;
            case 6:
                targetPose = new Pose2d(0, 0, Rotation2d.fromDegrees(-31));
                if (closeToPosition(new Pose2d(0, 0, Rotation2d.fromDegrees(-31)), currentPose, 3, 3, 3)) {
                    resetOdo(robot);
                    num += 1;
                }
                break;
            case 7:
                targetPose = new Pose2d(4, 0, Rotation2d.fromDegrees(31));
                if (closeToPosition(new Pose2d(4, 0, Rotation2d.fromDegrees(31)), currentPose, 2, 3, 2)) {
                    xCont = new PIDController(kpX, kiX, kdX);
                    yCont = new PIDController(kpY, kiY, kdY);
                    resetOdo(robot);
                    num += 1;
                }
                break;
            case 8:
                targetPose = new Pose2d(0, -18, Rotation2d.fromDegrees(0));
                if (closeToPosition(new Pose2d(0, -18, Rotation2d.fromDegrees(0)), currentPose, 2, 1.25, 2)) {
                    num += 1;
                }
                break;

        }

        autoMove(targetPose, currentPose, telemetry, drivetrain, xCont, yCont, headingCont);
        horiz.setHorizPwr(horizPID.calculate(horiz.getCurrentPosition(), horiz.getHorizTarget()));
        lift.setLiftsPower(liftPID.calculate(liftCurrent, liftTarget));
        intake.setPower(-intakePower);

        telemetry.addData("Current Pose", currentPose);
        telemetry.addData("Target Pose", targetPose);
        telemetry.addData("Target X", targetPose.getX());
        telemetry.addData("Target Y", targetPose.getY());
        telemetry.addData("Target Heading", targetPose.getHeading());
        telemetry.addData("Case", num);
        telemetry.addData("X Cont", xCont);
        telemetry.addData("Y Cont", yCont);

    }

    //close red: ID:6; -25, 15, -5; ID:5, 23; ID:4, 20, 5 & then 50 heading, -4, 10


    public void autoMove(Pose2d targetPose, Pose2d currentPose, Telemetry telemetry, Drivetrain
        drivetrain, PIDController xCont, PIDController yCont, PIDController headingCont) {

        double xPower = clipValue(-0.55, 0.55, xCont.calculate(currentPose.getY(), targetPose.getY()));
        double headingPower = clipValue(-0.6, 0.6, headingCont.calculate(currentPose.getHeading(), targetPose.getHeading()));
        double yPower = clipValue(-0.55, 0.55, yCont.calculate(currentPose.getX(), targetPose.getX()));

        drivetrain.move(-yPower, xPower, -headingPower, 0);
        telemetry.addData("x Power", xPower);
        telemetry.addData("y Power", yPower);
        telemetry.addData("heading Power", headingPower);
    }

    public boolean closeToPosition(Pose2d targetPose, Pose2d currentPose, double xDeadband, double yDeadband, double headingDeadband) {
        if ((Math.abs((targetPose.getX() - currentPose.getX())) < xDeadband) && (Math.abs((targetPose.getY() - currentPose.getY())) < yDeadband) && (Math.abs((targetPose.getHeading() - currentPose.getHeading())) < headingDeadband)) {
            return true;
        } else {
            return false;
        }
    }

    public void resetOdo(Robot robot) {
        robot.center_odometer.reset();
        robot.right_odometer.reset();
        robot.left_odometer.reset();
    }

    public double clipValue(double min, double max, double value) {
        return Math.min(Math.max(value, min), max);
    }

       //if (!resetted1) {
//                    timer1.reset();
//                    resetted1 = true;
//                }
//                if (timer1.seconds() > 2) {
//                    num += 1;
//                    resetted1 = false;
//                }
//
//                targetPose = new Pose2d(54, 17, Rotation2d.fromDegrees(0));
//                if (closeToPosition(new Pose2d(54, 17, Rotation2d.fromDegrees(0)), currentPose, 1, 1.25, 1)) {
//                    num += 1;
//                }
//
//            case 2:
//                liftTarget = 310;
//                if (!resetted1) {
//                    timer1.reset();
//                    resetted1 = true;
//                }
//                if (timer1.seconds() > 1) {
//                    num += 1;
//                    resetted1 = false;
//                }
//                break;
//            case 3:
//                deposit.setDepoLock(MICROOPENED);
//                deposit.setDepoPivot(PIVOTPRE);
//                deposit.setLiftDepos(LIFTSERVPRE);
//                if (!resetted1) {
//                    timer1.reset();
//                    resetted1 = true;
//                }
//                if (timer1.seconds() > 1.5) {
//                    num += 1;
//                    resetted1 = false;
//                }
//                break;
//            case 4:
//                liftTarget = 0;
//                if (!resetted1) {
//                    timer1.reset();
//                    resetted1 = true;
//                }
//                if (timer1.seconds() > 1.5) {
//                    num += 1;
//                    resetted1 = false;
//                }
//                break;
//            case 5:
//                deposit.setDepoPivot(PIVOTFULL);
//                deposit.setLiftDepos(LIFTSERVFULL);
//                if (!resetted1) {
//                    timer1.reset();
//                    resetted1 = true;
//                }
//                if (timer1.seconds() > 0.5) {
//                    num += 1;
//                    resetted1 = false;
//                }
//                break;
//            case 6:
//                intakePower = 0.7;
//                intake.setLock(INTAKELOCKOPENED);
//                deposit.setDepoLock(MICROOPENED);
//                if (!resetted1) {
//                    timer1.reset();
//                    resetted1 = true;
//                }
//                if (timer1.seconds() > 0.8) {
//                    num += 1;
//                    resetted1 = false;
//                }
//                break;
//            case 7:
//                deposit.setDepoLock(MICROCLOSED);
//                liftTarget = 190;
//                if (!resetted1) {
//                    timer1.reset();
//                    resetted1 = true;
//                }
//                if (timer1.seconds() > 1) {
//                    num += 1;
//                    resetted1 = false;
//                }
//                break;
//            case 8:
//                deposit.setDepoPivot(HIGHPIVOTUP);
//                deposit.setLiftDepos(LIFTSERVSUP);
//                if (!resetted1) {
//                    timer1.reset();
//                    resetted1 = true;
//                }
//                if (timer1.seconds() > 1) {
//                    num += 1;
//                    resetted1 = false;
//                }
//                break;
//            case 9:
//                deposit.setDepoPivot(PIVOTINIT);
//                deposit.setLiftDepos(LIFTSERVINIT);
//                if (!resetted1) {
//                    timer1.reset();
//                    resetted1 = true;
//                }
//                if (timer1.seconds() > 0.5) {
//                    num += 1;
//                    resetted1 = false;
//                }
//                break;
//            case 10:
//                liftTarget = 0;
//                intakePower = 0;
//                if (!resetted1) {
//                    timer1.reset();
//                    resetted1 = true;
//                }
//                if (timer1.seconds() > 0.5) {
//                    resetted1 = false;
//                    num += 1;
//                }
//                break;




}