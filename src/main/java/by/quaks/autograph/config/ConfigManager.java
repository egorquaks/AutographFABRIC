package by.quaks.autograph.config;

import by.quaks.autograph.Autograph;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {
    private static Map<String, Object> configData;
    public static void initConfig() {
        String currentDirectory = System.getProperty("user.dir");
        String configAutographDirectoryPath = currentDirectory + File.separator + "config" + File.separator + "autograph";
        String filePath = configAutographDirectoryPath + File.separator + "config.yml";
        Yaml yaml = new Yaml();
        try {
            FileInputStream fis = new FileInputStream(filePath);
            Map<String, Object> data = yaml.load(fis);
            fis.close();
            configData = flattenYaml(data);
        } catch (FileNotFoundException e) {
            Autograph.LOGGER.error("Файл config.yml не найден.");
        } catch (Exception e) {
            Autograph.LOGGER.error("Ошибка чтения файла: " + e.getMessage());
        }
    }
    @SuppressWarnings("unchecked")
    private static Map<String, Object> flattenYaml(Map<String, Object> map) {
        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof Map) {
                Map<String, Object> nestedMap = (Map<String, Object>) value;
                Map<String, Object> nestedResult = flattenYaml(nestedMap);
                nestedResult.forEach((nestedKey, nestedValue) ->
                        result.put(key + "." + nestedKey, nestedValue));
            } else {
                result.put(key, value);
            }
        }
        return result;
    }
    public static void createConfigFile() throws IOException {
        InputStream inputStream = ConfigManager.class.getClassLoader().getResourceAsStream("config.yml");

        if (inputStream != null) {
            String currentDirectory = System.getProperty("user.dir");
            String configAutographDirectoryPath = currentDirectory + File.separator + "config" + File.separator + "autograph";
            File configAutographDirectory = new File(configAutographDirectoryPath);
            File fileInConfigAutograph = new File(configAutographDirectory, "config.yml");

            configAutographDirectory.mkdirs();
            if(!fileInConfigAutograph.exists()){
                fileInConfigAutograph.createNewFile();
                Files.copy(inputStream, fileInConfigAutograph.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        } else {
            System.err.println("Файл config.yml внутри JAR не найден.");
        }
    }

    public String getString(String option) {
        String autographSetting = "null";
        System.out.println(configData.keySet());
        if (configData != null && configData.containsKey(option)) {
            autographSetting = (String) configData.get(option);
        }

        return autographSetting;
    }
    public boolean getBoolean(String option) {
        boolean autographSetting = false;
        if (configData != null && configData.containsKey(option)) {
            autographSetting = (boolean) configData.get(option);
        }

        return autographSetting;
    }
}

