package org.firstinspires.ftc.teamcode.opmodes.test;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.AutoDrive;
import org.firstinspires.ftc.teamcode.hardware.LineFinder;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.opmodes.LoggingOpMode;
import org.firstinspires.ftc.teamcode.opmodes.auto.AutonomousTemplate;
import org.firstinspires.ftc.teamcode.util.Status;
import org.firstinspires.ftc.teamcode.util.event.EventBus;

@Autonomous(name="Line Sense Test")
public class ColorSensorTestAuto extends LoggingOpMode
{
    private Robot robot;
    private AutonomousTemplate auto;
    private LineFinder lineFinder;
    private final String name = "Line Sense Test";
    private int id1 = 0;
    private int id2 = 0;
    private ElapsedTime timer1;
    private boolean waiting1 = false;
    private ElapsedTime timer2;
    private final boolean waiting2 = false;


    @Override
    public void init() {
        super.init();
        this.robot = Robot.initialize(hardwareMap, name, -1);
        this.auto = new AutonomousTemplate(
                name,
                this.robot,
                hardwareMap,
                new ControllerMap(gamepad1, gamepad2, new EventBus()),
                telemetry
        );
        this.lineFinder = robot.lineFinder;
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
        switch (id1) {
            case 0: //vision
                auto.check_image(false);
                if (!waiting1) {
                    timer1.reset();
                    waiting1 = true;
                }
                if (timer1.seconds() > 0.3) {
                    id1 += 1;
                    id2 += 1;
                    waiting1 = false;
                }
                break;
            case 1: //go to goal
                robot.navigation.moveToPosition(0, -55.0, 0.0, 0.6, true);
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

