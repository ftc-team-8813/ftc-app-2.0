package org.firstinspires.ftc.teamcode.util;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;

public class Configurations {
    Gson gson;
    String path;

    public Configurations(String path) {
        gson = new Gson();
        this.path = path;
    }

    public void addData(String key, double value) throws IOException {
        File file = Storage.createFile(path);
        JsonObject data = gson.fromJson(new FileReader(file), JsonObject.class);
        data.addProperty(key, value);
        gson.toJson(data, new FileWriter(file));
    }
}