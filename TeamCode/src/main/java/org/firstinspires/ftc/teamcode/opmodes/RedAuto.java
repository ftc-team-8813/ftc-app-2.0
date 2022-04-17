package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.hardware.CapstoneDetector;
import org.firstinspires.ftc.teamcode.hardware.Drivetrain;
import org.firstinspires.ftc.teamcode.hardware.Duck;
import org.firstinspires.ftc.teamcode.hardware.Intake;
import org.firstinspires.ftc.teamcode.hardware.Lift;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.util.Logger;
import org.firstinspires.ftc.teamcode.util.LoopTimer;
import org.firstinspires.ftc.teamcode.util.Storage;

@Autonomous(name = "Red Auto")
public class RedAuto extends LoggingOpMode{
    private Logger log = new Logger("Red Auto");
    private Drivetrain drivetrain;
    private Lift lift;
    private Intake intake;
    private Duck duck;
    private CapstoneDetector cap_detector;

    private int main_id = 0;
    private int lift_id = 0;

    private int cap_location;

    private ElapsedTime intake_timer;
    private ElapsedTime lift_timer;
    private ElapsedTime duck_timer;

    private boolean lift_reset;
    private boolean cap_sampled = false;
    private boolean spinning = false;

    private double PITSTOP;
    private double MAX_HEIGHT;
    private double AUTO_RAISE;
    private double AUTO_ROTATE;
    private double AUTO_TURN;
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
        drivetrain = robot.drivetrain;
        lift = robot.lift;
        intake = robot.intake;
        duck = robot.duck;
        cap_detector = robot.cap_detector;

        PITSTOP = Storage.getJsonValue("pitstop");
        MAX_HEIGHT = Storage.getJsonValue("max_height");
        AUTO_RAISE = Storage.getJsonValue("auto_high_raise");
        AUTO_ROTATE = Storage.getJsonValue("auto_high_rotate");
        AUTO_TURN = Storage.getJsonValue("auto_high_turn");
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
    }

    @Override
    public void init_loop() {
        super.init_loop();
        lift.resetLift();

        if (cap_detector.detect_capstone()){
            cap_sampled = true;
        }

        if (lift.getPivotReset() && cap_sampled){
            telemetry.addData("Finished Initialization", "");
            telemetry.update();
        }
    }

    @Override
    public void start() {
        super.start();
        cap_detector.setOpMode("Red");
        cap_location = cap_detector.final_location();
        if (cap_sampled = false){
            cap_location = 3;
        }
        if (cap_location == 1){
            AUTO_TURN = -Storage.getJsonValue("auto_low_turn");
            AUTO_RAISE = Storage.getJsonValue("auto_low_raise");
            AUTO_ROTATE = -Storage.getJsonValue("auto_low_rotate");
        } else if (cap_location == 2){
            AUTO_TURN = -Storage.getJsonValue("auto_mid_turn");
            AUTO_RAISE = Storage.getJsonValue("auto_mid_raise");
            AUTO_ROTATE = -Storage.getJsonValue("auto_mid_rotate");
        } else if (cap_location == 3){
            AUTO_TURN = -Storage.getJsonValue("auto_high_turn");
            AUTO_RAISE = Storage.getJsonValue("auto_high_raise");
            AUTO_ROTATE = -Storage.getJsonValue("auto_high_rotate");
        } else {
            AUTO_TURN = -Storage.getJsonValue("auto_high_turn");
            AUTO_RAISE = Storage.getJsonValue("auto_high_raise");
            AUTO_ROTATE = -Storage.getJsonValue("auto_high_rotate");
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
                drivetrain.autoMove(-1500,-100,0);
                break;
            case 2:
                switch (lift_id){
                    case 0:
//                        lift.raise(MAX_HEIGHT - 500);
//                        if (lift.liftReached()) lift_id += 1;
//                        break;
//                    case 1:
                        lift.raise(AUTO_RAISE);
                        if (lift.liftReached()) lift_id += 2;
                        break;
                    case 2:
                        lift.rotate(AUTO_ROTATE);
                        if (lift.pivotReached()) lift_id += 1;
                        break;
                    case 3:
//                        lift.raise(AUTO_RAISE);
//                        if (lift.liftReached()) {
//                            lift_timer.reset();
//                            intake.deposit(OPEN_CLAW);
////                            drivetrain.autoMove(-100,0,0);
//                            lift_id += 1;
//                        }
                        lift_timer.reset();
                        intake.deposit(OPEN_CLAW);
                        lift_id += 1;
                        break;
                    case 4:
                        if (lift_timer.seconds() > HOLD_TIME + 0.5) lift_id += 1;
                        break;
                    case 5:
                        lift.raise(PITSTOP);
                        if (lift.liftReached()) lift_id += 1;
                        break;
                    case 6:
                        lift.rotate(0);
                        if (lift.pivotReached()) lift_id += 1;
                        break;
                    case 7:
                        lift.raise(0);
                        if (lift.liftReached()) lift_id += 1;
                        break;
                    case 8:
                        lift_id = -1;
                        drivetrain.autoSpeed(0.37,0.45);
                        break;
                }
                if (lift_id == -1) main_id += 1;
                break;
            case 3:
                drivetrain.autoMove(1400, 925, 0);
                break;
            case 4:
                duck_timer.reset();
                main_id += 1;
            case 5:
                duck_spin();
                break;
            case 6:
                drivetrain.autoMove(-200, -100,24);
                intake.setPower(.7);
                break;
            case 7:
                drivetrain.autoMove(425, -125,0);
                drivetrain.autoSpeed(.45,.3);
                break;
            case 8:
                drivetrain.autoMove(0,0,60);
                break;
            case 9:
                drivetrain.autoSpeed(.6,.45);
                drivetrain.autoMove(400, 405,0);
                break;
            case 10:
                drivetrain.autoMove(-120,-800,-27);
                intake.deposit(CLOSE_CLAW_DUCK);
                intake.setPower(0);
                lift_id = 0;
                break;
            case 11:
                switch (lift_id){
                    case 0:
                        lift.raise(60000);
                        if (lift.liftReached()) lift_id = 1;
                        break;
                    case 1:
                        lift.rotate(-54);
                        if (lift.pivotReached()) {
//                        break;
//                    case 2:
//
//                        if (lift.liftReached()) {
                            lift_timer.reset();
                            intake.deposit(OPEN_CLAW);
                            lift_id += 2;
                        }
                        break;
                    case 3:
                        if (lift_timer.seconds() > HOLD_TIME + 0.5) lift_id += 1;
                        break;
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
                        lift_id = 0;
                        main_id += 1;
                        break;
                }
                break;
            case 12:
                drivetrain.autoMove(-1150, 0, 13);
                break;
        }

        lift.update();
        drivetrain.update(telemetry);

        if (drivetrain.ifReached() && main_id != 2 && main_id != 11 || if_spinned()) {
            main_id += 1;
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

    public void duck_spin() {
        duck.spin((duck_timer.seconds() / 10));
        spinning = true;
    }

    public boolean if_spinned(){
        if (duck_timer.seconds() > 3 && spinning){
            duck.spin(0);
            spinning = false;
            return true;
        }
        return false;
    }
}
