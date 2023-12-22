package by.quaks.autograph.server;

import by.quaks.autograph.commands.AutographCommand;
import net.fabricmc.api.DedicatedServerModInitializer;

public class AutographServer implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        new AutographCommand().register();
    }
}
