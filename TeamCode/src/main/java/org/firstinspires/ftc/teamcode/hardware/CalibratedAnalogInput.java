package org.firstinspires.ftc.teamcode.hardware;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.qualcomm.robotcore.hardware.AnalogInput;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class CalibratedAnalogInput
{
    private Double[] calibrationX, calibrationY;
    private AnalogInput in;
    
    public CalibratedAnalogInput(AnalogInput in, File calibrationJson)
    {
        this.in = in;
        loadCalibration(calibrationJson);
    }
    
    public double get()
    {
        double value = in.getVoltage() / in.getMaxVoltage();
        return linearize(value);
    }
    
    private double linearize(double y)
    {
        int idx = 0;
        for (; idx < calibrationX.length; idx++)
        {
            if (calibrationY[idx] > y) break;
        }
        if (idx == 0) return 0;
        if (idx == calibrationX.length) return 1;
        double xbase = calibrationX[idx];
        double dx = (calibrationX[idx] - calibrationX[idx-1])
                * (y - calibrationY[idx]) / (calibrationY[idx] - calibrationY[idx-1]);
        return xbase + dx;
    }
    
    private void loadCalibration(File calibrationJson)
    {
        /*
            Calibration data format:
            {
              "xValues": [ X values (linear values) ],
              "yValues": [ Y values (nonlinear values) ]
            }
        */
        try (JsonReader reader = new JsonReader(new FileReader(calibrationJson)))
        {
            JsonParser parser = new JsonParser();
            JsonObject root = parser.parse(reader).getAsJsonObject();
            JsonArray xValues = root.get("xValues").getAsJsonArray();
            JsonArray yValues = root.get("yValues").getAsJsonArray();
            ArrayList<Double> xList = new ArrayList<>();
            ArrayList<Double> yList = new ArrayList<>();
            for (JsonElement elem : xValues)
            {
                xList.add(elem.getAsDouble());
            }
            for (JsonElement elem : yValues)
            {
                yList.add(elem.getAsDouble());
            }
            calibrationX = xList.toArray(new Double[0]);
            calibrationY = yList.toArray(new Double[0]);
        } catch (IOException | JsonParseException e)
        {
            throw new IllegalStateException("Unable to load calibration data", e);
        }
    }
}
