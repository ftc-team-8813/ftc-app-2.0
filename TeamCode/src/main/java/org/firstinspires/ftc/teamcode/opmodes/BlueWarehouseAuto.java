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
        this.robot = Robot.initialize(hardwareMap, name);
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
                if (timer1.seconds() > 0.5) {id1+=1; id2+=1; waiting1 = false;};
                break;
            case 1: //go to goal
                robot.navigation.moveToPosition(0.0, -25.0, 0.0, 1.0, true);
                break;
            case 2: //go to warehouse, make sure slides are in before bucket goes down
                robot.navigation.moveToPosition(2,26.0,0.0, 1, true);
                if (auto.chassis_reached && id2 == 9) id1+=1;
                break;
            case 3: //intake, once done tell slides to start extending
                robot.intake.setIntakeFront(1);
                robot.intake.detectFreight();
                robot.drivetrain.move(0.3, 0,0);
                if (!waiting1) {timer1.reset(); waiting1 = true;}
                if (timer1.seconds() > 1) {id1+=1; waiting1 = false;};
                if(auto.freight_sensed) {id1+=1; id2+=1;}
                break;
            case 4: //outtake and back to warehouse entrance
                robot.intake.setIntakeFront(-0.8);
                robot.navigation.moveToPosition(2,20.0,0.0, 0.8, true);
                if (auto.chassis_reached) id1+=1;
                break;
            case 5: //back to goal, wait for scoring before next case
                robot.navigation.moveToPosition(2,-24,0.0, 1, true);
                if (auto.chassis_reached && id2 == 14) id2+=1;
                break;
            case 6:
                robot.navigation.moveToPosition(2,24,0.0, 1, true);
                break;
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
                if (timer2.seconds() > Status.PITSTOP_WAIT_TIME) {id2+=1; waiting2 = false;};
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
                if (timer2.seconds() > 0.4) {id2+=1; waiting2 = false;};
                break;
            case 5: //flip up again, tell chassis to drive back to warehouse
                robot.intake.deposit(Status.DEPOSITS.get("carry"));
                if (!waiting2) {timer2.reset(); waiting2 = true;}
                if (timer2.seconds() > 0.2) {id2+=1; id1+=1; waiting2 = false;};
                break;
            case 6: //retract
                robot.lift.extend(Status.STAGES.get("pitstop"), true);
                if (auto.lift_reached) id2+=1;
                break;
            case 7: //flip arm back in
                robot.lift.rotate(Status.ROTATIONS.get("in"));
                if (!waiting2) {timer2.reset(); waiting2 = true;}
                if (timer2.seconds() > Status.PITSTOP_WAIT_TIME + 0.05) {id2+=1; waiting2 = false;};
                break;
            case 8: //retract slides fully
                robot.lift.extend(0, true);
                if (auto.lift_reached) id2+=1;
                break;
            case 9: //flip the bucket down, wait for intake
                robot.intake.deposit(Status.DEPOSITS.get("front"));
                break;
            case 10: //flip the bucket up
                robot.intake.deposit(Status.DEPOSITS.get("carry"));
                if (!waiting2) {timer2.reset(); waiting2 = true;}
                if (timer2.seconds() > 0.3) {id2+=1; waiting2 = false;};
                break;
            case 11: //pit stop
                robot.lift.extend(Status.STAGES.get("pitstop"), true);
                if (auto.lift_reached) id2+=1;
                break;
            case 12: //flip arm out
                robot.lift.rotate(Status.ROTATIONS.get("high_out"));
                if (!waiting2) {timer2.reset(); waiting2 = true;}
                if (timer2.seconds() > Status.PITSTOP_WAIT_TIME) {id2+=1; waiting2 = false;};
                break;
            case 13: //extend slides
                robot.lift.extend(Status.STAGES.get("high"), true);
                break;
            case 14: //wait for chassis
                break;
            case 15: //dump
                robot.intake.deposit(Status.DEPOSITS.get("dump"));
                if (!waiting2) {timer2.reset(); waiting2 = true;}
                if (timer2.seconds() > 0.3) {id2+=1; waiting2 = false;};
                break;
            case 16: //flip up again, tell chassis to drive back to warehouse
                robot.intake.deposit(Status.DEPOSITS.get("carry"));
                if (!waiting2) {timer2.reset(); waiting2 = true;}
                if (timer2.seconds() > 0.2) {id2+=1; id1+=1; waiting2 = false;};
                break;
            case 17: //retract
                robot.lift.extend(Status.STAGES.get("pitstop"), true);
                if (auto.lift_reached) id2+=1;
                break;
            case 18: //flip arm back in
                robot.lift.rotate(Status.ROTATIONS.get("in"));
                if (!waiting2) {timer2.reset(); waiting2 = true;}
                if (timer2.seconds() > Status.PITSTOP_WAIT_TIME + 0.05) {id2+=1; waiting2 = false;};
                break;
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
