package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.opmodes.teleop.ControlMgr;
import org.firstinspires.ftc.teamcode.opmodes.teleop.ControlModule;
import org.firstinspires.ftc.teamcode.util.Status;
import org.firstinspires.ftc.teamcode.vision.ImageDraw;

public class LineFinder {
    public final ColorSensor line_finder;
    public int alpha_init = -1;

    public LineFinder(ColorSensor line_finder){
        this.line_finder = line_finder;
    }

    public void initialize(){
        alpha_init = line_finder.alpha();
    }

    public boolean lineFound() {
        double check_value = alpha_init * 1;
        return line_finder.alpha() > check_value;
    }
}
