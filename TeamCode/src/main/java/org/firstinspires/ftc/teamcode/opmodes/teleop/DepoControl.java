//package org.firstinspires.ftc.teamcode.opmodes.teleop;
//
//import com.arcrobotics.ftclib.controller.PIDFController;
//import com.qualcomm.robotcore.util.ElapsedTime;
//
//import org.firstinspires.ftc.robotcore.external.Telemetry;
//import org.firstinspires.ftc.teamcode.hardware.Deposit;
//import org.firstinspires.ftc.teamcode.hardware.Lift;
//import org.firstinspires.ftc.teamcode.hardware.Robot;
//import org.firstinspires.ftc.teamcode.input.ControllerMap;
//
//public class DepoControl extends ControlModule{
//
//    private Deposit deposit;
//    private Lift lift;
//    private Robot robot;
//    private ControllerMap.ButtonEntry b_button;
//    private ControllerMap.ButtonEntry y_button;
//    private ControllerMap.ButtonEntry a_button;
//    private ControllerMap.ButtonEntry x_button;
//    private ControllerMap.ButtonEntry lb_button;
//    private ControllerMap.ButtonEntry rb_button;
//
//    public ControllerMap.ButtonEntry goIn;
//
//    private final double PIVOTUP = 0.261;
//    private final double LIFTSERVSUP = 0.33;
//    private final double LIFTSERVPRE = 0.68;
//    private final double PIVOTPRE = 0.03;
//    private final double LIFTSERVFULL = 0.692;
//    private final double PIVOTFULL = 0.161;
//    private final double PIVOTINIT = 0.95;
//    private final double LIFTSERVINIT = 0.710;
//    private final double MICROCLOSED = 0.5;
//    private final double MICROOPENED = 0;
//    private final double INTAKELOCKOPENED = 0.4;
//    private final double INTAKELOCKClOSED = 0.8;
//    public boolean transferring = false;
//    private ElapsedTime timer1;
//    private ElapsedTime timer2;
//
//    PIDFController horizPID = new PIDFController(0.03, 0, 0, 0);
//
//    public DepoControl(String name) {
//        super(name);
//    }
//
//    @Override
//    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager) {
//        this.deposit = robot.deposit;
//        this.robot = robot;
//        this.lift = robot.lift;
//
////        this.transferring = robot.transferring;
//
//        y_button = controllerMap.getButtonMap("lift:high","gamepad1","y");
//        b_button = controllerMap.getButtonMap("lift:mid","gamepad1","b");
//        a_button = controllerMap.getButtonMap("lift:low","gamepad1","a");
//        x_button = controllerMap.getButtonMap("lift:default","gamepad1","x");
//        lb_button = controllerMap.getButtonMap("deposit:lock1","gamepad1","left_bumper");
//        rb_button = controllerMap.getButtonMap("horizIntake","gamepad1","right_bumper");
//
//        goIn = controllerMap.getButtonMap("goIn", "gamepad2", "dpad_down");
//
//        deposit.setDepoLock(MICROCLOSED);
//        deposit.setLiftDepos(LIFTSERVSUP);
//        deposit.setDepoPivot(PIVOTUP);
//
//        robot.horiz.resetEncoders();
//
//        lift.setLiftTarget(0);
//        lift.resetEncoders();
//
//        timer1 = new ElapsedTime();
//        timer2 = new ElapsedTime();
//    }
//
//    @Override
//    public void update(Telemetry telemetry) {
//
//        lift.update();
//
//        if(y_button.edge() == -1 || b_button.edge() == -1){
//            deposit.setDepoLock(MICROOPENED);
//            robot.intake.setLock(INTAKELOCKOPENED);
//            timer1.reset();
//        }
//
//        if(timer1.milliseconds() == 1000){
//            deposit.setDepoLock(MICROCLOSED);
//            deposit.setLiftDepos(LIFTSERVSUP);
//            deposit.setDepoPivot(PIVOTUP);
//        }
//
//        if(a_button.edge() == -1){
//            timer2.reset();
//            transferring = false;
//            deposit.setDepoLock(MICROOPENED);
//            robot.intake.setLock(INTAKELOCKClOSED);
//            robot.horiz.setHorizTarget(1440);
//        }
//
//        if(timer2.milliseconds() == 5000){
//            deposit.setDepoPivot(PIVOTPRE);
//            deposit.setLiftDepos(LIFTSERVPRE);
//        }
//
//        if(lb_button.edge() == -1){
//            deposit.setDepoLock(MICROOPENED);
//        }
//
//        if(x_button.edge() == -1){
//            transferring = true;
//        }
//
//        if(transferring){
//            robot.horiz.setHorizTarget(0);
//            deposit.setDepoPivot(PIVOTFULL);
//            deposit.setLiftDepos(LIFTSERVFULL);
//            robot.intake.setPower(0.5);
//        }
////
////        if(y_button.edge() == -1){
////            lift.setLiftTarget(LIFTHIGHPOS);
////        }
////        if(b_button.edge() == -1){
////            lift.setLiftTarget(LIFTMIDPOS);
////        }
////        if(a_button.edge() == -1){
////            lift.setLiftTarget(LIFTDOWNPOS);
////        }
//
//        robot.horiz.setHorizPwr(horizPID.calculate(robot.horiz.getCurrentPosition(), robot.horiz.getHorizTarget()));
//
//        telemetry.addData("Horiz Target", robot.horiz.getHorizTarget());
//        telemetry.addData("Horiz Current", robot.horiz.getCurrentPosition());
//        telemetry.addData("Horiz Power", robot.horiz.getHorizPwr());
//
//
//    }
//}