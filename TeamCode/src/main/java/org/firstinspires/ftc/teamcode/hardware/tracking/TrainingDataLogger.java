package org.firstinspires.ftc.teamcode.hardware.tracking;

import org.firstinspires.ftc.teamcode.util.Storage;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class TrainingDataLogger {
    public static ArrayList<String> x_positions = new ArrayList<>();
    public static ArrayList<String> y_positions = new ArrayList<>();
    public static ArrayList<String> powers = new ArrayList<>();
    public static ArrayList<String> headings = new ArrayList<>();

    public static void addDataPoint(double x, double y, double power, double heading){
        x_positions.add(String.valueOf(x));
        y_positions.add(String.valueOf(y));
        powers.add(String.valueOf(power));
        headings.add(String.valueOf(heading));
    }

    public static void dump(){
        try (FileWriter writer = new FileWriter(Storage.getFile("training_data_logger"), true)){
            for (int i = 0; i < x_positions.size() - 1; i++){
                writer.append(x_positions.get(i));
                writer.append(y_positions.get(i));
                writer.append(powers.get(i));
                writer.append(headings.get(i));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
