package org.firstinspires.ftc.teamcode.opmodes.util;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.hardware.CalibratedAnalogInput;
import org.firstinspires.ftc.teamcode.hardware.IMU;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.hardware.autoshoot.ShooterDataLogger;
import org.firstinspires.ftc.teamcode.hardware.navigation.Odometry;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.opmodes.LoggingOpMode;
import org.firstinspires.ftc.teamcode.util.Scheduler;
import org.firstinspires.ftc.teamcode.util.event.EventBus;

import java.util.ResourceBundle;

@TeleOp(name="ShooterDataCollector")
public class ShooterDataCollector extends LoggingOpMode {
    private Robot robot;
    private Odometry odometry;
    private IMU imu;
    private EventBus ev;
    private Scheduler scheduler;
    private ShooterDataLogger logger;
    private ControllerMap controllerMap;

    private ControllerMap.AxisEntry ax_drive_l;
    private ControllerMap.AxisEntry ax_drive_r;
    private ControllerMap.AxisEntry ax_turret;
    private ControllerMap.AxisEntry ax_shooter;
    private ControllerMap.ButtonEntry btn_reset_shooter;
    private ControllerMap.ButtonEntry btn_turret_home;
    private ControllerMap.ButtonEntry btn_turret_reverse;
    private ControllerMap.ButtonEntry btn_pusher;
    private ControllerMap.ButtonEntry btn_log;
    private ControllerMap.ButtonEntry btn_remove_log;

    double shooter_power;

    @Override
    public void init() {
        robot = new Robot(hardwareMap);
        odometry = new Odometry(robot.drivetrain.top_left, robot.drivetrain.top_right, imu);
        odometry.setPosition(0, 48);
        ev = new EventBus();
        scheduler = new Scheduler(ev);
        logger = new ShooterDataLogger();
        controllerMap = new ControllerMap(gamepad1, gamepad2);


        controllerMap.setAxisMap("drive_l",   "gamepad1", "left_stick_y" );
        controllerMap.setAxisMap("drive_r",   "gamepad1", "right_stick_y");
        controllerMap.setAxisMap("turret",    "gamepad2", "right_stick_x" );
        controllerMap.setAxisMap("shooter",   "gamepad2", "left_stick_y");
        controllerMap.setButtonMap("reset_shooter", "gamepad2", "x");
        controllerMap.setButtonMap("turret_home", "gamepad2", "dpad_up");
        controllerMap.setButtonMap("turret_reverse", "gamepad2", "dpad_down");
        controllerMap.setButtonMap("pusher", "gamepad2", "y");
        controllerMap.setButtonMap("log", "gamepad2", "a");
        controllerMap.setButtonMap("remove_log", "gamepad2", "b");

        ax_drive_l = controllerMap.axes.get("drive_l");
        ax_drive_r = controllerMap.axes.get("drive_r");
        ax_turret = controllerMap.axes.get("turret");
        ax_shooter = controllerMap.axes.get("shooter");
        btn_reset_shooter = controllerMap.buttons.get("reset_shooter");
        btn_turret_home = controllerMap.buttons.get("turret_home");
        btn_turret_reverse = controllerMap.buttons.get("turret_reverse");
        btn_pusher = controllerMap.buttons.get("pusher");
        btn_log = controllerMap.buttons.get("log");
        btn_remove_log = controllerMap.buttons.get("remove_log");


        robot.turret.connectEventBus(ev);
        robot.drivetrain.connectEventBus(ev);
        robot.imu.initialize(ev, scheduler);
        robot.turret.startZeroFind();
    }

    @Override
    public void init_loop(){
        robot.turret.updateInit(telemetry);
    }

    @Override
    public void loop() {
        robot.drivetrain.telemove(ax_drive_r.get(), ax_drive_l.get());

        shooter_power = Range.clip(shooter_power + -ax_shooter.get() * 0.005, 0, 1);
        robot.turret.shooter.start(shooter_power);


        double turret_adj = -ax_turret.get() * 0.0001;
        robot.turret.rotate(robot.turret.getTarget() + turret_adj);

        if (btn_turret_home.edge() > 0){
            robot.turret.home();
        }

        if (btn_turret_reverse.edge() > 0) {
            robot.turret.rotate(robot.turret.getTurretShootPos());
        }

        if (btn_pusher.get()) robot.turret.push();
        else robot.turret.unpush();

        if (btn_reset_shooter.edge() > 0){
            shooter_power = 0;
        }

        if (btn_log.edge() > 0){
            logger.addDataPoint(getHypo(), shooter_power);
        }

        if (btn_remove_log.edge() > 0){
            logger.removeLastPoint();
        }

        robot.turret.update(telemetry);
        telemetry.update();
    }

    public void stop(){
        logger.dump();
        super.stop();
    }

    public double getHypo(){
        double x_side = 72 - odometry.getX();
        double y_side = 36 - odometry.getY();
        return Math.sqrt(Math.pow(x_side, 2) + Math.pow(y_side, 2));
    }
}
