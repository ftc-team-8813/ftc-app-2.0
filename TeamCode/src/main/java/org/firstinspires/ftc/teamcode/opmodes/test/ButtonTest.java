package org.firstinspires.ftc.teamcode.opmodes.test;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.opmodes.LoggingOpMode;
import org.firstinspires.ftc.teamcode.util.event.EventBus;

@TeleOp(name="Controller test")
@Disabled
public class ButtonTest extends LoggingOpMode
{
    ControllerMap map;
    
    @Override
    public void init()
    {
        map = new ControllerMap(gamepad1, gamepad2, new EventBus());
    }
    
    @Override
    public void loop()
    {
        map.update();
        
        telemetry.addData("Gamepad1 buttons", "");
        for (ControllerMap.Button b : ControllerMap.Button.values())
        {
            telemetry.addData(b.name(), map.getButton(ControllerMap.Controller.gamepad1, b));
        }
        
        telemetry.addData("Gamepad2 buttons", "");
        for (ControllerMap.Button b : ControllerMap.Button.values())
        {
            telemetry.addData(b.name(), map.getButton(ControllerMap.Controller.gamepad2, b));
        }
        telemetry.update();
    }
}
