package by.quaks.autograph.config;

import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
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
            configData = yaml.load(fis);
            fis.close();
        } catch (FileNotFoundException e) {
            System.out.println("Файл config.yml не найден.");
        } catch (Exception e) {
            System.out.println("Ошибка чтения файла: " + e.getMessage());
        }
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

