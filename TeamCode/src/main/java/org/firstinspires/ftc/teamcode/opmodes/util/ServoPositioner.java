package org.firstinspires.ftc.teamcode.opmodes.util;

import com.qualcomm.hardware.lynx.LynxServoController;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoController;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.opmodes.LoggingOpMode;
import org.firstinspires.ftc.teamcode.telemetry.HTMLString;
import org.firstinspires.ftc.teamcode.telemetry.Scroll;
import org.firstinspires.ftc.teamcode.util.Time;
import org.firstinspires.ftc.teamcode.util.event.EventBus;

import java.util.Map;

import static org.firstinspires.ftc.robotcore.external.Telemetry.DisplayFormat.HTML;

// TODO: Add save-load system (from DiffyServoPositioner)
@TeleOp(group = "util", name = "Servo Positioner")
@Disabled
public class ServoPositioner extends LoggingOpMode
{
    
    private static final int SERVOS_PER_CONTROLLER = 6;
    
    private Servo[] enumerateServos(HardwareMap hardwareMap)
    {
        // TODO: this is a hack
        servoControllers = hardwareMap.getAll(ServoController.class).toArray(new ServoController[0]);
        Servo[] servos = new Servo[SERVOS_PER_CONTROLLER * servoControllers.length];
        for (Map.Entry<String, Servo> entry : hardwareMap.servo.entrySet())
        {
            String name = entry.getKey();
            Servo servo = entry.getValue();
            int controllerIdx;
            for (controllerIdx = 0; controllerIdx < servoControllers.length; controllerIdx++)
            {
                if (servoControllers[controllerIdx] == servo.getController()) break;
            }
            System.out.printf("Servo %s -- port %d controller %d\n", name, servo.getPortNumber(), controllerIdx);
            if (controllerIdx == servoControllers.length) continue;
            servos[controllerIdx * SERVOS_PER_CONTROLLER + servo.getPortNumber()] = servo;
        }
        return servos;
    }
    
    private ControllerMap.ButtonEntry btn_up_arrow;
    private ControllerMap.ButtonEntry btn_down_arrow;
    private ControllerMap.ButtonEntry btn_reset_pos;
    private ControllerMap.ButtonEntry btn_delete_pos;
    private ControllerMap.ButtonEntry btn_ok;
    private ControllerMap.ButtonEntry btn_stop_servo;
    private ControllerMap.ButtonEntry btn_exit_to_menu;
    private ControllerMap.AxisEntry ax_change_position;
    
    private double lastTick;
    
    private static abstract class Scene
    {
        private boolean firstLoop = true;
        
        abstract void init();
        
        abstract void loop();
    }
    
    private Scene currScene;
    private ControllerMap controllerMap;
    private boolean started;
    private int servoId;
    private ServoController[] servoControllers;
    
    private EventBus evBus;
    
    @Override
    public void init()
    {
        super.init();
        telemetry.setDisplayFormat(HTML);
        currScene = new SceneChoose();
        evBus = new EventBus();
        controllerMap = new ControllerMap(gamepad1, gamepad2, evBus);
        started = false;
        
        setDefaultButtons();
        // TODO: load buttons from file
        btn_up_arrow = controllerMap.buttons.get("up_arrow");
        btn_down_arrow = controllerMap.buttons.get("down_arrow");
        btn_reset_pos = controllerMap.buttons.get("reset_pos");
        btn_delete_pos = controllerMap.buttons.get("delete_pos");
        btn_ok = controllerMap.buttons.get("ok");
        btn_stop_servo = controllerMap.buttons.get("stop_servo");
        btn_exit_to_menu = controllerMap.buttons.get("exit_to_menu");
        ax_change_position = controllerMap.axes.get("change_pos");
    }
    
    @Override
    public void init_loop()
    {
        loop();
    }
    
    @Override
    public void start()
    {
        started = true;
    }
    
    @Override
    public void loop()
    {
        if (currScene != null)
        {
            if (currScene.firstLoop)
            {
                currScene.firstLoop = false;
                telemetry.clearAll();
                telemetry.update();
                currScene.init();
            }
            else currScene.loop();
            controllerMap.update();
            telemetry.update();
        }
    }
    
