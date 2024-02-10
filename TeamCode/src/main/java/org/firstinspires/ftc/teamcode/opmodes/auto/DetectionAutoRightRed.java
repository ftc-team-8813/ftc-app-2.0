package org.firstinspires.ftc.teamcode.opmodes.auto;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.controller.PIDController;
import com.arcrobotics.ftclib.controller.PIDFController;
import com.arcrobotics.ftclib.geometry.Pose2d;
import com.arcrobotics.ftclib.geometry.Rotation2d;
import com.arcrobotics.ftclib.kinematics.HolonomicOdometry;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.hardware.Camera;
import org.firstinspires.ftc.teamcode.hardware.Deposit;
import org.firstinspires.ftc.teamcode.hardware.DistanceSensors;
import org.firstinspires.ftc.teamcode.hardware.Drivetrain;
import org.firstinspires.ftc.teamcode.hardware.Horizontal;
import org.firstinspires.ftc.teamcode.hardware.Intake;
import org.firstinspires.ftc.teamcode.hardware.Lift;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.hardware.navigation.ColorPipeline;
import org.firstinspires.ftc.teamcode.opmodes.LoggingOpMode;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

@Config
@Autonomous(name = "!!Detection Right Red!!")
public class DetectionAutoRightRed extends LoggingOpMode {
//    private Camera camera;
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
    private HolonomicOdometry odometry;
    private Drivetrain drivetrain;
    private Lift lift;
    private Intake intake;
    private Deposit deposit;
    private Horizontal horizontal;
    private DistanceSensors sensors;
    private Pose2d currentPose;
    private Pose2d targetPose;
    private String location;
    private int ID;
    private int num;
    private double intakePower;
    private double liftCurrent;
    private double horizCurrent;
    private double horizTarget;
    private FtcDashboard dashboard;
    private double liftTarget;
    private PIDFController horizPID = new PIDFController(0.027, 0, 0, 0);
    private PIDFController liftPID = new PIDFController(0.02, 0, 0, 0);
    private PIDController xCont = new PIDController(0.03, 0, 0);
    private PIDController yCont = new PIDController(0.07, 0, 0.07);
    private PIDController headingCont = new PIDController(0.07, 0.125, 0.005);

//    public static double kpX = 0.03;
//    public static double kiX = 0.07;
//    public static double kdX = 0;
//    private PIDController xCont = new PIDController(kpX, kiX, kdX);
    private ElapsedTime timer;
    private ElapsedTime timer2;
    private ElapsedTime timer3;
    private ElapsedTime timer4;
    private ElapsedTime timer5;
    private ElapsedTime timer6;
    private ElapsedTime loopTimer;
    private boolean odoMove;
    private boolean resetted;
    private boolean resetted2;
    private boolean resetted3;
    private boolean resetted4;
    private boolean resetted5;
    private boolean resetted6;
    private OpenCvCamera camera1;
    private ColorPipeline color_pipeline;
    private String result = "Nothing";
    @Override
    public void init() {
        super.init();
        Robot robot = Robot.initialize(hardwareMap);
//        camera = robot.camera;
        odometry = robot.odometry;
        drivetrain = robot.drivetrain;
        sensors = robot.sensors;
        lift = robot.lift;
        deposit = robot.deposit;
        horizontal = robot.horiz;
        intake = robot.intake;
        dashboard = FtcDashboard.getInstance();
        num = 0;
        intakePower = 0;
        liftCurrent = 0;
        horizCurrent = 0;
        liftTarget = 0;
        horizTarget = 0;

        currentPose = new Pose2d(0, 0, new Rotation2d(0));
        targetPose = new Pose2d(0, 0, new Rotation2d(0));

//        timer = new ElapsedTime();
//        timer2 = new ElapsedTime();
//        timer3 = new ElapsedTime();
//        timer4 = new ElapsedTime();
//        timer5 = new ElapsedTime();
//        timer6 = new ElapsedTime();
//
//        resetted = false;
//        resetted2 = false;
//        resetted3 = false;
//        resetted4 = false;
//        resetted5 = false;
//        resetted6 = false;

        loopTimer = new ElapsedTime();
        odometry.updatePose(new Pose2d(0, 0, new Rotation2d(0)));
        odoMove = true;
        odometry.update(0, 0, 0);

        robot.center_odometer.reset();
        robot.right_odometer.reset();
        robot.left_odometer.reset();

        lift.resetEncoders();
        horizontal.resetEncoders();
        intake.setLock(INTAKELOCKOPENED);

        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        camera1 = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam 1"), cameraMonitorViewId);
        color_pipeline = new ColorPipeline(410,200,190,230,"close red"); //far red and close  blue

        camera1.setPipeline(color_pipeline);

        camera1.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener()
        {
            @Override
            public void onOpened()
            {
                camera1.startStreaming(640,480, OpenCvCameraRotation.UPRIGHT);
            }

            @Override
            public void onError(int errorCode)
            {

            }
        });

    }

    @Override
    public void init_loop() {
        super.init_loop();
        odometry.updatePose(new Pose2d(0, 0, new Rotation2d(0)));
        odometry.update(0, 0, 0);

        location = color_pipeline.getLocation();

        telemetry.addLine(location);

        if(location.equals("center")){
            ID = 5;
        }else if(location.equals("left")){
            ID = 4;
        }else{
            ID = 6;
        }

//        if ((sensors.getRightDistance() > 600) && (sensors.getRightDistance() < 800)) {
//            ID = 6;
//        } else if ((sensors.getLeftDistance() > 600) && (sensors.getLeftDistance() < 800)) {
//            ID = 5;
//        } else {
//            ID = 4;
//        }
//
//        telemetry.addData("Detected", ID);
//        telemetry.update();

    }

    @Override
    public void loop() {
        loopTimer.reset();
        lift.update();
        horizontal.update();
        odometry.updatePose();

        currentPose = odometry.getPose();
        liftCurrent = lift.getCurrentPosition();
        horizCurrent = horizontal.getCurrentPosition();

        switch (num) {
            case 0:
                targetPose = new Pose2d(14, -30, Rotation2d.fromDegrees(-90));
                if(closeToPosition(currentPose, targetPose, 10, 10,3)){
                    num += 1;
                }
                break;
            case 1:
                odoMove = false;
//                moveTowardsID(camera.getDetection(ID), drivetrain, telemetry);


        }
        if(odoMove){
            autoMove(targetPose, currentPose, telemetry, drivetrain, xCont, yCont, headingCont);
        }
        horizontal.setHorizPwr(horizPID.calculate(horizCurrent, horizTarget));
        lift.setLiftsPower(liftPID.calculate(liftCurrent, liftTarget));
        intake.setPower(-intakePower);
        telemetry.addData("Loop Times", loopTimer.seconds());
        telemetry.addData("Current Pose", currentPose);
        telemetry.addData("Target Pose", targetPose);
        telemetry.addData("Case", num);
        telemetry.addData("ID", ID);

//        moveTowardsID(camera.getDetection(6), drivetrain, telemetry);
//            case 2: //place pixel
//                intakePower = -0.1;
//                if(!resetted){
//                    timer.reset();
//                    resetted = true;
//                }
//                if(timer.seconds() > 0.1){
//                    resetted = false;
//                    num += 1;
//                    break;
//                }
//            case 3: //face towards board
//                intakePower = 0;
//                horizTarget = 0;
//                if(!resetted){
//                    timer.reset();
//                    resetted = true;
//                }
//                if(timer.seconds() > 0.3){
//                    targetPose = new Pose2d(0, 0, new Rotation2d(0));
//                    if(closeToPosition(targetPose, currentPose, 10, 10, 4)){
//                        resetted = false;
//                        num += 1;
//                        break;
//                    }
//                }
//            case 4: //go to april tag
//                odoMove = false;
//                moveTowardsID(camera.getDetection(ID), drivetrain, telemetry);
//                if(sensors.getRed() < 0 && sensors.getRed() > 0){ //change
//                    num += 1;
//                    break;
//                }
//            case 5: //lift up
//                liftTarget = 150;
//                if(!resetted){
//                    timer.reset();
//                    resetted = true;
//                }
//                if(timer.seconds() > 0.1){
//                    resetted = false;
//                    num += 1;
//                    break;
//                }
//            case 6: //lift pos
//                deposit.setLiftDepos(0.109);
//                if(!resetted){
//                    timer.reset();
//                    resetted = true;
//                }
//                if(timer.seconds() > 0.2){
//                    resetted = false;
//                    num += 1;
//                    break;
//                }
//            case 7: //pivot pos
//                deposit.setDepoPivot(0.027);
//                if(!resetted){
//                    timer.reset();
//                    resetted = true;
//                }
//                if(timer.seconds() > 0.2){
//                    resetted = false;
//                    num += 1;
//                    break;
//                }
//            case 8: //openDepo
//                deposit.setDepoLock(0);
//                if(!resetted){
//                    timer.reset();
//                    resetted = true;
//                }
//                if(timer.seconds() > 0.15){
//                    resetted = false;
//                    num += 1;
//                    break;
//                }
//            case 9: //go to corner
//                odoMove = true;
//                atargetPose = new Pose2d(0, 0, new Rotation2d(0));
//                if(closeToPosition(targetPose, currentPose, 10, 10, 4)){
//                    num += 1;
//                    break;
//                }
//            case 10: //set to pre
//                deposit.setDepoPivot(0.03);
//                if(!resetted){
//                    timer.reset();
//                    resetted = true;
//                }
//                if(timer.seconds() > 0.2){
//                    resetted = false;
//                    num += 1;
//                    break;
//                }
//            case 11: //set depoLock pre
//                deposit.setDepoLock(0.51);
//                if(!resetted){
//                    timer.reset();
//                    resetted = true;
//                }
//                if(timer.seconds() > 0.2){
//                    resetted = false;
//                    num += 1;
//                    break;
//                }
//            case 12: //goDown
//                liftTarget = 0;
//                break;
//                //!!!starts at pre position before TeleOP!!!!
//        }


    }
    public void autoMove(Pose2d targetPose, Pose2d currentPose, Telemetry telemetry, Drivetrain drivetrain, PIDController xCont, PIDController yCont, PIDController headingCont) {
        //change PIDs here

        double xPower = xCont.calculate(currentPose.getY(), targetPose.getY());
        double headingPower = headingCont.calculate(currentPose.getHeading(), targetPose.getHeading());
        double yPower = yCont.calculate(currentPose.getX(), targetPose.getX());

        drivetrain.move(-yPower, xPower, -headingPower, 0);

        telemetry.addData("x Power", xPower);
        telemetry.addData("y Power", yPower);
        telemetry.addData("heading Power", headingPower);
    }
    public void moveTowardsID(AprilTagDetection detection, Drivetrain drivetrain, Telemetry telemetry){
        if(detection != null){
            PIDController xCont = new PIDController(0.041,0,0.3);
            PIDController yCont = new PIDController(0.01,0,0);
            PIDController zCont = new PIDController(0.00005,0,0);

            double xPower = xCont.calculate(0, detection.ftcPose.x);
            double yPower = yCont.calculate(0, (detection.ftcPose.y + 6));
            double zPower = zCont.calculate(0, (detection.ftcPose.z));

            drivetrain.move(-yPower, -xPower, zPower,0);
//            drivetrain.move(0, -xPower, 0,0);


            telemetry.addData("x Power", xPower);
            telemetry.addData("y Power", yPower);
            telemetry.addData("z Power", zPower);
            telemetry.addData("x Distance", detection.ftcPose.x);
            telemetry.addData("y Distance", detection.ftcPose.y);
            telemetry.addData("y Distance", detection.ftcPose.z);
        }
    }
    public boolean closeToPosition(Pose2d currentPose, Pose2d targetPose, double xDeadband, double yDeadband, double headingDeadband){
        if((Math.abs((targetPose.getX() - currentPose.getX())) < xDeadband) && (Math.abs((targetPose.getY() - currentPose.getY())) < yDeadband) && (Math.abs((targetPose.getHeading() - currentPose.getHeading())) < headingDeadband)){
            return true;
        }else{
            return false;
        }
    }
}

