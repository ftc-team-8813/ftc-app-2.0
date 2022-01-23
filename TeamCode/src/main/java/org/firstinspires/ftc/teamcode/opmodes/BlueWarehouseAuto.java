package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.AutoDrive;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.opmodes.auto.AutonomousTemplate;
import org.firstinspires.ftc.teamcode.util.Status;
import org.firstinspires.ftc.teamcode.util.event.EventBus;

@Autonomous(name="Blue Warehouse Auto", group="Blues")
public class BlueWarehouseAuto extends LoggingOpMode
{
    private Robot robot;
    private AutonomousTemplate auto;
    private final String name = "Blue Warehouse Auto";
    private int id1 = 0;
    private int id2 = 0;
    private ElapsedTime timer1;
    private boolean waiting1 = false;
    private ElapsedTime timer2;
    private boolean waiting2 = false;


    @Override
    public void init() {
        super.init();
        this.robot = Robot.initialize(hardwareMap, name, 1);
        this.auto = new AutonomousTemplate(
                name,
                this.robot,
                hardwareMap,
                new ControllerMap(gamepad1, gamepad2, new EventBus()),
                telemetry
        );
        timer1 = new ElapsedTime();
        timer2 = new ElapsedTime();
        auto.init_camera();
        auto.init_lift();
    }

    @Override
    public void start() {
        timer1.reset();
        timer2.reset();
    }

