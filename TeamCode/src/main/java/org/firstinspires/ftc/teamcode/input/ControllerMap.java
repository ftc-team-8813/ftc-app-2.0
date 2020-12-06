package org.firstinspires.ftc.teamcode.input;

import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.telemetry.HTMLString;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class ControllerMap
{
    
    private Gamepad gamepad1;
    private Gamepad gamepad2;
    
    private int[] oldButtons = {0, 0};
    private static final float triggerThreshold = 0.8f;
    public final Map<String, ButtonEntry> buttons;
    public final Map<String, AxisEntry> axes;
    
    public ControllerMap(Gamepad gamepad1, Gamepad gamepad2)
    {
        this.gamepad1 = gamepad1;
        this.gamepad2 = gamepad2;
        this.buttons = new HashMap<>();
        this.axes = new HashMap<>();
    }
    
    // TODO: Add configuration tool for this
    
    public void setButtonMap(String name, Controller c, Button b)
    {
        buttons.put(name, new ButtonEntry(c, b));
    }
    
    public void setButtonMap(String name, String c, String b)
    {
        setButtonMap(name, Controller.valueOf(c), Button.valueOf(b));
    }
    
    public void setAxisMap(String name, Controller c, Axis a)
    {
        axes.put(name, new AxisEntry(c, a));
    }
    
    public void setAxisMap(String name, String c, String a)
    {
        setAxisMap(name, Controller.valueOf(c), Axis.valueOf(a));
    }
    
    public Gamepad getController(Controller c)
    {
        if      (c == Controller.gamepad1) return gamepad1;
        else if (c == Controller.gamepad2) return gamepad2;
        return null;
    }
    
    public int getButtons(Controller c)
    {
        Gamepad gamepad = getController(c);
        byte[] data;
        try { data = gamepad.toByteArray(); }
        catch (RobotCoreException e) { return -1; }
        /*  buffer:
            0x00: s32  ROBOCOL_GAMEPAD_VERSION
            0x04: s32 id
            0x08: s64 timestamp
            0x0c: f32 left_stick_x
            0x10: f32 left_stick_y
            0x14: f32 right_stick_x
            0x18: f32 right_stick_y
            0x1c: f32 left_trigger
            0x20: f32 right_trigger
            0x24: s32 buttons
         */
        
        // build an int out of bytes. Everything is signed and sign-extended in Java, so this gets tedious.
        int buttons = ((data[0x2a] & 0xFF) << 24) | ((data[0x2b] & 0xFF) << 16) | ((data[0x2c] & 0xFF) << 8) | (data[0x2d] & 0xFF);
        
        if (gamepad.left_stick_y > triggerThreshold)   buttons |= (1 << Button.left_stick_y_pos.ordinal());
        if (gamepad.left_stick_y < -triggerThreshold)  buttons |= (1 << Button.left_stick_y_neg.ordinal());
        if (gamepad.left_stick_x > triggerThreshold)   buttons |= (1 << Button.left_stick_x_pos.ordinal());
        if (gamepad.left_stick_x < -triggerThreshold)  buttons |= (1 << Button.left_stick_x_neg.ordinal());
        if (gamepad.right_stick_y > triggerThreshold)  buttons |= (1 << Button.right_stick_y_pos.ordinal());
        if (gamepad.right_stick_y < -triggerThreshold) buttons |= (1 << Button.right_stick_y_neg.ordinal());
        if (gamepad.right_stick_x > triggerThreshold)  buttons |= (1 << Button.right_stick_x_pos.ordinal());
        if (gamepad.right_stick_x < -triggerThreshold) buttons |= (1 << Button.right_stick_x_neg.ordinal());
        if (gamepad.left_trigger > triggerThreshold)   buttons |= (1 << Button.left_trigger.ordinal());
        if (gamepad.right_trigger > triggerThreshold)  buttons |= (1 << Button.right_trigger.ordinal());
        
        return buttons;
    }
    
    public float[] getAxes(Controller controller)
    {
        Gamepad gamepad = getController(controller);
        return new float[] {
                gamepad.left_stick_x,
                gamepad.left_stick_y,
                gamepad.right_stick_x,
                gamepad.right_stick_y,
                gamepad.left_trigger,
                gamepad.right_trigger
        };
    }
    
    public float getAxis(Controller controller, Axis axis)
    {
        return getAxes(controller)[axis.ordinal()];
    }
    
    public boolean getButton(Controller gamepad, Button button)
    {
        return (getButtons(gamepad) & (1 << button.ordinal())) != 0;
    }
    
    public int getEdge(Controller gamepad, Button button)
    {
        int buttonVal = getButtons(gamepad) & (1 << button.ordinal());
        int padnum = gamepad.ordinal();
        if (buttonVal != 0)
        {
            if ((oldButtons[padnum] & (1 << button.ordinal())) == 0)
            {
                oldButtons[padnum] |= (1 << button.ordinal());
                return 1;
            }
            else return 0;
        }
        else
        {
            if ((oldButtons[padnum] & (1 << button.ordinal())) != 0)
            {
                oldButtons[padnum] &= ~(1 << button.ordinal());
                return -1;
            }
            else return 0;
        }
    }
    
    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Classes and enums
    
    public enum Controller
    {
        gamepad1,
        gamepad2
    }
    
    public enum Button
    {
        right_bumper,
        left_bumper,
        back,
        start,
        guide,
        y,
        x,
        b,
        a,
        dpad_right,
        dpad_left,
        dpad_down,
        dpad_up,
        right_stick_button,
        left_stick_button,
        touchpad,
        // axis-mapped buttons
        left_stick_y_pos,
        left_stick_y_neg,
        left_stick_x_pos,
        left_stick_x_neg,
        right_stick_y_pos,
        right_stick_y_neg,
        right_stick_x_pos,
        right_stick_x_neg,
        left_trigger,
        right_trigger
    }
    
    public enum Axis
    {
        left_stick_x,
        left_stick_y,
        right_stick_x,
        right_stick_y,
        left_trigger,
        right_trigger,
        dpad_x,
        dpad_y
    }
    
    public class ButtonEntry
    {
        public final Controller controller;
        public final Button button;
        
        public ButtonEntry(Controller controller, Button button)
        {
            this.controller = controller;
            this.button = button;
        }
        
        public boolean get()
        {
            return getButton(controller, button);
        }
        
        public int edge()
        {
            return getEdge(controller, button);
        }
        
        public String toString()
        {
            return controller.name() + "." + button.name();
        }
        
        public String toStyledString()
        {
            int controllerN = controller.ordinal() + 1;
            String color = "#fff";
            if (button == Button.y)      color = "#fd3";
            else if (button == Button.x) color = "#57f";
            else if (button == Button.b) color = "#f10";
            else if (button == Button.a) color = "#7f1";
            HTMLString root = new HTMLString(null);
            root.addElement(new HTMLString("sup", null, Integer.toString(controllerN)));
            // TODO: add more human-friendly names
            root.addElement(new HTMLString("span", "style=\"color: " + color + ";\"", button.name()));
            return root.toString();
        }
    }
    
    public class AxisEntry
    {
        public final Controller controller;
        public final Axis axis;
        
        public AxisEntry(Controller controller, Axis axis)
        {
            this.controller = controller;
            this.axis = axis;
        }
        
        public float get()
        {
            return getAxis(controller, axis);
        }
        
        public String toString()
        {
            return axis.name();
        }
    }
}
