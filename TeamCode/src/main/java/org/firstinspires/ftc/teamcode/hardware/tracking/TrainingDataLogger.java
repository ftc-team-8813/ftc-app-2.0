package org.firstinspires.ftc.teamcode.hardware.tracking;

import org.firstinspires.ftc.teamcode.util.Storage;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class TrainingDataLogger {
    public ArrayList<String> hypos;
    public ArrayList<String> powers;

    public TrainingDataLogger(){
        this.hypos = new ArrayList<>();
        this.powers = new ArrayList<>();
    }

    public void addDataPoint(double hypo, double power){
        hypos.add(String.format("%.2f", hypo));
        powers.add(String.format("%.2f", power));
    }

    public void removeLastPoint(){
        int last_index = hypos.size() - 1;
        hypos.remove(last_index);
        powers.remove(last_index);
    }

    public void dump(){
        try (FileWriter writer = new FileWriter(Storage.getFile("training_data_logger.csv"), true)){
            for (int i = 0; i < hypos.size() - 1; i++){
                writer.append(hypos.get(i)).append(",");
                writer.append(powers.get(i)).append(",");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
