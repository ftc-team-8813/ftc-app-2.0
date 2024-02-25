//package org.firstinspires.ftc.teamcode.opmodes.teleop;
//
//import org.firstinspires.ftc.robotcore.external.Telemetry;
//import org.firstinspires.ftc.teamcode.hardware.Drivetrain;
//import org.firstinspires.ftc.teamcode.hardware.Robot;
//import org.firstinspires.ftc.teamcode.input.ControllerMap;
//
//public class DriveTrainTest extends ControlModule{
//
//    private Drivetrain drivetrain;
//    private Robot robot;
//
//    private ControllerMap.AxisEntry ax_drive_left_x;
//    private ControllerMap.AxisEntry ax_drive_left_y;
//    private ControllerMap.AxisEntry ax_drive_right_x;
//    public DriveTrainTest(String name) {
//        super(name);
//
//    }
//
//    @Override
//    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager) {
//        this.drivetrain = robot.drivetrain;
//        this.robot = robot;
//
//        ax_drive_left_x = controllerMap.getAxisMap("drive:left_x", "gamepad1", "left_stick_x");
//        ax_drive_left_y = controllerMap.getAxisMap("drive:right_y", "gamepad1", "left_stick_y");
//        ax_drive_right_x = controllerMap.getAxisMap("drive:right_x", "gamepad1", "right_stick_x");
//    }
//
//    @Override
//    public void update(Telemetry telemetry) {
//        robot.drivetrain.
//    }
//}
