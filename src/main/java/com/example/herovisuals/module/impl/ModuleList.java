package com.example.herovisuals.module.impl;

import com.example.herovisuals.gui.Draw;
import com.example.herovisuals.gui.Theme;
import com.example.herovisuals.module.Category;
import com.example.herovisuals.module.Module;
import com.example.herovisuals.module.ModuleManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

public class ModuleList extends Module {
    public ModuleList() {
        super("Module List", "Enabled modules on the right", Category.HUD);
        setEnabled(true);
    }

    @Override
    public void onHudRender(DrawContext ctx, MinecraftClient mc, float td) {
        List<Module> on = new ArrayList<>();
        for (Module m : ModuleManager.all()) {
            if (m.isEnabled() && m.category != Category.HUD) on.add(m);
        }
        on.sort((a, b) -> mc.textRenderer.getWidth(b.name) - mc.textRenderer.getWidth(a.name));

        float t = Theme.time();
        int sw = ctx.getScaledWindowWidth();
        int y = 4;
        for (int i = 0; i < on.size(); i++) {
            String s = on.get(i).name;
            int w = mc.textRenderer.getWidth(s) + 8;
            int x = sw - w - 4;
            Draw.rect(ctx, x, y, w, 12, Theme.argb(0x99, 0x1B0A3C));
            ctx.fill(sw - 4, y, sw - 2, y + 12, Theme.argb(0xFF, Theme.shimmer(0f, i * 40f, t)));
            Draw.gradientText(ctx, mc.textRenderer, s, x + 4, y + 2, 1f, t - i * 0.2f);
            y += 13;
        }
    }
}
