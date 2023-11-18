//package org.firstinspires.ftc.teamcode.opmodes.teleop;
//
//import com.acmerobotics.dashboard.config.Config;
//import com.arcrobotics.ftclib.controller.PIDFController;
//import com.qualcomm.robotcore.hardware.PIDCoefficients;
//import com.qualcomm.robotcore.hardware.PIDFCoefficients;
//import com.qualcomm.robotcore.util.ElapsedTime;
//
//import org.firstinspires.ftc.robotcore.external.Telemetry;
//import org.firstinspires.ftc.teamcode.hardware.Lift;
//import org.firstinspires.ftc.teamcode.hardware.Robot;
//import org.firstinspires.ftc.teamcode.input.ControllerMap;
//
//import java.util.concurrent.TimeUnit;
//
//@Config
//public class LiftControl extends ControlModule {
//
//    private Lift lift;
//    private ControllerMap.ButtonEntry y_button;
//    private ControllerMap.ButtonEntry b_button;
//    private ControllerMap.ButtonEntry a_button;
//    private ControllerMap.ButtonEntry x_button;
//    ElapsedTime timer;
//
//    public static double LIFTDOWNPOS = 0;
//    public static double LIFTLOWPOS = 460;
//    public static double LIFTMIDPOS = 820;
//    public static double LIFTHIGHPOS = 930;
//
//    public static double kp = 0.008;
//    public static double ki = 0;
//    public static double kd = 0;
//    public static double kf = 0;
//
//    PIDFController liftPID = new PIDFController(kp, ki, kd, kf);
//
//    public LiftControl(String name) {
//        super(name);
//    }
//
//    @Override
//    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager) {
//        this.lift = robot.lift;
//
//        y_button = controllerMap.getButtonMap("lift:high","gamepad1","y");
//        b_button = controllerMap.getButtonMap("lift:mid","gamepad1","b");
//        a_button = controllerMap.getButtonMap("lift:low","gamepad1","a");
//        x_button = controllerMap.getButtonMap("lift:default","gamepad1","x");
//
//        lift.setLiftTarget(0);
//        lift.resetEncoders();
//
//        timer = new ElapsedTime();
//    }
//
//    @Override
//    public void init_loop(Telemetry telemetry) {
//        super.init_loop(telemetry);
//
//        lift.setLiftsPower(-0.2);
//        lift.resetEncoders();
//    }
//
//    @Override
//    public void update(Telemetry telemetry) {
//        lift.update();
//
//        if(y_button.edge() == -1){
//            lift.setLiftTarget(LIFTHIGHPOS);
//        }
//        if(b_button.edge() == -1){
//            lift.setLiftTarget(LIFTMIDPOS);
//        }
//        if(a_button.edge() == -1){
//            lift.setLiftTarget(LIFTLOWPOS);
//        }
//        if(x_button.edge() == -1){
//            lift.setLiftTarget(LIFTDOWNPOS);
//            timer.reset();
//        }
//
//        if (timer.milliseconds() > 900 && lift.getLiftTarget() < 100) {
//            lift.resetEncoders();
//        }
//        lift.setLiftsPower(liftPID.calculate(lift.getCurrentPosition(), lift.getLiftTarget()));
//
//        telemetry.addData("Lift Power", lift.getLiftPower());
//        telemetry.addData("Lift Target", lift.getLiftTarget());
//        telemetry.addData("Lift Current", lift.getCurrentPosition());
//    }
//}
