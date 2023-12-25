package by.quaks.autograph.commands;


import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
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
import java.util.regex.Pattern;

import static by.quaks.autograph.client.AutographClient.clientAdventure;
import static by.quaks.autograph.server.AutographServer.serverAdventure;
import static by.quaks.autograph.Autograph.configReader;

public class AutographCommand {

    private static boolean dedicated;

    public void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            register(dispatcher);
            dedicated = environment.dedicated;
        });
    }

    private void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("autograph")
                .executes(this::dispatch));
    }


    private int dispatch(CommandContext<ServerCommandSource> context) {
        final ServerCommandSource source = context.getSource();
        final PlayerEntity player = source.getPlayer();
        if (player != null) {
            ItemStack itemStack = player.getMainHandStack();
            if (!itemStack.isEmpty()) {
                if (itemStack.isDamageable()) {
                    if (!hasAutographBy(itemStack, player)) {
                        addLore(itemStack, genJsonAutograph(player));
                    } else {
                        sendMessage(context.getSource().getPlayer(), "itemContainsMaxAutographsBy");
                    }
                } else {
                    sendMessage(player, "nonValuableItem");
                }
            } else {
                sendMessage(player, "emptyHandMessage");
            }
        }
        return 1;
    }

    private static void sendMessage(PlayerEntity player, String configMessage) {
        if (dedicated) {
            serverAdventure().player(player.getUuid()).sendMessage(MiniMessage.miniMessage().deserialize(configReader.getString(configMessage)));
        } else {
            clientAdventure().audience().sendMessage(MiniMessage.miniMessage().deserialize(configReader.getString(configMessage)));
        }
    }

    public static String genJsonAutograph(PlayerEntity player) {
        String autographSetting = configReader.getString("autograph");
        Component autographComp = MiniMessage.miniMessage().deserialize(autographSetting.replaceAll("\\{player-name\\}", player.getName().getString()));
        return JSONComponentSerializer.json().serialize(autographComp).replaceFirst("\\{", "{\"italic\":false,");
    }

    public static boolean hasAutographBy(ItemStack itemStack, PlayerEntity player) {
        List<String> lore = getPlainList(getLore(itemStack));
        for (String entry : lore) {
            if (entry.toLowerCase().contains(player.getName().getString().toLowerCase())) {
                return true;
            }
        }
        return false;
    }
    public static boolean isAutographed(ItemStack itemStack){
        List<String> lore = getPlainList(getLore(itemStack));
        String autographFormRaw = configReader.getString("general.autograph");
        Component component = MiniMessage.miniMessage().deserialize(autographFormRaw);
        String autographForm = PlainTextComponentSerializer.plainText().serialize(component);
        String autographPattern = autographForm.replaceAll("\\{player-name}","(.*?)");
        Pattern pattern = Pattern.compile(autographPattern);
        return lore.stream().anyMatch(s -> pattern.matcher(s).find());
    }

    public static void addLore(ItemStack itemStack, String json) {
        NbtCompound itemNbt = itemStack.getOrCreateSubNbt("display");
        NbtList lore = new NbtList();
        if (itemNbt.contains("Lore")) {
            lore = itemNbt.getList("Lore", NbtElement.STRING_TYPE);
        }
        lore.add(NbtString.of(json));
        itemNbt.put("Lore", lore);
    }

    private static JSONArray getLore(ItemStack itemStack) {
        NbtCompound itemNbt = itemStack.getOrCreateSubNbt("display");
        NbtList lore = new NbtList();
        if (itemNbt.contains("Lore")) {
            lore = itemNbt.getList("Lore", NbtElement.STRING_TYPE);
        }
        String loreJson = lore.asString();
        return new JSONArray(loreJson);
    }

    private static List<String> getList(JSONArray jsonArray) {
        List<String> loreList = new ArrayList<>();
        for (int i1 = 0; i1 < jsonArray.length(); i1++) {
            loreList.add(jsonArray.getString(i1));
        }
        return loreList;
    }

    private static List<String> getPlainList(JSONArray jsonArray) {
        List<String> loreList = new ArrayList<>();
        JSONComponentSerializer jsonSerializer = JSONComponentSerializer.json();
        PlainTextComponentSerializer plainTextSerializer = PlainTextComponentSerializer.plainText();
        for (int i = 0; i < jsonArray.length(); i++) {
            String json = jsonArray.getString(i);
            Component component = jsonSerializer.deserialize(json);
            String plainLoreEntry = plainTextSerializer.serialize(component);
            loreList.add(plainLoreEntry);
        }
        return loreList;
    }
}
