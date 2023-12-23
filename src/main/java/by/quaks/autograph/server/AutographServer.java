package by.quaks.autograph.server;

import by.quaks.autograph.commands.AutographCommand;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.kyori.adventure.platform.fabric.FabricServerAudiences;

public class AutographServer implements DedicatedServerModInitializer {
    private static FabricServerAudiences serverAdventure;
    public static FabricServerAudiences serverAdventure() {
        if(serverAdventure == null) {
            throw new IllegalStateException("Tried to access Adventure without a running server!");
        }
        return serverAdventure;
    }
    @Override
    public void onInitializeServer() {
        new AutographCommand().register();
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            System.out.println("SERVER_STARTING"); // TODO: 23.11.2023 FOR REMOVAL
            serverAdventure = FabricServerAudiences.of(server);
        });
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> serverAdventure = null);
    }
}
