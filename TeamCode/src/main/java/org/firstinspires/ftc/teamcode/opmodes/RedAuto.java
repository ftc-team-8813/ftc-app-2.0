package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.hardware.Capper;
import org.firstinspires.ftc.teamcode.hardware.CapstoneDetector;
import org.firstinspires.ftc.teamcode.hardware.Drivetrain;
import org.firstinspires.ftc.teamcode.hardware.Duck;
import org.firstinspires.ftc.teamcode.hardware.Intake;
import org.firstinspires.ftc.teamcode.hardware.Lift;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.util.Logger;
import org.firstinspires.ftc.teamcode.util.LoopTimer;
import org.firstinspires.ftc.teamcode.util.Persistent;
import org.firstinspires.ftc.teamcode.util.Storage;

@Autonomous(name = "Red Auto")
public class RedAuto extends LoggingOpMode{
    private String name = "Red Auto";
    private Logger log = new Logger(name);
    private Drivetrain drivetrain;
    private Lift lift;
    private Intake intake;
    private Duck duck;
    private CapstoneDetector cap_detector;
    private Capper capper;

    private int main_id = 0;
    private int lift_id = 0;

    private int cap_location;

    private ElapsedTime intake_timer;
    private ElapsedTime lift_timer;
    private ElapsedTime duck_timer;

    private boolean cap_sampled = false;
    private boolean spinning = false;
    private final double spin_time = 4;

    private double PITSTOP;
    private double MAX_HEIGHT;
    private double AUTO_RAISE;
    private double AUTO_ROTATE;
    private double PIVOT_LIFT_TRIGGER;
    private double HOLD_TIME;
    private double CLOSE_CLAW_FREIGHT;
    private double CLOSE_CLAW_DUCK;
    private double OPEN_CLAW;
    private double SWEEPER_UP;
    private double SWEEPER_DOWN;

    private double spinner_speed = 0.0;
    private boolean stop_duck_spin = false;


    @Override
    public void init() {
        super.init();
        Robot robot = Robot.initialize(hardwareMap);
        capper = robot.capper;
        drivetrain = robot.drivetrain;
        lift = robot.lift;
        intake = robot.intake;
        duck = robot.duck;
        cap_detector = robot.cap_detector;

        PITSTOP = Storage.getJsonValue("pitstop");
        MAX_HEIGHT = Storage.getJsonValue("max_height");
        AUTO_RAISE = Storage.getJsonValue("auto_high_raise");
        AUTO_ROTATE = Storage.getJsonValue("auto_high_rotate");
        PIVOT_LIFT_TRIGGER = Storage.getJsonValue("pivot_lift_trigger");
        HOLD_TIME = Storage.getJsonValue("hold_time");
        CLOSE_CLAW_FREIGHT = Storage.getJsonValue("close_claw_freight");
        CLOSE_CLAW_DUCK = Storage.getJsonValue("close_claw_duck");
        OPEN_CLAW = Storage.getJsonValue("open_claw");
        SWEEPER_UP = Storage.getJsonValue("sweeper_up");
        SWEEPER_DOWN = Storage.getJsonValue("sweeper_down");

        intake_timer = new ElapsedTime();
        lift_timer = new ElapsedTime();
        duck_timer = new ElapsedTime();

        intake.deposit(CLOSE_CLAW_FREIGHT);
        capper.init();
    }

    @Override
    public void init_loop() {
        super.init_loop();
        lift.setPowers(-gamepad2.right_stick_y * 0.9,
                       -gamepad2.right_stick_y * 0.9,
                     gamepad2.left_stick_x * 0.5);
        if (lift.liftAtBottom()){
            lift.resetLiftEncoder();
        }
        if (gamepad2.a){
            lift.resetPivotEncoder();
        }

        if (cap_detector.detect_capstone()){
            cap_sampled = true;
        }

        if (cap_sampled){
            telemetry.addData("Finished Initialization", "");
        }

        telemetry.addData("Lift Current: ", lift.getLiftPosition());
        telemetry.addData("Pivot Current: ", lift.getPivotPosition());
        telemetry.update();
    }

    @Override
    public void start() {
        super.start();
        drivetrain.resetEncoders();

        lift.raise(lift.getLiftPosition());
        lift.rotate(lift.getPivotPosition());

        cap_detector.setOpMode(name);
        cap_location = cap_detector.final_location();
        if (cap_sampled = false){
            cap_location = 3;
        }
        if (cap_location == 1){
            AUTO_RAISE = Storage.getJsonValue("auto_low_red_raise");
            AUTO_ROTATE = -Storage.getJsonValue("auto_low_red_rotate");
        } else if (cap_location == 2){
            AUTO_RAISE = Storage.getJsonValue("auto_mid_red_raise");
            AUTO_ROTATE = -Storage.getJsonValue("auto_mid_red_rotate");
        } else if (cap_location == 3){
            AUTO_RAISE = Storage.getJsonValue("auto_high_red_raise");
            AUTO_ROTATE = -Storage.getJsonValue("auto_high_red_rotate");
        } else {
            AUTO_RAISE = Storage.getJsonValue("auto_high_red_raise");
            AUTO_ROTATE = -Storage.getJsonValue("auto_high_red_rotate");
        }
        log.i("Cap Height: %d", cap_location);
    }

