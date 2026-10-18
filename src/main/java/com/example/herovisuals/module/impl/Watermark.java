package com.example.herovisuals.module.impl;

import com.example.herovisuals.gui.Draw;
import com.example.herovisuals.gui.Theme;
import com.example.herovisuals.module.Category;
import com.example.herovisuals.module.Module;
import com.example.herovisuals.setting.BoolSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class Watermark extends Module {
    private final BoolSetting fps = add(new BoolSetting("Show FPS", true));
    private final BoolSetting coords = add(new BoolSetting("Show coordinates", false));

    public Watermark() {
        super("Watermark", "Client name + info in the corner", Category.HUD);
        setEnabled(true);
    }

    @Override
    public void onHudRender(DrawContext ctx, MinecraftClient mc, float td) {
        StringBuilder sb = new StringBuilder("Hero Visuals");
        if (fps.get()) sb.append("  |  ").append(mc.getCurrentFps()).append(" fps");
        if (coords.get() && mc.player != null) {
            sb.append("  |  ").append((int) mc.player.getX()).append(' ')
                    .append((int) mc.player.getY()).append(' ')
                    .append((int) mc.player.getZ());
        }
        String text = sb.toString();
        float t = Theme.time();
        int w = mc.textRenderer.getWidth(text) + 12;
        Draw.rect(ctx, 4, 4, w, 15, Theme.argb(0xB0, 0x1B0A3C));
        for (int x = 0; x < w - 4; x += 3) {
            ctx.fill(6 + x, 4, Math.min(6 + x + 3, 4 + w - 2), 5, Theme.argb(0xFF, Theme.shimmer(x * 2f, 0f, t)));
        }
        Draw.gradientText(ctx, mc.textRenderer, text, 10, 8, 1f, t);
    }
}
