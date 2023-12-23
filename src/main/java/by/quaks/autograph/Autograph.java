package by.quaks.autograph;

import by.quaks.autograph.commands.AutographCommand;
import by.quaks.autograph.config.ConfigManager;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.kyori.adventure.platform.fabric.FabricClientAudiences;
import net.kyori.adventure.platform.fabric.FabricServerAudiences;


import java.io.IOException;

public class Autograph implements ModInitializer {
    private static FabricServerAudiences serverAdventure;
    private static FabricClientAudiences clientAdventure;
    public static ConfigManager configReader;

    public static FabricServerAudiences serverAdventure() {
        if(serverAdventure == null) {
            return null;
        }
        return serverAdventure;
    }
    public static FabricClientAudiences clientAdventure() {
        if(clientAdventure == null) {
            return null;
        }
        return clientAdventure;
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
            serverAdventure = FabricServerAudiences.of(server);
        });
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            serverAdventure = null;
        });
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
            System.out.println("CLIENT_STARTED");
            clientAdventure = FabricClientAudiences.of();
        });
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> clientAdventure = null);
    }

}