    @Override
    public void loop() {

        //--------------------------------------------------------------------------------------------------------
        //CHASSIS AND INTAKE -------------------------------------------------------------------------------------
        //--------------------------------------------------------------------------------------------------------
        switch (id1){
            case 0: //vision
                auto.check_image(false);
                if (!waiting1) {timer1.reset(); waiting1 = true;}
                if (timer1.seconds() > 0.3) {id1+=1; id2+=1; waiting1 = false;}
                break;
            case 1: //go to goal
                robot.navigation.moveToPosition(1, -25.0, 0.0, 0.6, true);
                break;
            case 2: //go to warehouse, make sure slides are in before bucket goes down
                robot.navigation.moveToPosition(1,27.0,0.0, 0.6, true);
                if (auto.chassis_reached && id2 == 9) {id1+=1; auto.chassis_reached = false;}
                break;
//            case 3: //intake, once done tell slides to start extending
//                robot.intake.setIntakeFront(0.9);
//                robot.navigation.moveToPosition(-1,40,0,.35,true);
//                if (!waiting1) {timer1.reset(); waiting1 = true;}
//                if (timer1.seconds() > 3) {id1+=1; id2+=1; waiting1 = false;};
//                if(auto.freight_sensed) {id1+=1; id2+=1; waiting1 = false;}
//                break;
//            case 4: //outtake and back to warehouse entrance
//                robot.intake.setIntakeBack(-0.8);
//                robot.intake.setIntakeFront(-0.8);
//                robot.navigation.moveToPosition(1.5,33.0,0.0, 0.6, true);
//                if (auto.chassis_reached && id2 == 12) {id1+=1; id2+=1; auto.chassis_reached = false;}
//                break;
//            case 5: //back to goal, wait for scoring before next case
//                robot.navigation.moveToPosition(1.5,-21,0.0, 0.6, true);
//                robot.intake.setIntakeFront(-0.5);
//                if (auto.chassis_reached && id2 == 14) {id2+=1; auto.chassis_reached = false;}
//                break;
//            case 6: //go to warehouse, make sure slides are in before bucket goes down
//                robot.navigation.moveToPosition(1.5,30.0,0.0, 0.6, true);
//                if (auto.chassis_reached && id2 == 20) {id1+=1; auto.chassis_reached = false;}
//                break;
//            case 7: //intake, once done tell slides to start extending
//                robot.intake.setIntakeFront(0.9);
//                robot.navigation.moveToPosition(-3,44,0,.35,true);
//                if (!waiting1) {timer1.reset(); waiting1 = true;}
//                if (timer1.seconds() > 3) {id1+=1; id2+=1; waiting1 = false;};
//                if(auto.freight_sensed) {id1+=1; id2+=1; waiting1 = false;}
//                break;
//            case 8: //outtake and back to warehouse entrance
//                robot.intake.setIntakeFront(-0.8);
//                robot.navigation.moveToPosition(2,33.0,0.0, 0.6, true);
//                if (auto.chassis_reached && id2 == 23) {id1+=1; id2+=1; auto.chassis_reached = false;}
//                break;
//            case 9: //back to goal, wait for scoring before next case
//                robot.navigation.moveToPosition(2,-20,0.0, 0.6, true);
//                robot.intake.setIntakeFront(-0.5);
//                if (auto.chassis_reached && id2 == 25) {id2+=1; auto.chassis_reached = false;}
//                break;
//            case 10: //go to warehouse, make sure slides are in before bucket goes down
//                robot.navigation.moveToPosition(2,31.0,0.0, 0.6, true);
//                if (auto.chassis_reached && id2 == 31) {id1+=1; auto.chassis_reached = false;}
//                break;
//            case 11: //intake, once done tell slides to start extending
//                robot.intake.setIntakeFront(0.9);
//                robot.navigation.moveToPosition(-6,47,0,.35,true);
//                if (!waiting1) {timer1.reset(); waiting1 = true;}
//                if (timer1.seconds() > 3) {id1+=1; id2+=1; waiting1 = false;};
//                if(auto.freight_sensed) {id1+=1; id2+=1; waiting1 = false;}
//                break;
//            case 12: //outtake and back to warehouse entrance
//                robot.intake.setIntakeFront(-0.8);
//                robot.navigation.moveToPosition(2.5,33.0,0.0, 0.6, true);
//                if (auto.chassis_reached && id2 == 34) {id1+=1; id2+=1; auto.chassis_reached = false;}
//                break;
//            case 13: //back to goal, wait for scoring before next case
//                robot.navigation.moveToPosition(2.5,-20,0.0, 0.6, true);
//                robot.intake.setIntakeFront(-0.5);
//                if (auto.chassis_reached && id2 == 36) {id2+=1; auto.chassis_reached = false;}
//                break;
//            case 14: //park
//                //robot.navigation.wall_shove = true;
//                robot.intake.setIntakeFront(0);
//                robot.intake.setIntakeBack(0);
//                robot.navigation.moveToPosition(2,45,0.0, 0.8, true);
//                break;
        }
        //--------------------------------------------------------------------------------------------------------
        //LIFT ---------------------------------------------------------------------------------------------------
        //--------------------------------------------------------------------------------------------------------
        switch (id2){
            case 0:
                break;
            case 1: //pit stop while camera is running
                robot.lift.extend(Status.STAGES.get("pitstop"), true);
                if (auto.lift_reached && id1 == 1) id2+=1;
                break;
            case 2: //flip arm out
                switch(auto.shipping_height){
                    case 1:
                        robot.lift.rotate(Status.ROTATIONS.get("low_out"));
                        break;
                    case 2:
                        robot.lift.rotate(Status.ROTATIONS.get("mid_out"));
                        break;
                    case 3:
                        robot.lift.rotate(Status.ROTATIONS.get("high_out"));
                        break;
                    case 0:
                        robot.lift.rotate(Status.ROTATIONS.get("high_out"));
                        break;
                }
                if (!waiting2) {timer2.reset(); waiting2 = true;}
                if (timer2.seconds() > Status.PITSTOP_WAIT_TIME) {id2+=1; waiting2 = false;}
                break;
            case 3: //extend slides
                switch(auto.shipping_height){
                    case 1:
                        robot.lift.extend(Status.STAGES.get("low"), true);
                        break;
                    case 2:
                        robot.lift.extend(Status.STAGES.get("mid"), true);
                        break;
                    case 3:
                        robot.lift.extend(Status.STAGES.get("high"), true);
                        break;
                    case 0:
                        robot.lift.extend(Status.STAGES.get("high"), true);
                        break;
                }
                if (auto.lift_reached && auto.chassis_reached) id2+=1;
                break;
            case 4: //dump
                robot.intake.deposit(Status.DEPOSITS.get("dump"));
                if (!waiting2) {timer2.reset(); waiting2 = true;}
                if (timer2.seconds() > 0.15) {id2+=1; id1+=1; waiting2 = false;}
                break;
            case 5: //flip up again, tell chassis to drive back to warehouse
                robot.intake.deposit(Status.DEPOSITS.get("carry"));
                if (!waiting2) {timer2.reset(); waiting2 = true;}
                if (timer2.seconds() > 0.1) {id2+=1; waiting2 = false;}
                break;
            case 6: //retract
                robot.lift.extend(Status.STAGES.get("pitstop"), true);
                if (auto.lift_reached) id2+=1;
                break;
            case 7: //flip arm back in
                robot.lift.rotate(Status.ROTATIONS.get("in"));
                if (!waiting2) {timer2.reset(); waiting2 = true;}
                if (timer2.seconds() > Status.PITSTOP_WAIT_TIME + 0.05) {id2+=1; waiting2 = false;}
                break;
            case 8: //retract slides fully
                auto.autoLiftRetract();
                if (auto.lift_reached) id2+=1;
                break;
            case 9: //flip the bucket down, wait for intake
                robot.intake.deposit(Status.DEPOSITS.get("front"));
                break;
//            case 10: //flip the bucket up
//                robot.intake.deposit(Status.DEPOSITS.get("carry"));
//                if (!waiting2) {timer2.reset(); waiting2 = true;}
//                if (timer2.seconds() > 0.1) {id2+=1; waiting2 = false;};
//                break;
//            case 11: //pit stop
//                robot.lift.extend(Status.STAGES.get("pitstop"), true);
//                if (auto.lift_reached) id2+=1;
//                break;
//            case 12: //flip arm out
//                robot.lift.rotate(Status.ROTATIONS.get("high_out"));
////                if (!waiting2) {timer2.reset(); waiting2 = true;}
////                if (timer2.seconds() > Status.PITSTOP_WAIT_TIME) {id2+=1; waiting2 = false;};
//                break;
//            case 13: //extend slides
//                robot.lift.extend(Status.STAGES.get("high") + 500, true);
//                if (auto.lift_reached) id2+=1;
//                break;
//            case 14: //wait for chassis
//                break;
//            case 15: //dump
//                robot.intake.deposit(Status.DEPOSITS.get("dump"));
//                if (!waiting2) {timer2.reset(); waiting2 = true;}
//                if (timer2.seconds() > 0.2) {id2+=1; id1+=1; waiting2 = false;}
//                break;
//            case 16: //flip up again, tell chassis to drive back to warehouse
//                robot.intake.deposit(Status.DEPOSITS.get("carry"));
//                if (!waiting2) {timer2.reset(); waiting2 = true;}
//                if (timer2.seconds() > 0.1) {id2+=1; waiting2 = false;};
//                break;
//            case 17: //retract
//                robot.lift.extend(Status.STAGES.get("pitstop"), true);
//                if (auto.lift_reached) id2+=1;
//                break;
//            case 18: //flip arm back in
//                robot.lift.rotate(Status.ROTATIONS.get("in"));
//                if (!waiting2) {timer2.reset(); waiting2 = true;}
//                if (timer2.seconds() > Status.PITSTOP_WAIT_TIME + 0.05) {id2+=1; waiting2 = false;};
//                break;
//            case 19: //retract fully
//                auto.autoLiftRetract();
//                if (auto.lift_reached) id2+=1;
//                break;
//            case 20: //flip the bucket down, wait for intake
//                robot.intake.deposit(Status.DEPOSITS.get("front"));
//                break;
//            case 21: //flip the bucket up
//                robot.intake.deposit(Status.DEPOSITS.get("carry"));
//                if (!waiting2) {timer2.reset(); waiting2 = true;}
//                if (timer2.seconds() > 0.1) {id2+=1; waiting2 = false;};
//                break;
//            case 22: //pit stop
//                robot.lift.extend(Status.STAGES.get("pitstop"), true);
//                if (auto.lift_reached) id2+=1;
//                break;
//            case 23: //flip arm out
//                robot.lift.rotate(Status.ROTATIONS.get("high_out"));
////                if (!waiting2) {timer2.reset(); waiting2 = true;}
////                if (timer2.seconds() > Status.PITSTOP_WAIT_TIME) {id2+=1; waiting2 = false;};
//                break;
//            case 24: //extend slides
//                robot.lift.extend(Status.STAGES.get("high") + 500, true);
//                if (auto.lift_reached) id2+=1;
//                break;
//            case 25: //wait for chassis
//                break;
//            case 26: //dump
//                robot.intake.deposit(Status.DEPOSITS.get("dump"));
//                if (!waiting2) {timer2.reset(); waiting2 = true;}
//                if (timer2.seconds() > 0.2) {id2+=1; waiting2 = false;};
//                break;
//            case 27: //flip up again, tell chassis to drive back to warehouse
//                robot.intake.deposit(Status.DEPOSITS.get("carry"));
//                if (!waiting2) {timer2.reset(); waiting2 = true;}
//                if (timer2.seconds() > 0.1) {id2+=1; id1+=1; waiting2 = false;};
//                break;
//            case 28: //retract
//                robot.lift.extend(Status.STAGES.get("pitstop"), true);
//                if (auto.lift_reached) id2+=1;
//                break;
//            case 29: //flip arm back in
//                robot.lift.rotate(Status.ROTATIONS.get("in"));
//                if (!waiting2) {timer2.reset(); waiting2 = true;}
//                if (timer2.seconds() > Status.PITSTOP_WAIT_TIME + 0.05) {id2+=1; waiting2 = false;};
//                break;
//            case 30: //retract fully
//                auto.autoLiftRetract();
//                if (auto.lift_reached) id2+=1;
//                break;
//            case 31: //flip the bucket down, wait for intake
//                robot.intake.deposit(Status.DEPOSITS.get("front"));
//                break;
//            case 32: //flip the bucket up
//                robot.intake.deposit(Status.DEPOSITS.get("carry"));
//                if (!waiting2) {timer2.reset(); waiting2 = true;}
//                if (timer2.seconds() > 0.1) {id2+=1; waiting2 = false;};
//                break;
//            case 33: //pit stop
//                robot.lift.extend(Status.STAGES.get("pitstop"), true);
//                if (auto.lift_reached) id2+=1;
//                break;
//            case 34: //flip arm out
//                robot.lift.rotate(Status.ROTATIONS.get("high_out"));
////                if (!waiting2) {timer2.reset(); waiting2 = true;}
////                if (timer2.seconds() > Status.PITSTOP_WAIT_TIME) {id2+=1; waiting2 = false;};
//                break;
//            case 35: //extend slides
//                robot.lift.extend(Status.STAGES.get("high") + 500, true);
//                if (auto.lift_reached) id2+=1;
//                break;
//            case 36: //wait for chassis
//                break;
//            case 37: //dump
//                robot.intake.deposit(Status.DEPOSITS.get("dump"));
//                if (!waiting2) {timer2.reset(); waiting2 = true;}
//                if (timer2.seconds() > 0.2) {id2+=1; waiting2 = false;};
//                break;
//            case 38: //flip up again, tell chassis to drive back to warehouse
//                robot.intake.deposit(Status.DEPOSITS.get("carry"));
//                if (!waiting2) {timer2.reset(); waiting2 = true;}
//                if (timer2.seconds() > 0.1) {id2+=1; id1+=1; waiting2 = false;};
//                break;
//            case 39: //retract
//                robot.lift.extend(Status.STAGES.get("pitstop"), true);
//                if (auto.lift_reached) id2+=1;
//                break;
//            case 40: //flip arm back in
//                robot.lift.rotate(Status.ROTATIONS.get("in"));
//                if (!waiting2) {timer2.reset(); waiting2 = true;}
//                if (timer2.seconds() > Status.PITSTOP_WAIT_TIME + 0.05) {id2+=1; waiting2 = false;};
//                break;
//            case 41:
//                auto.autoLiftRetract();
//                break;
        }
        auto.update();
        robot.eventBus.update();
        robot.scheduler.loop();
    }

    @Override
    public void stop() {
        auto.stop();
    }
}
