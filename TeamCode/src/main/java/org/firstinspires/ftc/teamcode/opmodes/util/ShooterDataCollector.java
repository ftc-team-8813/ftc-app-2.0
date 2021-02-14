package org.firstinspires.ftc.teamcode.opmodes.util;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.hardware.IMU;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.hardware.autoshoot.ShooterDataLogger;
import org.firstinspires.ftc.teamcode.hardware.navigation.Odometry;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.opmodes.LoggingOpMode;

@TeleOp(name="ShooterDataCollector")
public class ShooterDataCollector extends LoggingOpMode {
    private Robot robot;
    private Odometry odometry;
    private IMU imu;
    private ShooterDataLogger logger;
    private ControllerMap controllerMap;

    private ControllerMap.AxisEntry ax_drive_l;
    private ControllerMap.AxisEntry ax_drive_r;
    private ControllerMap.AxisEntry ax_turret;
    private ControllerMap.AxisEntry ax_shooter;
    private ControllerMap.ButtonEntry btn_log;
    private ControllerMap.ButtonEntry btn_remove_log;

    double shooter_power;
    double turret_power;

    @Override
    public void init() {
        robot = new Robot(hardwareMap);
        odometry = new Odometry(robot.drivetrain.top_left, robot.drivetrain.top_right, imu);
        odometry.setPosition(0, 48);
        logger = new ShooterDataLogger();
        controllerMap = new ControllerMap(gamepad1, gamepad2);


        controllerMap.setAxisMap("drive_l",   "gamepad1", "left_stick_y" );
        controllerMap.setAxisMap("drive_r",   "gamepad1", "right_stick_y");
        controllerMap.setAxisMap("turret",    "gamepad2", "left_stick_x" );
        controllerMap.setButtonMap("shooter",   "gamepad2", "left_stick_y");
        controllerMap.setButtonMap("log", "gamepad1", "a");
        controllerMap.setButtonMap("remove_log", "gamepad1", "b");

        ax_drive_l = controllerMap.axes.get("drive_l");
        ax_drive_r = controllerMap.axes.get("drive_r");
        ax_turret = controllerMap.axes.get("turret");
        ax_shooter = controllerMap.axes.get("shooter");
        btn_log = controllerMap.buttons.get("log");
        btn_remove_log = controllerMap.buttons.get("remove_log");
    }

    @Override
    public void loop() {
        robot.drivetrain.telemove(ax_drive_l.get(), ax_drive_r.get());

        shooter_power = Range.clip(shooter_power + ax_shooter.get() * 0.1, -1, 1);
        robot.turret.shooter.setPower(turret_power);

        double turret_adj = Math.pow(ax_turret.get(), 3) * 0.001;
        robot.turret.rotate(robot.turret.getTarget() + turret_adj);

        if (btn_log.edge() > 0){
            logger.addDataPoint(getHypo(), shooter_power);
        }

        if (btn_remove_log.edge() > 0){
            logger.removeLastPoint();
        }

        telemetry.addData("Turret Position: ", robot.turret.getTarget());
        telemetry.addData("Shooter Power: ", shooter_power);
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
