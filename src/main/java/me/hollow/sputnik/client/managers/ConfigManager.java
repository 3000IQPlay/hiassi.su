package me.hollow.sputnik.client.managers;

import com.google.gson.*;
import me.hollow.sputnik.Main;
import me.hollow.sputnik.api.property.Bind;
import me.hollow.sputnik.api.property.EnumConverter;
import me.hollow.sputnik.api.property.Setting;
import me.hollow.sputnik.client.modules.Module;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class ConfigManager {

    public ArrayList<Module> features = new ArrayList<>();
    public String config = "hiassi.su/config/";

    public void loadConfig(String name) {
        List<File> files = Arrays.stream(Objects.requireNonNull(new File("hiassi.su").listFiles()))
                .filter(File::isDirectory)
                .collect(Collectors.toList());
        if (files.contains(new File("hiassi.su/" + "modules" + "/"))) {
            config = "hiassi.su/" + "modules" + "/";
        } else {
            config = "hiassi.su/modules/";
        }
        for(Module feature : this.features) {
            try {
                loadSettings(feature);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveConfig(String name) {
        config = "hiassi.su/" + "modules" + "/";
        File path = new File(config);
        if (!path.exists()) {
            path.mkdir();
        }
        for (Module feature : this.features) {
            try {
                saveSettings(feature);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveSettings(Module feature) throws IOException {
        JsonObject object = new JsonObject();
        File directory = new File(config + getDirectory(feature));
        if (!directory.exists()) {
            directory.mkdir();
        }
        String featureName = config + getDirectory(feature) + feature.getLabel() + ".json";
        Path outputFile = Paths.get(featureName);
        if (!Files.exists(outputFile)) {
            Files.createFile(outputFile);
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(writeSettings(feature));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(outputFile)));
        writer.write(json);
        writer.close();
    }

    //TODO: String[] Array for FriendList? Also ENUMS!!!!!
    public static void setValueFromJson(Module feature, Setting setting, JsonElement element) {
        if (setting.getName().equals("Enabled")) {
            if (feature.isPersistent()) {
                feature.setEnabled(true);
            }
            if (element.getAsBoolean()) {
                feature.setEnabled(true);
            }
            return;
        }
        switch (setting.getType()) {
            case "Boolean":
                setting.setValue(element.getAsBoolean());
                break;
            case "Double":
                setting.setValue(element.getAsDouble());
                break;
            case "Float":
                setting.setValue(element.getAsFloat());
                break;
            case "Integer":
                setting.setValue(element.getAsInt());
                break;
            case "String":
                String str = element.getAsString();
                setting.setValue(str.replace("_", " "));
                break;
            case "Bind":
                setting.setValue(new Bind.BindConverter().doBackward(element));
                break;
            case "Enum":
                try {
                    EnumConverter converter = new EnumConverter(((Enum) setting.getValue()).getClass());
                    Enum value = converter.doBackward(element);
                    setting.setValue(value == null ? setting.getDefaultValue() : value);
                    break;
                } catch(Exception e) {
                    break;
                }
        }
    }

    //TODO: add everything with Settings here
    public void init() {
        features.addAll(Main.INSTANCE.getModuleManager().getModules());

        loadConfig("modules");
    }

    private void loadSettings(Module feature) throws IOException {
        String featureName = config + getDirectory(feature) + feature.getLabel() + ".json";
        Path featurePath = Paths.get(featureName);
        if (!Files.exists(featurePath)) {
            return;
        }
        loadPath(featurePath, feature);
    }

    private void loadPath(Path path, Module feature) throws IOException {
        InputStream stream = Files.newInputStream(path);
        try {
            loadFile(new JsonParser().parse(new InputStreamReader(stream)).getAsJsonObject(), feature);
        } catch (IllegalStateException e) {
            loadFile(new JsonObject(), feature);
        }
        stream.close();
    }

    private static void loadFile(JsonObject input, Module feature) {
        for (Map.Entry<String, JsonElement> entry : input.entrySet()) {
            String settingName = entry.getKey();
            JsonElement element = entry.getValue();
            boolean settingFound = false;
            for (Setting setting : feature.getSettings()) {
                if (settingName.equals(setting.getName())) {
                    try {
                        setValueFromJson(feature, setting, element);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    settingFound = true;
                }
            }
        }
    }

    public JsonObject writeSettings(Module feature) {
        JsonObject object = new JsonObject();
        JsonParser jp = new JsonParser();
        for (Setting setting : feature.getSettings()) {
            if (setting.isEnumSetting()) {
                EnumConverter converter = new EnumConverter(((Enum) setting.getValue()).getClass());
                object.add(setting.getName(), converter.doForward((Enum) setting.getValue()));
                continue;
            }

            if(setting.isStringSetting()) {
                String str = (String)setting.getValue();
                setting.setValue(str.replace(" ", "_"));
            }

            try {
                object.add(setting.getName(), jp.parse(setting.getValueAsString()));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return object;
    }

    public String getDirectory(Module feature) {
        String directory = "";
        if(feature instanceof Module) {
            directory = directory + ((Module) feature).getCategory().getLabel() + "/";
        }
        return directory;
    }

}
