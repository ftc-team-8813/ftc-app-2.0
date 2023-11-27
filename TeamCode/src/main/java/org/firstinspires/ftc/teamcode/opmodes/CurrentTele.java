//package org.firstinspires.ftc.teamcode.opmodes.teleop;
//
//import com.acmerobotics.dashboard.config.Config;
//import com.arcrobotics.ftclib.command.CommandOpMode;
//import com.arcrobotics.ftclib.command.CommandScheduler;
//import com.arcrobotics.ftclib.command.button.GamepadButton;
//import com.arcrobotics.ftclib.gamepad.GamepadEx;
//import com.arcrobotics.ftclib.gamepad.GamepadKeys;
//import com.arcrobotics.ftclib.hardware.motors.Motor;
//import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
//import com.qualcomm.robotcore.hardware.DcMotorEx;
//
//import org.firstinspires.ftc.teamcode.hardware.Drivetrain;
//import org.firstinspires.ftc.teamcode.hardware.Lift;
//
//@Config
//@TeleOp(name = "CRI TeleOp")
//public class CRITeleOp extends CommandOpMode {
//
//    private GamepadEx gamepadEx1, gamepadEx2;
//    private Drivetrain drivetrain;
//    private Lift lift;
//
//    private Motor front_left;
//    private Motor front_right;
//    private Motor back_left;
//    private Motor back_right;
//
//    private Motor lift1;
//    private Motor lift2;
//
//
//
//    @Override
//    public void initialize() {
//        gamepadEx1 = new GamepadEx(gamepad1);
//        gamepadEx2 = new GamepadEx(gamepad2);
//
//        front_left = new Motor(hardwareMap, "FL");
//        front_right = new Motor(hardwareMap, "FR");
//        back_left = new Motor(hardwareMap, "BL");
//        back_right = new Motor(hardwareMap, "BR");
//
//        lift1 = new Motor(hardwareMap, "l1");
//        lift2 = new Motor(hardwareMap, "l2");
//
//
//        drivetrain = new Drivetrain(front_left, front_right, back_left, back_right);
//
//        new GamepadButton(gamepadEx2, GamepadKeys.Button.DPAD_UP).whenPressed(() -> Lift.Heights.HIGH);
//    }
//
//    @Override
//    public void run() {
//        super.run();
//        drivetrain.move(-gamepadEx1.getRightX(), gamepadEx1.getLeftX(), -gamepadEx1.getLeftY(), 0);
//
//    }
//}

package org.firstinspires.ftc.teamcode.opmodes;
//import com.acmerobotics.dashboard.FtcDashboard;
//import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
//import org.firstinspires.ftc.teamcode.opmodes.teleop.ClawControl;
import org.firstinspires.ftc.teamcode.opmodes.teleop.ControlMgr;
import org.firstinspires.ftc.teamcode.opmodes.teleop.DriveControl;
import org.firstinspires.ftc.teamcode.opmodes.teleop.DroneControl;
//import org.firstinspires.ftc.teamcode.opmodes.teleop.HorizControl;
//import org.firstinspires.ftc.teamcode.opmodes.teleop.LiftControl;
import org.firstinspires.ftc.teamcode.opmodes.teleop.HorizControl;
import org.firstinspires.ftc.teamcode.opmodes.teleop.IntakeControl;
import org.firstinspires.ftc.teamcode.util.LoopTimer;
import org.firstinspires.ftc.teamcode.util.Persistent;
import org.firstinspires.ftc.teamcode.util.Scheduler;
import org.firstinspires.ftc.teamcode.util.event.EventBus;


@TeleOp(name = "!!The TeleOp!!")
public class CurrentTele extends LoggingOpMode {

    private Robot robot;
    private ControllerMap controllerMap;
    private ControlMgr controlMgr;

    private EventBus evBus;
    private Scheduler scheduler;

    @Override
    public void init() {
        super.init();
        robot = Robot.initialize(hardwareMap);
        evBus = robot.eventBus;
        scheduler = robot.scheduler;
//        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        controllerMap = new ControllerMap(gamepad1, gamepad2, evBus);
        controlMgr = new ControlMgr(robot, controllerMap);

        controlMgr.addModule(new DriveControl("Drive Control"));
//        controlMgr.addModule(new LiftControl("Lift Control"));
//        controlMgr.addModule(new ClawControl("Claw Control"));
        controlMgr.addModule(new DroneControl("Drone Control"));
//        controlMgr.addModule(new IntakeControl("Intake Control"));
        controlMgr.addModule(new HorizControl("Horiz Control"));


        controlMgr.initModules();
    }

    @Override
    public void init_loop()
    {
    }

    @Override
    public void start()
    {
        Persistent.clear();
        LoopTimer.resetTimer();
    }

    @Override
    public void loop()
    {
        // Loop Updaters
        controllerMap.update();
        try {
            controlMgr.loop(telemetry);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        scheduler.loop();
        evBus.update();
        telemetry.update();
        LoopTimer.resetTimer();
    }

    @Override
    public void stop()
    {
        controlMgr.stop();
        super.stop();
    }
}