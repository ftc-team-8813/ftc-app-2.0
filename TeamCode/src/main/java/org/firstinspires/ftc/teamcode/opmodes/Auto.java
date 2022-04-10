package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.hardware.Drivetrain;
import org.firstinspires.ftc.teamcode.hardware.Duck;
import org.firstinspires.ftc.teamcode.hardware.Intake;
import org.firstinspires.ftc.teamcode.hardware.Lift;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.util.Storage;

@Autonomous(name = "Red Auto")
public class Auto extends LoggingOpMode{

    private Drivetrain drivetrain;
    private Lift lift;
    private Intake intake;
    private Duck duck;

    private int main_id = 0;
    private int lift_id = -1;

    private ElapsedTime timer;

    private boolean updated;

    private double PITSTOP;
    private double HIGH_RAISE;
    private double HIGH_ROTATE;
    private double PIVOT_LIFT_TRIGGER;
    private double HOLD_TIME;
    private double CLOSE_CLAW_FREIGHT;
    private double OPEN_CLAW;
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

        PITSTOP = Storage.getJsonValue("pitstop");
        HIGH_RAISE = Storage.getJsonValue("high_raise");
        HIGH_ROTATE = Storage.getJsonValue("high_rotate");
        PIVOT_LIFT_TRIGGER = Storage.getJsonValue("pivot_lift_trigger");
        HOLD_TIME = Storage.getJsonValue("hold_time");
        CLOSE_CLAW_FREIGHT = Storage.getJsonValue("close_claw_freight");
        OPEN_CLAW = Storage.getJsonValue("open_claw");
        timer = new ElapsedTime();
        intake.deposit(CLOSE_CLAW_FREIGHT);
    }

    @Override
    public void loop() {
        switch (main_id) {
            case 0:
                drivetrain.autoMove(-475,-200,0);
                if (drivetrain.ifReached()) {
                    main_id += 1;
                }
                break;
            case 1:
                drivetrain.autoMove(0,0,58);
                if (drivetrain.ifReached()) {
                    timer.reset();
                    updated = false;
                    main_id += 1;
                }
                break;
            case 2:
                //lift stuff
                if (updated) {
                    main_id += 1;
                }
                break;
            case 3:
                drivetrain.autoMove(0, 0, -30);
                if (drivetrain.ifReached()) {
                    updated = false;
                    main_id += 1;
                }
                break;
            case 4:
                if (updated) {
                    main_id += 1;
                }
                break;
            case 5:
                drivetrain.autoMove(-75, 1000,0);
                if (drivetrain.ifReached()) {
                    updated = false;
                    main_id += 1;
                }
                break;
            case 6:
                if (updated) {
                    main_id += 1;
                }
            case 7:
                duck_spin();
                break;
        }

        switch (lift_id){
            case 0:
                lift.raise(PITSTOP);
                if (lift.liftReached()) lift_id += 1;
                break;
            case 1:
                lift.rotate(HIGH_ROTATE);
                if (lift.getPivotPosition() > PIVOT_LIFT_TRIGGER && HIGH_RAISE > PITSTOP){
                    lift_id += 1;
                } else if (lift.pivotReached()){
                    lift_id += 1;
                }
                break;
            case 2:
                lift.raise(HIGH_RAISE);
                if (lift.liftReached()) {
                    timer.reset();
                    intake.deposit(OPEN_CLAW);
                    lift_id += 1;
                }
                break;
            case 3:
                if (timer.seconds() > HOLD_TIME) {
                    lift_id += 1;
                }
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
            case 7:
                lift_id = -1;
                main_id += 1;
                break;
        }

//        if (drivetrain.ifReached()){
//            main_id += 1;
//        }

        //lift.update();
        drivetrain.update(telemetry);

        telemetry.addData("Main ID: ", main_id);
        telemetry.addData("Lift ID: ", lift_id);
        telemetry.addData("Heading: ", drivetrain.getHeading());
        telemetry.addData("Forward: ", drivetrain.getForwardPosition());
        telemetry.addData("Strafe: ", drivetrain.getStrafePosition());
        telemetry.addData("Duck Speed: ", spinner_speed);
        telemetry.addData("Duck Timer: ", timer.seconds());
        telemetry.update();

        updated = true;
    }
    public void duck_spin() {
        duck.spin(spinner_speed);
        stop_duck_spin = timer.seconds() >= 2.8;
        if (stop_duck_spin == true) {
            spinner_speed = 0.0;
           // main_id += 1;
        } else {
            spinner_speed = -(timer.seconds() / 8);
        }
    }
}
