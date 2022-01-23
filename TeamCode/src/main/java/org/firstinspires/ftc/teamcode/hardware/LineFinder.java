package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.ColorSensor;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.opmodes.teleop.ControlMgr;
import org.firstinspires.ftc.teamcode.opmodes.teleop.ControlModule;
import org.firstinspires.ftc.teamcode.util.Status;
import org.firstinspires.ftc.teamcode.vision.ImageDraw;

public class LineFinder {
    private final ColorSensor line_finder;
    private double alpha_init;

    public LineFinder(ColorSensor line_finder){
        this.line_finder = line_finder;
    }

    public void initialize(){
        alpha_init = line_finder.alpha();
    }

    public boolean lineFound() {
        return line_finder.alpha() >= (alpha_init * Status.LIGHT_MULTIPLIER);
    }

    public void update(Telemetry telemetry){
        telemetry.addData("light", line_finder.alpha());
        telemetry.addData("red", line_finder.red());
        telemetry.addData("green", line_finder.green());
        telemetry.addData("blue", line_finder.blue());
    }
}
