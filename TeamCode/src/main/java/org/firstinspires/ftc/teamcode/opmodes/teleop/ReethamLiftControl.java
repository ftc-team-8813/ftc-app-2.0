package org.firstinspires.ftc.teamcode.opmodes.teleop;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import org.firstinspires.ftc.teamcode.hardware.ReethamLift;

public class ReethamLiftControl extends OpMode {
        ReethamLift lift;
        DcMotor Motor1;
        DcMotor Motor2;
        DcMotor pivoter;
        boolean lift_motors_out;
        boolean pivot_motor_out;
        boolean donePivoting;

        @Override
        public void init() {
                lift = new ReethamLift(Motor1, Motor2, pivoter, 0.001);
        }

        @Override
        public void loop() {
                if(gamepad1.dpad_up) {
                        lift.lift_motors_extension(1000);
                        if(Motor1.getCurrentPosition() == 1000 && Motor2.getCurrentPosition() == 1000){
                                lift_motors_out = true;
                        }
                }else if(lift_motors_out){
                        lift.set_pivot(pivoter, gamepad1.left_stick_x, donePivoting);
                }else if(donePivoting){
                        lift.pivot_motor_extension(1000);
                        if(pivoter.getCurrentPosition() == 1000){
                                pivot_motor_out = true;
                        }
                }else if(gamepad1.dpad_down){
                        if(pivot_motor_out){
                                lift.pivot_motor_extension(0);
                                        if(pivoter.getCurrentPosition() == 0){
                                                pivot_motor_out = false;
                                        }

                                lift.lift_motors_extension(0);
                                        if(Motor1.getCurrentPosition() == 0 && Motor2.getCurrentPosition() == 0){
                                                lift_motors_out = false;
                                        }
                        }else if(lift_motors_out){
                                lift.lift_motors_extension(0);
                                if(Motor1.getCurrentPosition() == 0 && Motor2.getCurrentPosition() == 0){
                                        lift_motors_out = false;
                                }
                        }
                }
        }
}
