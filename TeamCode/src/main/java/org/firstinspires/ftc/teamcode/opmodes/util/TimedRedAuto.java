package org.firstinspires.ftc.teamcode.opmodes.util;

import android.text.ParcelableSpan;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.hardware.Arm;
import org.firstinspires.ftc.teamcode.hardware.Drivetrain;
import org.firstinspires.ftc.teamcode.hardware.Horizontal;
import org.firstinspires.ftc.teamcode.hardware.Intake;
import org.firstinspires.ftc.teamcode.hardware.Lift;
import org.firstinspires.ftc.teamcode.hardware.navigation.PID;
import org.firstinspires.ftc.teamcode.opmodes.LoggingOpMode;

@Autonomous(name = "!!! Time Based Auto !!!")
public class TimedRedAuto extends LoggingOpMode {

    private Lift lift;
    private Horizontal horizontal;
    private Arm arm;
    private Intake intake;
    private Drivetrain drivetrain;

    private int main_id = 0;

    ElapsedTime timer = new ElapsedTime();
    ElapsedTime auto_timer = new ElapsedTime();

    private final PID arm_PID = new PID(0.009, 0, 0, 0.1, 0, 0);
    private final PID horizontal_PID = new PID(0.008, 0, 0, 0, 0, 0);
    private final PID lift_PID = new PID(0.02, 0, 0, 0.015, 0, 0);

    @Override
    public void init_loop() {

        super.init_loop();

        if(!arm.getLimit()){
            arm.setPower(0.5);
        }
        if(!lift.getLimit()){
            lift.setPower(-0.2);
        }
        if(!horizontal.getLimit()){
            horizontal.setPower(0.3);
        }

        if(arm.getLimit()){
            arm.resetEncoders();
        }
        if(lift.getLimit()){
            lift.resetEncoders();
        }
        if(horizontal.getLimit()){
            horizontal.resetEncoders();
        }

        lift.setHolderPosition(0.12);

        arm.resetEncoders();
        lift.resetEncoders();
        horizontal.resetEncoders();
    }

    @Override
    public void start() {
        super.start();
        lift.setHolderPosition(0.3);
        auto_timer.reset();
        timer.reset();
    }

    @Override
    public void loop() {
        if(auto_timer.seconds() < 26)
        {
            switch(main_id){
                case 0:
                    if(timer.seconds() < 1.5){
                        drivetrain.move(1, 0, 0, 0);
                        timer.reset();
                        main_id += 1;
                    }
                    break;
//                case 1:
//                    if(timer.seconds() < 0.25){
//                        drivetrain.move(0 ,0, 1, 0);
//                        main_id += 1;
//                    }
//                case 2:

            }
        }else{
            //nothing right now
        }
    }
}
