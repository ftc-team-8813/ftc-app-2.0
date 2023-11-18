//package org.firstinspires.ftc.teamcode.opmodes.teleop;
//
//import org.firstinspires.ftc.robotcore.external.Telemetry;
//import org.firstinspires.ftc.teamcode.hardware.Robot;
//import org.firstinspires.ftc.teamcode.hardware.Claw;
//import org.firstinspires.ftc.teamcode.input.ControllerMap;
//
//public class ClawControl extends ControlModule{
//
//    private Claw claw;
//    private Robot robot;
//
//    private ControllerMap.ButtonEntry closeButton;
//    private ControllerMap.ButtonEntry openButton;
//
//    private boolean clawClose;
//
//    private final double openPos = 0.38;
//    private final double closePos = 0;
//
//    public ClawControl(String name) {
//        super(name);
//    }
//
//    @Override
//    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager) {
//        this.robot = robot;
//        this.claw = robot.claw;
//
//        closeButton = controllerMap.getButtonMap("claw:closeClaw","gamepad1","right_bumper");
//        openButton = controllerMap.getButtonMap("claw:openClaw","gamepad1","left_bumper");
//
//        claw.setClawPos(closePos);
//        clawClose = true;
//    }
//
//    @Override
//    public void update(Telemetry telemetry) {
//
//        if(clawClose){
//            if(openButton.edge() == -1){
//                claw.setClawPos(openPos);
//                clawClose = false;
//            }
//        }
//
//        if(!clawClose){
//            if(closeButton.edge() == -1){
//                claw.setClawPos(closePos);
//                clawClose = true;
//            }
//        }
//
//        if(!clawClose){
//            if(claw.getSensorDistance() <= 60 && robot.lift.getLiftTarget() < 100){
//                claw.setClawPos(closePos);
//                clawClose = true;
//            }
//        }
//
//        telemetry.addData("Sensor Distance", claw.getSensorDistance());
//        telemetry.addData("Claw Position", claw.getClawPos());
//        telemetry.addData("clawClose Status", clawClose);
//    }
//}
