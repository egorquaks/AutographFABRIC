package by.quaks.autograph;

import by.quaks.autograph.config.ConfigManager;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.kyori.adventure.platform.fabric.FabricServerAudiences;


import java.io.IOException;

public class Autograph implements ModInitializer {
    private static FabricServerAudiences adventure;
    public static ConfigManager configReader;

    public static FabricServerAudiences adventure() {
        if(adventure == null) {
            throw new IllegalStateException("Tried to access Adventure without a running server!");
        }
        return adventure;
    }
    @Override
    public void onInitialize() {
        try {
            ConfigManager.createConfigFile();
            ConfigManager.initConfig();
            configReader = new ConfigManager();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            System.out.println("SERVER_STARTING"); // TODO: 23.11.2023 FOR REMOVAL
            adventure = FabricServerAudiences.of(server);
        });
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> adventure = null);
    }

}