    private void setDefaultButtons()
    {
        controllerMap.setButtonMap("up_arrow", ControllerMap.Controller.gamepad1, ControllerMap.Button.dpad_up);
        controllerMap.setButtonMap("down_arrow", ControllerMap.Controller.gamepad1, ControllerMap.Button.dpad_down);
        controllerMap.setButtonMap("reset_pos", ControllerMap.Controller.gamepad1, ControllerMap.Button.x);
        controllerMap.setButtonMap("delete_pos", ControllerMap.Controller.gamepad1, ControllerMap.Button.y);
        controllerMap.setButtonMap("ok", ControllerMap.Controller.gamepad1, ControllerMap.Button.b);
        controllerMap.setButtonMap("stop_servo", ControllerMap.Controller.gamepad1, ControllerMap.Button.a);
        controllerMap.setButtonMap("exit_to_menu", ControllerMap.Controller.gamepad1, ControllerMap.Button.right_bumper);
        controllerMap.setAxisMap("change_pos", ControllerMap.Controller.gamepad1, ControllerMap.Axis.left_stick_y);
    }
    
    private class SceneChoose extends Scene
    {
        private Scroll servoChooser;
        
        @Override
        public void init()
        {
            servoChooser = new Scroll(8);
            Servo[] servos = enumerateServos(hardwareMap);
            for (int i = 0; i < servos.length; i++)
            {
                if (i % SERVOS_PER_CONTROLLER == 0)
                {
                    servoChooser.addLine(new HTMLString(
                            "span", "style=\"color: #aaaaaa;\"",
                            servoControllers[i / 6].getConnectionInfo()).toString(), -1);
                }
                
                if (servos[i] == null)
                {
                    servoChooser.addLine(new HTMLString(
                            "span", "style=\"color: #aaaaaa;\"",
                            "" + i + " -- [empty]").toString(), i);
                }
                else
                {
                    String servoName = hardwareMap.getNamesOf(servos[i]).toArray(new String[0])[0];
                    servoChooser.addLine("" + i + " -- " + servoName, i);
                }
            }
        }
        
        @Override
        public void loop()
        {
            int up_edge = btn_up_arrow.edge();
            int dn_edge = btn_down_arrow.edge();
            if (up_edge > 0) servoChooser.press(-1);
            else if (dn_edge > 0) servoChooser.press(1);
            
            if (btn_up_arrow.get() || btn_down_arrow.get()) servoChooser.hold();
            
            if (btn_ok.edge() > 0)
            {
                int sel_servo = (Integer) servoChooser.getLineMeta(servoChooser.getScrollPos());
                if (sel_servo >= 0)
                {
                    servoId = sel_servo;
                    currScene = new SceneMove();
                }
            }
            servoChooser.render(telemetry);
        }
    }
    
    private class SceneMove extends Scene
    {
        private LynxServoController controller;
        private int servo;
        private double pos = 0;
        private Scroll posList;
        private Telemetry.Item status;
        
        @Override
        void init()
        {
            controller = (LynxServoController) servoControllers[servoId / 6];
            servo = servoId % 6;
            
            controller.pwmDisable();
            controller.setServoPwmEnable(servo);
            posList = new Scroll(8);
            posList.setScrollMode(Scroll.SCROLL_WRAP);
            posList.addLine("", Double.NaN); // dummy value, will get set immediately
            status = telemetry.addLine().addData("", "");
        }
        
        @Override
        void loop()
        {
            if (!started)
            {
                status.setCaption("Press the PLAY button to start");
                return;
            }
            double dt = Time.now() - lastTick; // seconds per loop
            lastTick = Time.now();
            double step = Math.pow(-ax_change_position.get(), 3) * 0.3 * dt;
            pos += step;
            if (pos > 1) pos = 1;
            else if (pos < 0) pos = 0;
            
            controller.setServoPosition(servo, pos);
            
            status.setCaption("Servo positions");
            
            boolean change = false;
            if (btn_up_arrow.edge() > 0)
            {
                posList.press(-1);
                change = true;
            }
            if (btn_down_arrow.edge() > 0)
            {
                posList.press(1);
                change = true;
            }
            if ((btn_up_arrow.get() || btn_down_arrow.get()) && !change) change = posList.hold();
            if (btn_delete_pos.edge() > 0 && posList.size() > 1)
            {
                posList.removeLine(posList.getScrollPos());
                change = true;
            }
            
            if (change) pos = (Double) posList.getSelectedMeta();
            else if (btn_reset_pos.edge() > 0)
            {
                posList.addLine("", Double.NaN); // dummy, will get filled immediately
                posList.setScrollPos(posList.size() - 1);
            }
            
            if (pos != (Double) posList.getSelectedMeta())
            {
                posList.setLine(posList.getScrollPos(), String.format("%.3f", pos));
                posList.setLineMeta(posList.getScrollPos(), pos);
            }
            
            posList.render(telemetry);
            
            if (btn_exit_to_menu.edge() > 0) currScene = new SceneChoose();
        }
    }
}
