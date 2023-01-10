//package org.firstinspires.ftc.teamcode.opmodes.teleop;
//
//import com.qualcomm.robotcore.util.ElapsedTime;
//
//import org.firstinspires.ftc.robotcore.external.Telemetry;
//import org.firstinspires.ftc.teamcode.hardware.Intake;
//import org.firstinspires.ftc.teamcode.hardware.Lift;
//import org.firstinspires.ftc.teamcode.hardware.Robot;
//import org.firstinspires.ftc.teamcode.input.ControllerMap;
//
//public class IntakeControl extends ControlModule {
//
//    private Intake intake;
//    private Lift lift;
//    private boolean claw_open;
//    private boolean cone_detected = false;
////    private boolean first_close = false;
//    private ElapsedTime timer = new ElapsedTime();
//    private boolean wait_till_close = true;
//
//    private final double AL_DEGREES_PER_TICK = (360.0/(28.0*108.8*32.0/15.0));
//    private final double AU_DEGREES_PER_TICK = (360.0/8192.0);
//    private final double WRIST_DEGREES_PER_TICK = (360.0/128.0);
//
//    private ControllerMap.ButtonEntry right_bumper;
//
//    private double claw_open_pos = 0.11;
//
//    public IntakeControl(String name) {
//        super(name);
//    }
//
//    @Override
//    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager) {
//        this.intake = robot.intake;
//        this.lift = robot.lift;
//        right_bumper = controllerMap.getButtonMap("intake:claw","gamepad1","right_bumper");
//
//    }
//
//    @Override
//    public void update(Telemetry telemetry) {
//
////        if((right_bumper.edge() == -1) && (!first_close)) {
////            claw_open = false;
////            first_close = true;
////        }
//
//        double[] cur_angles = lift.getEncoderValue();
//        cur_angles[2] *= -WRIST_DEGREES_PER_TICK;
//
//        if (((cur_angles[2]%360) > 175) || ((cur_angles[2]%360) < -175)) {
//            claw_open_pos = 0.3;
//        }
//        else {
//            claw_open_pos = 0.11;
//        }
////
////
//        if (right_bumper.edge() == -1) {
//            claw_open = !claw_open;
//        }
////
//        if (claw_open) {
//            intake.setClaw(claw_open_pos);
//            if (wait_till_close) {
//                timer.reset();
//                wait_till_close = false;
//            }
//            if (timer.seconds() > 1) {
//                cone_detected = false;
//            }
//        }
//        if (!claw_open) {
//            intake.setClaw(0.63);
//
//        }
//
//        if (intake.getDistance() < 20.0 && !cone_detected) {
//            claw_open = false;
//            cone_detected = true;
//            wait_till_close = true;
//        }
//
//
//
//        telemetry.addData("claw sensor dist", intake.getDistance());
//    }
//}
