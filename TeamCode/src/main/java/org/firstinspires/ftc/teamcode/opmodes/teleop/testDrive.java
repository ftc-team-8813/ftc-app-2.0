//package org.firstinspires.ftc.teamcode.opmodes.teleop;
//
//import android.service.controls.Control;
//
//import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
//
//import org.firstinspires.ftc.teamcode.hardware.Drivetrain;
//import org.firstinspires.ftc.teamcode.hardware.Robot;
//import org.firstinspires.ftc.teamcode.input.ControllerMap;
//import org.firstinspires.ftc.teamcode.opmodes.LoggingOpMode;
//import org.firstinspires.ftc.teamcode.util.event.EventBus;
//
//@TeleOp(name = "!!2022-2023 Robot!!")
//public class testDrive extends LoggingOpMode {
//
//    Robot robot;
//    private ControllerMap controllerMap;
//    private ControllerMap.AxisEntry strafe;
//    private ControllerMap.AxisEntry forward;
//    private ControllerMap.AxisEntry turn;
//    private ControllerMap.ButtonEntry b;
//    EventBus evBus;
//
//    @Override
//    public void init() {
//        super.init();
//        robot = new Robot(hardwareMap);
//        evBus = robot.eventBus;
//        controllerMap = new ControllerMap(gamepad1, gamepad2, evBus);
//
//
//        //change hardware map
//       strafe = controllerMap.getAxisMap("drive:left_x", "gamepad1", "left_stick_x");
//       forward = controllerMap.getAxisMap("drive:left_y", "gamepad1", "left_stick_y");
//       turn = controllerMap.getAxisMap("drive:right_x", "gamepad1", "right_stick_x");
//    }
//
//    @Override
//    public void loop() {
//        robot.drivetrain.move(-forward.get(), strafe.get(), turn.get(), 0);
//        controllerMap.update();
//    }
//}
