//package org.firstinspires.ftc.teamcode.opmodes.teleop;
//
//import com.acmerobotics.dashboard.config.Config;
//import com.arcrobotics.ftclib.controller.PIDFController;
//import com.qualcomm.robotcore.util.ElapsedTime;
//
//import org.firstinspires.ftc.robotcore.external.Telemetry;
//import org.firstinspires.ftc.teamcode.hardware.Deposit;
//import org.firstinspires.ftc.teamcode.hardware.Lift;
//import org.firstinspires.ftc.teamcode.hardware.Robot;
//import org.firstinspires.ftc.teamcode.input.ControllerMap;
//
//
//@Config
//public class LiftControl extends ControlModule {
//
//    private Lift lift;
//    private Deposit deposit;
//    private ControllerMap.ButtonEntry y_button;
//    private ControllerMap.ButtonEntry b_button;
//    private ControllerMap.ButtonEntry a_button;
//    private ControllerMap.ButtonEntry x_button;
//
//    ElapsedTime timer;
//
//    public static double LIFTDOWNPOS = 0;
//    public static double LIFTMIDPOS = 190;
//    public static double LIFTHIGHPOS = 310;
//
//    public static double kp = 0.02;
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
//        y_button = controllerMap.getButtonMap("lift:high","gamepad2","y");
//        b_button = controllerMap.getButtonMap("lift:mid","gamepad2","b");
//        a_button = controllerMap.getButtonMap("lift:low","gamepad2","a");
//        x_button = controllerMap.getButtonMap("lift:default","gamepad2","x");
////        lb_button = controllerMap.getButtonMap("deposit:lock","gamepad1","left_bumper");
//
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
////        lift.update();
//
////        lift.setLiftsPower(liftPID.calculate(lift.getCurrentPosition(), LIFTMIDPOS));
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
////        if(b_button.edge() == -1){
////            lift.setLiftTarget(LIFTMIDPOS);
////        }
//        if(a_button.edge() == -1){
//            lift.setLiftTarget(LIFTDOWNPOS);
//        }
////
////        if (timer.milliseconds() > 900 && lift.getLiftTarget() < 50) {
////            lift.resetEncoders();
////        }
//        lift.setLiftsPower(liftPID.calculate(lift.getCurrentPosition(), lift.getLiftTarget()));
//
//        telemetry.addData("Lift Power", lift.getLiftPower());
//        telemetry.addData("Lift Target", lift.getLiftTarget());
//        telemetry.addData("Lift Current", lift.getCurrentPosition());
//    }
//}
