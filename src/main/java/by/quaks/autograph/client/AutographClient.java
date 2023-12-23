package by.quaks.autograph.client;

import by.quaks.autograph.commands.AutographCommand;
import net.fabricmc.api.ClientModInitializer;

public class AutographClient implements ClientModInitializer {
    /**
     * Runs the mod initializer on the client environment.
     */
    @Override
    public void onInitializeClient() {
        new AutographCommand().register();
        System.out.println("COMMAND REGISTRATION CLIENT");
    }
}
