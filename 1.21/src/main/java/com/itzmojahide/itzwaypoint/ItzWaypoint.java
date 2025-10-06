package com.itzmojahide.itzwaypoint;

import com.itzmojahide.itzwaypoint.gui.WaypointScreen;
import com.itzmojahide.itzwaypoint.manager.WaypointManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class ItzWaypoint implements ClientModInitializer {

    public static final String MOD_ID = "itzwaypoint";
    private static KeyBinding openMenuKey;

    @Override
    public void onInitializeClient() {
        WaypointManager.init(); // Initialize the manager on startup

        openMenuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.itzwaypoint.open_menu",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_P,
                "category.itzwaypoint.main"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMenuKey.wasPressed()) {
                client.setScreen(new WaypointScreen(null)); // Pass null for main menu
            }
        });
    }
}
