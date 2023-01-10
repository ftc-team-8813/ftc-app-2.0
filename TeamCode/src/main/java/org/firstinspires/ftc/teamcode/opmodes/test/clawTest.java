package org.firstinspires.ftc.teamcode.opmodes.test;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.hardware.Intake;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.util.event.EventBus;


@Config

@TeleOp(name = "Claw Test")
public class clawTest extends OpMode {
    private Robot robot;
    private Intake intake;
    private boolean open;
    private ControllerMap controllerMap;
    private EventBus evBus;

    private ControllerMap.ButtonEntry toggle;

    private final double CLAWOPENPOS = 0.3;
    private final double CLAWCLOSEPOS = 0.1;

    @Override
    public void init() {
        robot = new Robot(hardwareMap);

        evBus = robot.eventBus;
        intake = robot.intake;

        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        open = false;
        controllerMap = new ControllerMap(gamepad1, gamepad2, evBus);
        toggle = controllerMap.getButtonMap("toggle","gamepad1","a");
    }

    @Override
    public void loop() {
        if(toggle.edge() == -1 && !open){
            intake.setClaw(CLAWOPENPOS);
            open = true;
        }else if(toggle.edge() == -1 && open){
            intake.setClaw(CLAWCLOSEPOS);
            open = false;
        }

        telemetry.addData("Current Pos", intake.getClaw());
    }
}
