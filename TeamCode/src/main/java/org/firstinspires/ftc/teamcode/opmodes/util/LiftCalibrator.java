package org.firstinspires.ftc.teamcode.opmodes.util;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.hardware.Turret;
import org.firstinspires.ftc.teamcode.util.Storage;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

@TeleOp(name="Lift Calibrator")
@Disabled
public class LiftCalibrator extends OpMode
{
    
    private HashMap<Integer, Double> data;
    private int minPos = Integer.MAX_VALUE;
    private int maxPos = Integer.MIN_VALUE;
    
    private DcMotor enc;
    private Turret turret;
    
    @Override
    public void init()
    {
        data = new HashMap<>();
        Robot r = new Robot(hardwareMap);
        turret = r.turret;
        enc = hardwareMap.dcMotor.get("top_right");
        enc.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }
    
    @Override
    public void loop()
    {
        double value = turret.getRawPos()[2] / 3.3;
        int pos = enc.getCurrentPosition();
        telemetry.addData("voltage", "%.3f", value);
        telemetry.addData("pos", pos);
        telemetry.addData("data", "pts=%d min=%d max=%d", data.size(), minPos, maxPos);
        
        if (pos % 10 == 0)
        {
            data.put(pos, value);
            if (pos < minPos) minPos = pos;
            if (pos > maxPos) maxPos = pos;
        }
    }
    
    @Override
    public void stop()
    {
        int range = maxPos - minPos;
        Integer[] keys = data.keySet().toArray(new Integer[0]);
        Arrays.sort(keys);
        try (FileWriter w = new FileWriter(Storage.createFile("lift_cal_turret.csv")))
        {
            int i = keys.length;
            while (i --> 0)
            {
                int key = keys[i];
                double x = 1 - (double)(key - minPos) / range;
                double y = data.get(key);
                w.write(String.format("%.3f,%.4f\n", x, y));
            }
        }
        catch (IOException e) { e.printStackTrace(); }
    }
}
