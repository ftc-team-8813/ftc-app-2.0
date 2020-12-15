package org.firstinspires.ftc.teamcode.opmodes.test;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.input.ControllerMap;

@TeleOp(name="Controller test")
public class ButtonTest extends OpMode
{
    ControllerMap map;
    
    @Override
    public void init()
    {
        map = new ControllerMap(gamepad1, gamepad2);
    }
    
    @Override
    public void loop()
    {
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