    @Override
    public void loop() {
        switch (main_id) {
            case 0:
                drivetrain.autoSpeed(0.3,0.45);
                main_id += 1;
                break;
            case 1:
                drivetrain.autoMove(-1750,100,0);
                break;
            case 2:
                switch (lift_id){
                    case 0:
                        lift.raise(AUTO_RAISE);
                        if (lift.liftReached()) lift_id += 1;
                        break;
                    case 1:
                        lift.rotate(AUTO_ROTATE);
                        if (lift.pivotReached()){
                            lift_id += 1;
                            main_id += 1;
                        }
                        break;
                }
                break;
            case 3:
                drivetrain.autoMove(200, 0, 0);
                break;
            case 4:
                switch (lift_id){
                    case 2:
                        lift_timer.reset();
                        intake.deposit(OPEN_CLAW);
                        lift_id += 1;
                        break;
                    case 3:
                        if (lift_timer.seconds() > HOLD_TIME + 0.5) {
                            if (AUTO_RAISE > 50000){
                                main_id = 6;
                            } else {
                                main_id += 1;
                            }
                            lift_id += 1;
                        }
                        break;
                }
                break;
            case 5:
                drivetrain.autoMove(-200, 0, 0);
                break;
            case 6:
                switch (lift_id){
                    case 4:
                        lift.raise(PITSTOP);
                        if (lift.liftReached()) lift_id += 1;
                        break;
                    case 5:
                        lift.rotate(0);
                        if (lift.pivotReached()) lift_id += 1;
                        break;
                    case 6:
                        lift.raise(0);
                        if (lift.liftReached()) lift_id += 1;
                        break;
                    case 7:
                        lift_id = -1;
                        main_id += 1;
                        drivetrain.autoSpeed(0.37,0.45);
                        break;
                }
                break;
            case 7:
                drivetrain.autoSpeed(0.25,0.45);
                if (AUTO_RAISE > 50000){
                    drivetrain.autoMove(1220, 695, 0);
                } else {
                    drivetrain.autoMove(1465, 665, 0);
                }
                duck.spin(0.01);
                break;
            case 8:
                duck_timer.reset();
                main_id += 1;
                break;
            case 9:
                duck_spin();
                drivetrain.autoSpeed(0.3,0.55);
                break;
            case 10:
                drivetrain.autoMove(150, -50,3);
                intake.setPower(.6);
                break;
            case 11:
                drivetrain.autoMove(-250, 0,24);
                break;
            case 12:
                drivetrain.autoMove(350, -100,0);
                drivetrain.autoSpeed(.45,.55);
                break;
            case 13:
                drivetrain.autoMove(0,0,45);
                break;
            case 14:
                drivetrain.autoSpeed(.6,.45);
                drivetrain.autoMove(420, 350,0);
                break;
            case 15:
                drivetrain.autoMove(-130,-800,-35);
                intake.deposit(CLOSE_CLAW_DUCK);
                intake.setPower(0);
                lift_id = 0;
                break;
            case 16:
                switch (lift_id){
                    case 0:
                        lift.raise(63000);
                        if (lift.liftReached()) lift_id += 1;
                        break;
                    case 1:
                        lift.rotate(-54);
                        if (lift.pivotReached()) {
                            lift_timer.reset();
                            intake.deposit(OPEN_CLAW);
                            lift_id += 1;
                        }
                        break;
                    case 2:
                        if (lift_timer.seconds() > HOLD_TIME + 0.5) lift_id += 1;
                        break;
                    case 3:
                        lift.raise(PITSTOP);
                        if (lift.liftReached()) lift_id += 1;
                        break;
                    case 4:
                        lift.rotate(0);
                        if (lift.pivotReached()) lift_id += 1;
                        break;
                    case 5:
                        lift.raise(0);
                        if (lift.liftReached()) lift_id += 1;
                        break;
                    case 6:
                        lift_id = -1;
                        main_id += 1;
                        break;
                }
                break;
            case 17:
                drivetrain.autoSpeed(.4, .45);
                drivetrain.autoMove(-1150, 150, 13);
                break;
        }

        lift.update();
        drivetrain.update(telemetry);

        if (drivetrain.ifReached() && main_id != 16 || if_spinned()) {
            main_id += 1;
            intake_timer.reset();
        }

        if (intake.getPower() > 0){
            if (intake.freightDetected()){
                intake.deposit(CLOSE_CLAW_DUCK);
            }
        }

        telemetry.addData("Main ID: ", main_id);
        telemetry.addData("Lift ID: ", lift_id);
        telemetry.addData("Heading: ", drivetrain.getHeading());
        telemetry.addData("Forward: ", drivetrain.getForwardPosition());
        telemetry.addData("Strafe: ", drivetrain.getStrafePosition());
        telemetry.addData("Duck Speed: ", spinner_speed);
        telemetry.addData("Duck Timer: ", duck_timer.seconds());
        telemetry.addData("Loop Time: ", LoopTimer.getLoopTime());
        telemetry.update();

        LoopTimer.resetTimer();
    }

    @Override
    public void stop() {
        super.stop();
        Persistent.put("Color", name);
    }

    public void duck_spin() {
        double power = duck_timer.seconds() / 12;
//        double power = Math.pow(6.5, duck_timer.seconds() - spin_time);
        duck.spin(-power);
        spinning = true;
    }

    public boolean if_spinned(){
        if (duck_timer.seconds() > spin_time && spinning){
            duck.spin(0);
            spinning = false;
            return true;
        }
        return false;
    }
}
