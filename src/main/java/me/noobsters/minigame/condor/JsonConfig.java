package me.noobsters.minigame.condor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.bukkit.Bukkit;

import lombok.Getter;
import lombok.Setter;

public class JsonConfig {
    private static String CONDOR_PATH = Bukkit.getWorldContainer().getPath() + File.separatorChar + "condor"
            + File.separatorChar;
    private static Gson gson = new Gson();

    private @Getter @Setter JsonObject jsonObject = new JsonObject();
    private @Getter File file;

    public JsonConfig(String filename) throws IOException {
        file = new File(CONDOR_PATH + filename);
        if (!file.exists()) {
            file.getParentFile().mkdir();
            writeFile(file);
        } else {
            readFile(file);
        }
    }

    public void save() throws Exception {
        writeFile(file);
    }

    public void load() throws Exception {
        readFile(file);
    }

    private void writeFile(File path) throws IOException {
        var writer = new FileWriter(path);

        gson.toJson(jsonObject, writer);
        writer.flush();
        writer.close();

    }

    private void readFile(File path) throws IOException {
        var reader = Files.newBufferedReader(Paths.get(path.getPath()));
        var object = gson.fromJson(reader, JsonObject.class);
        reader.close();

        jsonObject = object;
    }

}
