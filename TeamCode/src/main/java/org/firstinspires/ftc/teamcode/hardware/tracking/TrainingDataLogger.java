package org.firstinspires.ftc.teamcode.hardware.tracking;

import org.firstinspires.ftc.teamcode.util.Storage;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class TrainingDataLogger {
    public ArrayList<String> hypos;
    public ArrayList<String> powers;
    public ArrayList<String> headings;

    public TrainingDataLogger(){
        this.hypos = new ArrayList<>();
        this.powers = new ArrayList<>();
        this.headings = new ArrayList<>();
    }

    public void addDataPoint(double hypo, double power, double heading){
        hypos.add(String.format("%.2f", hypo));
        powers.add(String.format("%.2f", power));
        headings.add(String.format("%.2f", heading));
    }

    public void removeLastPoint(){
        int last_index = hypos.size() - 1;
        hypos.remove(last_index);
        powers.remove(last_index);
        headings.remove(last_index);
    }

    public void dump(){
        try (FileWriter writer = new FileWriter(Storage.getFile("training_data_logger.csv"), true)){
            for (int i = 0; i < hypos.size() - 1; i++){
                writer.append(hypos.get(i));
                writer.append(powers.get(i));
                writer.append(headings.get(i));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
