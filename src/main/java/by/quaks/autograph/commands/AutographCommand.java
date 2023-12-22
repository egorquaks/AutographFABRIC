package by.quaks.autograph.commands;


import by.quaks.autograph.config.ConfigManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import static by.quaks.autograph.Autograph.adventure;
import static by.quaks.autograph.Autograph.configReader;

public class AutographCommand {
    public void register() {
        System.out.println("Registration Autograph");
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            register(dispatcher);
        });
    }

    private void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("autograph")
                .executes(this::run));
    }

    private int run(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        final ServerCommandSource source = ctx.getSource();
        final PlayerEntity player = source.getPlayer();
        if (player != null) {
            ItemStack itemStack = player.getMainHandStack();
            if (!itemStack.isEmpty()) {
                //adventure().player(player.getUuid()).sendMessage(MiniMessage.miniMessage().deserialize(configReader.getString("autograph").replaceAll("\\{player-name\\}", player.getName().getString())));
                if(itemStack.isDamageable()){
                    addLore(ctx);
                }else{
                    adventure().player(player.getUuid()).sendMessage(MiniMessage.miniMessage().deserialize(configReader.getString("nonValuableItem")));
                }
            }else{
                adventure().player(player.getUuid()).sendMessage(MiniMessage.miniMessage().deserialize(configReader.getString("emptyHandMessage")));
            }
        }
        return 1;
    }

    public static void addLore(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        if (context.getSource().getEntity().isPlayer()) {
            if (context.getSource().getPlayer().getMainHandStack().isEmpty()) return;
            NbtCompound itemNbt = context.getSource().getPlayer().getMainHandStack().getOrCreateSubNbt("display");
            NbtList lore = new NbtList();
            ConfigManager configReader = new ConfigManager();
            if (itemNbt.contains("Lore")) {
                lore = itemNbt.getList("Lore", NbtElement.STRING_TYPE);
            }else{
                String autographSetting = configReader.getString("autograph");
                Component autographComp = MiniMessage.miniMessage().deserialize(autographSetting.replaceAll("\\{player-name\\}", context.getSource().getPlayer().getName().getString()));
                String autographJson = JSONComponentSerializer.json().serialize(autographComp).replaceFirst("\\{", "{\"italic\":false,");
                lore.add(NbtString.of(autographJson));
                itemNbt.put("Lore", lore);
                return;
            }
            String loreJson = lore.asString();
            List<String> loreList = new ArrayList<>();
            JSONArray jsonArray = new JSONArray(loreJson);
            for (int i1 = 0; i1 < jsonArray.length(); i1++){
                String json = jsonArray.getString(i1);
                Component component = JSONComponentSerializer.json().deserialize(json);
                String plainLoreEntry = PlainTextComponentSerializer.plainText().serialize(component);
                loreList.add(plainLoreEntry);
            }
            String autographSetting = configReader.getString("autograph");
            Component autographComp = MiniMessage.miniMessage().deserialize(autographSetting.replaceAll("\\{player-name\\}", context.getSource().getPlayer().getName().getString()));
            String plainAutograph = PlainTextComponentSerializer.plainText().serialize(autographComp);
            if(!loreList.contains(plainAutograph)){
                String autographJson = JSONComponentSerializer.json().serialize(autographComp).replaceFirst("\\{", "{\"italic\":false,");
                lore.add(NbtString.of(autographJson));
                itemNbt.put("Lore", lore);
            }else{
                adventure().player(context.getSource().getPlayer().getUuid()).sendMessage(MiniMessage.miniMessage().deserialize(configReader.getString("itemContainsMaxAutographsBy")));
            }
        }
    }

}
