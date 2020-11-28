package org.firstinspires.ftc.teamcode.input;

import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.Gamepad;

public class ControllerMap
{
    private Gamepad gamepad1;
    private Gamepad gamepad2;
    
    private int oldButtons1;
    private int oldButtons2;
    private float triggerThreshold = 0.8f;
    
    public ControllerMap(Gamepad gamepad1, Gamepad gamepad2)
    {
        this.gamepad1 = gamepad1;
        this.gamepad2 = gamepad2;
    }
    
    public enum Controller
    {
        gamepad1,
        gamepad2
    };
    
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
    };
    
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
    };
    
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
            0x00: s8  ROBOCOL_GAMEPAD_VERSION
            0x01: s32 id
            0x05: s64 timestamp
            0x0d: f32 left_stick_x
            0x11: f32 left_stick_y
            0x15: f32 right_stick_x
            0x19: f32 right_stick_y
            0x1d: f32 left_trigger
            0x21: s32 buttons
         */
        
        // build an int out of bytes. Everything is signed and sign-extended in Java, so this gets tedious.
        int buttons = ((data[0x21] & 0xFF) << 24) | ((data[0x22] & 0xFF) << 16) | ((data[0x23] & 0xFF) << 8) | (data[0x24] & 0xFF);
        
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
    
    public boolean getButton(Controller gamepad, Button button)
    {
        return (getButtons(gamepad) & (1 << button.ordinal())) != 0;
    }
}
