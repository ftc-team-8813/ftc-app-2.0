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


    public boolean lineFound() {
        return (line_finder.alpha() > (alpha_init * Status.LIGHT_MULTIPLIER));
    }

    public void update(Telemetry telemetry){
        telemetry.addData("line found", lineFound());
        telemetry.addData("init light", alpha_init);
        telemetry.addData("light", line_finder.alpha());
//        telemetry.addData("red", line_finder.red());
//        telemetry.addData("green", line_finder.green());
//        telemetry.addData("blue", line_finder.blue());
    }
}
