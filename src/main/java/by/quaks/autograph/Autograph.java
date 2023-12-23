package by.quaks.autograph;

import by.quaks.autograph.config.ConfigManager;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Autograph implements ModInitializer {
    public static ConfigManager configReader;
    public static final Logger LOGGER = LoggerFactory.getLogger("Autograph");
    @Override
    public void onInitialize() {
        try {
            ConfigManager.createConfigFile();
            ConfigManager.initConfig();
            configReader = new ConfigManager();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
