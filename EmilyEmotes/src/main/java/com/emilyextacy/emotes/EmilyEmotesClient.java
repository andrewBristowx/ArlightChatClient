package com.emilyextacy.emotes;

import com.mojang.brigadier.Command;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class EmilyEmotesClient implements ClientModInitializer {
    private static KeyBinding openPicker;

    @Override
    public void onInitializeClient() {
        openPicker = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.emilyemotes.open",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_G,
                "category.emilyemotes"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openPicker.wasPressed()) {
                open(client);
            }
        });

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                dispatcher.register(literal("emotes").executes(context -> {
                    MinecraftClient client = MinecraftClient.getInstance();
                    client.execute(() -> open(client));
                    return Command.SINGLE_SUCCESS;
                }))
        );
    }

    private static void open(MinecraftClient client) {
        if (client == null) return;
        if (client.currentScreen instanceof EmotePickerScreen) return;
        client.setScreen(new EmotePickerScreen(client.currentScreen));
    }
}
