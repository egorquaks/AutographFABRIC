package by.quaks.autograph.client;

import by.quaks.autograph.commands.AutographCommand;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.kyori.adventure.platform.fabric.FabricClientAudiences;

public class AutographClient implements ClientModInitializer {
    /**
     * Runs the mod initializer on the client environment.
     */
    private static FabricClientAudiences clientAdventure;
    public static FabricClientAudiences clientAdventure() {
        if(clientAdventure == null) {
            throw new IllegalStateException("Tried to access Adventure without a running client!");
        }
        return clientAdventure;
    }
    @Override
    public void onInitializeClient() {
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            System.out.println("CLIENT_STARTED");
            clientAdventure = FabricClientAudiences.of();
        });
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> clientAdventure = null);
        new AutographCommand().register();
        System.out.println("COMMAND REGISTRATION CLIENT");
    }
}
