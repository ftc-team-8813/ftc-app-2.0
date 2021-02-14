package org.firstinspires.ftc.teamcode.hardware.autoshoot;

import org.firstinspires.ftc.teamcode.util.Storage;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class ShooterDataLogger {
    public ArrayList<String> hypos;
    public ArrayList<String> powers;

    public ShooterDataLogger(){
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
        if (hypos.isEmpty() && powers.isEmpty())
            return;
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
