package org.firstinspires.ftc.teamcode.input;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.telemetry.HTMLString;
import org.firstinspires.ftc.teamcode.util.event.EventBus;

import java.util.HashMap;
import java.util.Map;

public class ControllerMap
{

    public Gamepad gamepad1;
    public Gamepad gamepad2;
    private EventBus evBus;

    private int[] oldButtons = {0, 0};
    private int[] edges = {0, 0, 0, 0};
    private static final float triggerThreshold = 0.8f;
    public final Map<String, ButtonEntry> buttons;
    public final Map<String, AxisEntry> axes;

    public ControllerMap(Gamepad gamepad1, Gamepad gamepad2, EventBus evBus)
    {
        this.gamepad1 = gamepad1;
        this.gamepad2 = gamepad2;
        this.buttons = new HashMap<>();
        this.axes = new HashMap<>();
        this.evBus = evBus;
    }

    /**
     * @deprecated will be made private; use getButtonMap with default values instead
     **/
    @Deprecated
    public ButtonEntry setButtonMap(String name, Controller c, Button b)
    {
        ButtonEntry entry = new ButtonEntry(c, b, name);
        buttons.put(name, entry);
        return entry;
    }

    /**
     * @deprecated will be made private; use getButtonMap with default values instead
     **/
    @Deprecated
    public ButtonEntry setButtonMap(String name, String c, String b)
    {
        return setButtonMap(name, Controller.valueOf(c), Button.valueOf(b));
    }

    /**
     * @deprecated will be made private; use getAxisMap with default values instead
     **/
    @Deprecated
    public AxisEntry setAxisMap(String name, Controller c, Axis a)
    {
        AxisEntry entry = new AxisEntry(c, a, name);
        axes.put(name, entry);
        return entry;
    }

    /**
     * @deprecated will be made private; use getAxisMap with default values instead
     **/
    @Deprecated
    public AxisEntry setAxisMap(String name, String c, String a)
    {
        return setAxisMap(name, Controller.valueOf(c), Axis.valueOf(a));
    }

    public ButtonEntry getButtonMap(String name, String defController, String defButton)
    {
        if (!buttons.containsKey(name))
        {
            return setButtonMap(name, defController, defButton);
        }
        return buttons.get(name);
    }

    public AxisEntry getAxisMap(String name, String defController, String defAxis)
    {
        if (!axes.containsKey(name))
        {
            return setAxisMap(name, defController, defAxis);
        }
        return axes.get(name);
    }

    public void loadMap(JsonObject obj)
    {
        JsonObject buttons = obj.getAsJsonObject("buttons");
        JsonObject axes = obj.getAsJsonObject("axes");

        for (Map.Entry<String, JsonElement> entry : buttons.entrySet())
        {
            String name = entry.getKey();
            String[] value = entry.getValue().getAsString().split("\\.");
            if (value.length != 2)
                throw new IllegalArgumentException("Invalid button identifier: " + entry.getValue().getAsString());

            setButtonMap(name, value[0], value[1]);
        }

        for (Map.Entry<String, JsonElement> entry : axes.entrySet())
        {
            String name = entry.getKey();
            String[] value = entry.getValue().getAsString().split("\\.");
            if (value.length != 2)
                throw new IllegalArgumentException("Invalid button identifier: " + entry.getValue().getAsString());

            setAxisMap(name, value[0], value[1]);
        }
    }

    public JsonObject saveMap()
    {
        JsonObject root = new JsonObject();
        JsonObject buttons = new JsonObject();
        JsonObject axes = new JsonObject();

        root.add("buttons", buttons);
        root.add("axes", axes);

        for (ButtonEntry entry : this.buttons.values())
        {
            buttons.addProperty(entry.name, String.format("%s.%s", entry.controller.name(), entry.button.name()));
        }

        for (AxisEntry entry : this.axes.values())
        {
            axes.addProperty(entry.name, String.format("%s.%s", entry.controller.name(), entry.axis.name()));
        }

        return root;
    }

    public Gamepad getController(Controller c)
    {
        if (c == Controller.gamepad1) return gamepad1;
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

        if (gamepad.left_stick_y > triggerThreshold)
            buttons |= (1 << Button.left_stick_y_pos.ordinal());
        if (gamepad.left_stick_y < -triggerThreshold)
            buttons |= (1 << Button.left_stick_y_neg.ordinal());
        if (gamepad.left_stick_x > triggerThreshold)
            buttons |= (1 << Button.left_stick_x_pos.ordinal());
        if (gamepad.left_stick_x < -triggerThreshold)
            buttons |= (1 << Button.left_stick_x_neg.ordinal());
        if (gamepad.right_stick_y > triggerThreshold)
            buttons |= (1 << Button.right_stick_y_pos.ordinal());
        if (gamepad.right_stick_y < -triggerThreshold)
            buttons |= (1 << Button.right_stick_y_neg.ordinal());
        if (gamepad.right_stick_x > triggerThreshold)
            buttons |= (1 << Button.right_stick_x_pos.ordinal());
        if (gamepad.right_stick_x < -triggerThreshold)
            buttons |= (1 << Button.right_stick_x_neg.ordinal());
        if (gamepad.left_trigger > triggerThreshold)
            buttons |= (1 << Button.left_trigger.ordinal());
        if (gamepad.right_trigger > triggerThreshold)
            buttons |= (1 << Button.right_trigger.ordinal());

        return buttons;
    }

    public float[] getAxes(Controller controller)
    {
        Gamepad gamepad = getController(controller);
        return new float[]{
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
        return (oldButtons[gamepad.ordinal()] & (1 << button.ordinal())) != 0;
    }

    public int getEdge(Controller gamepad, Button button)
    {
        int padnum = gamepad.ordinal();
        if ((edges[2 * padnum] & 1 << button.ordinal()) != 0) return 1;
        else if ((edges[2 * padnum + 1] & 1 << button.ordinal()) != 0) return -1;
        return 0;
    }

    public void update()
    {
        int buttons1 = getButtons(Controller.gamepad1);
        int buttons2 = getButtons(Controller.gamepad2);
        int old1 = oldButtons[0];
        int old2 = oldButtons[1];

        int posedge1 = buttons1 & ~old1;
        int negedge1 = ~buttons1 & old1;
        int posedge2 = buttons2 & ~old2;
        int negedge2 = ~buttons2 & old2;
        edges[0] = posedge1;
        edges[1] = negedge1;
        edges[2] = posedge2;
        edges[3] = negedge2;

        oldButtons[0] = buttons1;
        oldButtons[1] = buttons2;

        for (ButtonEntry entry : buttons.values())
        {
            int edge = entry.edge();
            if (edge != 0)
            {
                evBus.pushEvent(new ButtonEvent(entry, edge));
            }
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

    public static final int NUM_BUTTONS = Button.values().length;

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

    public static final int NUM_AXES = Axis.values().length;

    public class ButtonEntry
    {
        public final Controller controller;
        public final Button button;
        public final String name;

        ButtonEntry(Controller controller, Button button, String name)
        {
            this.controller = controller;
            this.button = button;
            this.name = name;
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

        public int getEventID()
        {
            return controller.ordinal() * 0x20 + button.ordinal();
        }

        public String toStyledString()
        {
            int controllerN = controller.ordinal() + 1;
            String color = "#fff";
            if (button == Button.y) color = "#fd3";
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
        public final String name;

        AxisEntry(Controller controller, Axis axis, String name)
        {
            this.controller = controller;
            this.axis = axis;
            this.name = name;
        }

        public int getEventID()
        {
            return controller.ordinal() * 0x20 + axis.ordinal();
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
