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
public class Auto extends LoggingOpMode{
    private Logger log = new Logger("Auto");
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
    private boolean cap_sampled;
    private boolean spinning = false;

    private double PITSTOP;
    private double AUTO_RAISE;
    private double AUTO_ROTATE;
    private double PIVOT_LIFT_TRIGGER;
    private double HOLD_TIME;
    private double CLOSE_CLAW_FREIGHT;
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
        AUTO_RAISE = Storage.getJsonValue("auto_mid_raise");
        AUTO_ROTATE = Storage.getJsonValue("auto_mid_rotate");
        PIVOT_LIFT_TRIGGER = Storage.getJsonValue("pivot_lift_trigger");
        HOLD_TIME = Storage.getJsonValue("hold_time");
        CLOSE_CLAW_FREIGHT = Storage.getJsonValue("close_claw_freight");
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
        if (lift.resetLift()){
            lift_reset = true;
        }
        if (cap_detector.detect_capstone()){
            cap_sampled = true;
        }
        if (lift_reset && cap_sampled){
            telemetry.addData("Finished Initialization", "");
            telemetry.update();
        }
    }

    @Override
    public void start() {
        super.start();
        cap_location = cap_detector.final_location();
    }

    @Override
    public void loop() {
        switch (main_id) {
            case 0:
                drivetrain.autoMove(-405,-100,0);
                break;
            case 1:
                drivetrain.autoMove(0,0,58);
                break;
            case 2:
                switch (lift_id){
                    case 0:
                        lift.raise(PITSTOP);
                        if (lift.liftReached()) lift_id += 1;
                        break;
                    case 1:
                        lift.rotate(-AUTO_ROTATE);
                        if (lift.pivotReached()) lift_id += 1;
                        break;
                    case 2:
                        lift.raise(AUTO_RAISE);
                        if (lift.liftReached()) {
                            lift_timer.reset();
                            intake.deposit(OPEN_CLAW);
                            lift_id += 1;
                        }
                        break;
                    case 3:
                        if (lift_timer.seconds() > HOLD_TIME) drivetrain.autoMove(0,0,10); lift_id += 1;
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
            case 3:
                drivetrain.autoMove(0, 0, -40);
                break;
            case 4:
                drivetrain.autoMove(-375, 1000,0);
                break;
            case 5:
                duck_timer.reset();
                main_id += 1;
            case 6:
                duck_spin();
                break;
        }

        lift.updateLift();
        lift.updatePivot();
        drivetrain.update(telemetry);

        if (drivetrain.ifReached() || if_spinned()) {
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
        duck.spin(-(duck_timer.seconds() / 10));
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
