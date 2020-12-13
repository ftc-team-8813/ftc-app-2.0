package org.firstinspires.ftc.teamcode.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import org.json.JSONObject;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class Configurations {
    public static void rewriteData(HashMap<String, Double> values, File file) throws IOException {
        JSONObject json = new JSONObject(values);
        try(FileWriter writer = new FileWriter(file)){
            writer.write(json.toString());
        } catch (IOException e){
            throw new IllegalStateException();
        }
    }

    public static HashMap<String, Double> readData(String[] keys, File file) {
        HashMap<String, Double> values = new HashMap<>();
        try (JsonReader reader = new JsonReader(new FileReader(file))){
            JsonParser parser = new JsonParser();
            JsonObject objects = parser.parse(reader).getAsJsonObject();
            for (String key : keys){
                values.put(key, objects.get(key).getAsDouble());
            }
            return values;
        } catch (IOException e) {
            throw new IllegalArgumentException();
        }
    }
}