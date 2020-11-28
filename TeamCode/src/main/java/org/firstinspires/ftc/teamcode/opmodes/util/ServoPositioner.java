package org.firstinspires.ftc.teamcode.opmodes.util;

import android.os.UserManager;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoController;

import org.firstinspires.ftc.teamcode.telemetry.HTMLString;
import org.firstinspires.ftc.teamcode.telemetry.Scroll;

import java.util.ArrayList;
import java.util.List;

import static org.firstinspires.ftc.robotcore.external.Telemetry.DisplayFormat.HTML;

@TeleOp(group="util", name="Servo Positioner")
public class ServoPositioner extends OpMode
{
    
    private static final int SERVOS_PER_CONTROLLER = 6;
    
    private Servo[] enumerateServos(HardwareMap hardwareMap)
    {
        ServoController[] controllers = hardwareMap.getAll(ServoController.class).toArray(new ServoController[0]);
        Servo[] servos = new Servo[SERVOS_PER_CONTROLLER * controllers.length];
        for (Servo servo : servos)
        {
            int controllerIdx;
            for (controllerIdx = 0; controllerIdx < controllers.length; controllerIdx++)
            {
                if (controllers[controllerIdx] == servo.getController()) break;
            }
            if (controllerIdx == controllers.length) continue;
            servos[controllerIdx * SERVOS_PER_CONTROLLER + servo.getPortNumber()] = servo;
        }
        return servos;
    }
    
    private Scroll servoChooser;
    private Servo[] servos;
    
    @Override
    public void init()
    {
        telemetry.setDisplayFormat(HTML);
        servoChooser = new Scroll(8);
        servos = enumerateServos(hardwareMap);
        for (int i = 0; i < servos.length; i++)
        {
            if (i % SERVOS_PER_CONTROLLER == 0)
            {
                servoChooser.addLine(new HTMLString(
                        "span",
                        "style=\"color: #7f7f7f;\"",
                        servos[i].getController().getConnectionInfo()).toString());
            }
            
            if (servos[i] == null)
            {
                servoChooser.addLine(new HTMLString(
                        "span",
                        "style=\"color: #7f7f7f;\"",
                        "" + i + " -- [empty]").toString());
            }
            else
            {
                String servoName = hardwareMap.getNamesOf(servos[i]).toArray(new String[0])[0];
                servoChooser.addLine("" + i + " -- " + servoName);
            }
        }
    }
    
    
    
    @Override
    public void init_loop()
    {
    
    }
    
    @Override
    public void loop()
    {
    
    }
}
