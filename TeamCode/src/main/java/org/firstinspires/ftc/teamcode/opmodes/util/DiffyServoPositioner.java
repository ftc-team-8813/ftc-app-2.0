package org.firstinspires.ftc.teamcode.opmodes.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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
import org.firstinspires.ftc.teamcode.util.Configuration;
import org.firstinspires.ftc.teamcode.util.Storage;
import org.firstinspires.ftc.teamcode.util.Time;
import org.firstinspires.ftc.teamcode.util.event.EventBus;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import static org.firstinspires.ftc.robotcore.external.Telemetry.DisplayFormat.HTML;

@TeleOp(group = "util", name = "Differential Servo Positioner")
@Disabled
// much of this code is copied from ServoPositioner.java (from commit ab4c65f)
public class DiffyServoPositioner extends LoggingOpMode
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
    private ControllerMap.ButtonEntry btn_toggle_mode;
    private ControllerMap.AxisEntry ax_change_position_a;
    private ControllerMap.AxisEntry ax_change_position_b;
    
    private static abstract class Scene
    {
        private boolean firstLoop = true;
        
        abstract void init();
        
        abstract void loop();
        
        void exit() {}
    }
    
    private Scene currScene;
    private ControllerMap controllerMap;
    private boolean started;
    private int servoA;
    private int servoB;
    private ServoController[] servoControllers;
    private EventBus evBus;
    
    private String fileName;
    
    @Override
    public void init()
    {
        super.init();
        Storage.createDirs("servo_positions");
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
        btn_toggle_mode = controllerMap.buttons.get("toggle_mode");
        ax_change_position_a = controllerMap.axes.get("change_pos_a");
        ax_change_position_b = controllerMap.axes.get("change_pos_b");
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
        controllerMap.update();
        if (currScene != null)
        {
            Scene oldScene = currScene;
            if (currScene.firstLoop)
            {
                currScene.firstLoop = false;
                telemetry.clearAll();
                telemetry.update();
                currScene.init();
            }
            else currScene.loop();
            
            if (currScene != oldScene)
            {
                oldScene.exit();
            }
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
        controllerMap.setButtonMap("toggle_mode", ControllerMap.Controller.gamepad1, ControllerMap.Button.back);
        controllerMap.setAxisMap("change_pos_a", ControllerMap.Controller.gamepad1, ControllerMap.Axis.left_stick_y);
        controllerMap.setAxisMap("change_pos_b", ControllerMap.Controller.gamepad1, ControllerMap.Axis.right_stick_y);
    }
    
    private class SceneChoose extends Scene
    {
        private Telemetry.Line status;
        private Scroll servoChooser;
        private int numPicked = 0;
        private final String[] indexes = {"first", "second"};
        
        @Override
        public void init()
        {
            status = telemetry.addLine();
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
            status.addData(String.format("Choose the %s servo", indexes[numPicked]), "");
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
                    if (numPicked == 0)
                    {
                        servoA = sel_servo;
                    }
                    else if (numPicked == 1)
                    {
                        servoB = sel_servo;
                        // set file name
                        String ctrlA = ((LynxServoController) servoControllers[servoA / 6]).getSerialNumber().toString();
                        String ctrlB = ((LynxServoController) servoControllers[servoA / 6]).getSerialNumber().toString();
                        fileName = String.format("servo_positions/%s.%d_%s.%d.json", ctrlA, servoA % 6, ctrlB, servoB % 6);
                        
                        currScene = new SceneMove();
                    }
                    numPicked++;
                }
            }
            servoChooser.render(telemetry);
        }
    }
    
    private class SceneMove extends Scene
    {
        private LynxServoController controllerA;
        private LynxServoController controllerB;
        private int cservoA;
        private int cservoB;
        private double posA = 0;
        private double posB = 0;
        private Scroll posList;
        private Telemetry.Item status;
        private double lastTick = 0;
        private boolean differential = true;
        
        @Override
        void init()
        {
            controllerA = (LynxServoController) servoControllers[servoA / 6];
            controllerB = (LynxServoController) servoControllers[servoB / 6];
            cservoA = servoA % 6;
            cservoB = servoB % 6;
            
            posList = new Scroll(8);
            posList.setScrollMode(Scroll.SCROLL_WRAP);
            double[] init_pos = load(posList);
            if (init_pos == null)
            {
                posList.addLine("", new double[]{Double.NaN, Double.NaN}); // dummy value, will get set immediately
            }
            else
            {
                posA = init_pos[0];
                posB = init_pos[1];
                for (int i = 0; i < posList.size(); i++)
                {
                    updateLabel(i);
                }
            }
            status = telemetry.addLine().addData("", "");
        }
        
        void updateLabel(int index)
        {
            double[] metadata = (double[]) posList.getLineMeta(index);
            posList.setLine(index, String.format("%.3f, %.3f", metadata[0], metadata[1]));
        }
        
        @Override
        void loop()
        {
            if (lastTick == 0)
                lastTick = Time.now(); // avoid large jump from 0 to whenever we are now
            if (!started)
            {
                status.setCaption("Press the PLAY button to start");
            }
            double dt = Time.now() - lastTick; // seconds per loop
            lastTick = Time.now();
            double step1 = Math.pow(-ax_change_position_a.get(), 3) * 0.3 * dt;
            double step2 = Math.pow(-ax_change_position_b.get(), 3) * 0.3 * dt;
            
            if (differential) posA += step1 + step2;
            else posA += step1;
            if (posA > 1) posA = 1;
            else if (posA < 0) posA = 0;
            
            if (differential) posB += step1 - step2;
            else posB += step2;
            if (posB > 1) posB = 1;
            else if (posB < 0) posB = 0;
            
            if (started)
            {
                controllerA.setServoPosition(cservoA, posA);
                controllerB.setServoPosition(cservoB, posB);
                
                status.setCaption("Servo positions");
            }
            
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
            
            if (change)
            {
                double[] data = (double[]) posList.getSelectedMeta();
                posA = data[0];
                posB = data[1];
            }
            else if (btn_reset_pos.edge() > 0)
            {
                posList.addLine("", new double[]{Double.NaN, Double.NaN}); // dummy, will get filled immediately
                posList.setScrollPos(posList.size() - 1);
            }
            
            double[] listPositions = (double[]) posList.getSelectedMeta();
            if (posA != listPositions[0] || posB != listPositions[1])
            {
                // modify the metadata in-place
                listPositions[0] = posA;
                listPositions[1] = posB;
                updateLabel(posList.getScrollPos());
            }
            
            posList.render(telemetry);
            
            if (btn_toggle_mode.edge() > 0)
            {
                differential = !differential;
            }
            
            if (btn_exit_to_menu.edge() > 0)
            {
                save();
                currScene = new SceneChoose();
            }
        }
    }
    
    private double[] load(Scroll posList)
    {
        File dataFile = Storage.getFile(fileName);
        if (dataFile.exists())
        {
            JsonObject root = Configuration.readJson(dataFile);
            JsonArray positions = root.get("positions").getAsJsonArray();
            if (positions.size() > 0)
            {
                for (int i = 0; i < positions.size(); i++)
                {
                    JsonArray data = positions.get(i).getAsJsonArray();
                    posList.addLine("", new double[]{data.get(0).getAsDouble(), data.get(1).getAsDouble()});
                }
                return (double[]) posList.getLineMeta(0);
            }
        }
        return null;
    }
    
    private void save()
    {
        if (currScene != null && currScene instanceof SceneMove)
        {
            SceneMove scn = (SceneMove) currScene;
            Scroll posList = scn.posList;
            
            JsonObject root = new JsonObject();
            JsonArray positions = new JsonArray();
            root.add("positions", positions);
            
            for (int i = 0; i < posList.size(); i++)
            {
                double[] data = (double[]) posList.getLineMeta(i);
                JsonArray pos = new JsonArray();
                pos.add(data[0]);
                pos.add(data[1]);
                positions.add(pos);
            }
            
            try (FileWriter w = new FileWriter(Storage.createFile(fileName)))
            {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String jsonData = gson.toJson(root);
                w.write(jsonData + "\n");
            }
            catch (IOException e)
            {
                throw new IllegalStateException("Position save failed: " + e.getMessage(), e);
            }
        }
    }
    
    @Override
    public void stop()
    {
        super.stop();
        save();
    }
}