//case 0:
//                deposit.setDepoPivot(0.62);
//                deposit.setDepoLock(0.85);
//
//                if (!resetted) {
//                    timer.reset();
//                    resetted = true;
//                }
//                if (timer.seconds() > 1) {
//                    if (ID == 5) {
//                        targetPose = new Pose2d(24.5, -5, new Rotation2d(0));
//                    }
//                    if (ID == 6) {
//                        targetPose = new Pose2d(15.2, -13.93, new Rotation2d(0)); //6
//                    }
//                    if (ID == 4) {
//                        if (!resetted3) {
//                            timer3.reset();
//                            resetted3 = true;
//                        }
//                        if (timer3.seconds() < 3.5) {
//                            xCont = new PIDController(0.03, 0, 0);
//                            yCont = new PIDController(0.07, 0, 0.07);
//
//                            targetPose = new Pose2d(19, 2, Rotation2d.fromDegrees(0));
//                        } else {
//                            xCont = new PIDController(0, 0, 0);
//                            yCont = new PIDController(0, 0, 0);
//
//                            targetPose = new Pose2d(19, 2, Rotation2d.fromDegrees(45));
//                        }
//                    }
//                    if (!resetted2) {
//                        timer2.reset();
//                        resetted2 = true;
//                    }
//                    if (timer2.seconds() > 5) {
//                        resetted2 = false;
//                        resetted = false;
//                        resetted3 = false;
//                        num += 1;
//                    }
//                }
//                break;
//
//            case 1:
//                deposit.setDepoLock(0.2);
//
//                if (!resetted) {
//                    timer.reset();
//                    resetted = true;
//                }
//                if (timer.seconds() > 0.5) {
//                    resetted = false;
//                    num += 1;
//                }
//                break;
//            case 2:
//                deposit.setDepoPivot(0.95);
//                break;


