package com.example.herovisuals.module;

import com.example.herovisuals.setting.Setting;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

public abstract class Module {
    public final String name, description;
    public final Category category;
    public final List<Setting> settings = new ArrayList<>();
    private boolean enabled;

    // GUI state
    public float guiHover, guiOn, guiExpand;
    public boolean expanded;

    protected Module(String name, String description, Category category) {
        this.name = name;
        this.description = description;
        this.category = category;
    }

    protected <T extends Setting> T add(T s) {
        settings.add(s);
        return s;
    }

    public boolean isEnabled() { return enabled; }

    public void setEnabled(boolean v) {
        if (v == enabled) return;
        enabled = v;
        guiOn = v ? 1f : guiOn;
        if (v) onEnable(); else onDisable();
    }

    public void toggle() { setEnabled(!enabled); }

    public void onEnable() {}
    public void onDisable() {}
    public void onShutdown() {}
    public void onTick(MinecraftClient mc) {}
    public void onWorldRender(WorldRenderContext ctx, MinecraftClient mc, float td) {}
    public void onHudRender(DrawContext ctx, MinecraftClient mc, float td) {}
}
