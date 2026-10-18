package com.example.herovisuals;

import com.example.herovisuals.config.ConfigManager;
import com.example.herovisuals.gui.Draw;
import com.example.herovisuals.gui.HeroMenuScreen;
import com.example.herovisuals.gui.Theme;
import com.example.herovisuals.module.Module;
import com.example.herovisuals.module.ModuleManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class HeroVisualsMod implements ClientModInitializer {
    private static KeyBinding openKey;

    @Override
    public void onInitializeClient() {
        ModuleManager.init();
        ConfigManager.load();

        openKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.herovisuals.open", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, "key.categories.misc"));

        // Tick: menu key + module ticks
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openKey.wasPressed()) {
                if (client.currentScreen == null && client.player != null) {
                    client.setScreen(new HeroMenuScreen());
                }
            }
            for (Module m : ModuleManager.all()) {
                if (m.isEnabled()) m.onTick(client);
            }
        });

        // In-world rendering
        WorldRenderEvents.AFTER_ENTITIES.register(ctx -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player == null) return;
            float td = mc.getRenderTickCounter().getTickDelta(true);
            for (Module m : ModuleManager.all()) {
                if (m.isEnabled()) m.onWorldRender(ctx, mc, td);
            }
        });

        // HUD rendering
        HudRenderCallback.EVENT.register((ctx, tickCounter) -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player == null || mc.options.hudHidden) return;
            float td = tickCounter.getTickDelta(true);
            for (Module m : ModuleManager.all()) {
                if (m.isEnabled()) m.onHudRender(ctx, mc, td);
            }
        });

        // Restore vanilla options + save config on exit
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
            for (Module m : ModuleManager.all()) m.onShutdown();
            ConfigManager.save();
        });

        // Small shimmering credit in the corner of the ESC (pause) menu
        ScreenEvents.AFTER_INIT.register((client, screen, w, h) -> {
            if (screen instanceof GameMenuScreen) {
                ScreenEvents.afterRender(screen).register((s, ctx, mx, my, td) -> {
                    String text = "Hero Visuals by @scedm";
                    float sc = 0.7f;
                    TextRenderer tr = client.textRenderer;
                    int tw = (int) (tr.getWidth(text) * sc);
                    Draw.gradientText(ctx, tr, text, s.width - tw - 5, s.height - (int) (9 * sc) - 4, sc, Theme.time());
                });
            }
        });
    }
}