//            case 2:
//                if (!resetted) {
//                    timer.reset();
//                    resetted = true;
//                }
//                if (timer.seconds() > 1.5) {
//                    liftTarget = 310;
//                    if (!resetted2) {
//                        timer2.reset();
//                        resetted2 = true;
//                    }
//                    if (timer2.seconds() > 0.5) {
//                        deposit.setDepoPivot(PIVOTPRE);
//                        deposit.setLiftDepos(LIFTSERVPRE);
//                        if (!resetted3) {
//                            timer3.reset();
//                            resetted3 = true;
//                        }
//                        if (timer3.seconds() > 1.5) {
//                            liftTarget = 0;
//                            if (!resetted4) {
//                                timer4.reset();
//                                resetted4 = true;
//                            }
//                            if (timer4.seconds() > 0.5) {
//                                deposit.setDepoPivot(PIVOTFULL);
//                                deposit.setLiftDepos(LIFTSERVFULL);
//                                if (!resetted5) {
//                                    timer5.reset();
//                                    resetted5 = true;
//                                }
//                                if (timer5.seconds() > 0.5) {
//                                    intakePower = 0.7;
//                                    if(!resetted6){
//                                        timer6.reset();
//                                        resetted6 = true;
//                                    }
//                                    if(timer6.seconds() > 1){
//                                        deposit.setDepoLock(MICROCLOSED);
//                                        resetted = false;
//                                        resetted2 = false;
//                                        resetted3 = false;
//                                        resetted4 = false;
//                                        resetted5 = false;
//                                        resetted6 = false;
//                                        num += 1;
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//                break;
//            case 3:
//                targetPose = new Pose2d(15, -35, Rotation2d.fromDegrees(0));
//                if(!resetted){
//                    timer.reset();
//                    resetted = true;
//                }
//                if(timer.seconds() > 1){
//                    xCont = new PIDController(0, 0, 0);
//                    yCont = new PIDController(0, 0, 0);
//
//                    targetPose = new Pose2d(35, 0, Rotation2d.fromDegrees(-90));
//                }
//                break;
//            case 4:
//                moveTowardsID(camera.getDetection(ID), drivetrain, telemetry);
