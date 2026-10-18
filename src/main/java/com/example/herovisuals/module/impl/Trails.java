package com.example.herovisuals.module.impl;

import com.example.herovisuals.gui.Theme;
import com.example.herovisuals.module.Category;
import com.example.herovisuals.module.Module;
import com.example.herovisuals.render.WorldDraw;
import com.example.herovisuals.setting.ModeSetting;
import com.example.herovisuals.setting.NumberSetting;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

/** Glowing ribbon that follows behind you. */
public class Trails extends Module {
    private final ModeSetting color = add(new ModeSetting("Color", Theme.COLOR_MODES));
    private final NumberSetting length = add(new NumberSetting("Length", 40, 10, 120, 1));
    private final NumberSetting height = add(new NumberSetting("Height", 0.6, 0.1, 1.8, 0.05));
    private final NumberSetting opacity = add(new NumberSetting("Opacity", 0.7, 0.1, 1.0, 0.05));

    private final List<Vec3d> points = new ArrayList<>();

    public Trails() {
        super("Trails", "Ribbon behind your player", Category.VISUALS);
    }

    @Override
    public void onDisable() {
        points.clear();
    }

    @Override
    public void onTick(MinecraftClient mc) {
        PlayerEntity p = mc.player;
        if (p == null) {
            points.clear();
            return;
        }
        Vec3d pos = p.getPos();
        if (points.isEmpty() || points.get(points.size() - 1).squaredDistanceTo(pos) > 0.0004) {
            points.add(pos);
        } else if (!points.isEmpty()) {
            points.remove(0); // standing still: trail slowly disappears
        }
        while (points.size() > length.getInt()) points.remove(0);
    }

    @Override
    public void onWorldRender(WorldRenderContext ctx, MinecraftClient mc, float td) {
        Matrix4f m = WorldDraw.matrix(ctx);
        int n = points.size();
        if (m == null || n < 2) return;
        Vec3d cam = ctx.camera().getPos();

        WorldDraw.setup();
        BufferBuilder b = WorldDraw.begin(VertexFormat.DrawMode.TRIANGLE_STRIP);
        for (int i = 0; i < n; i++) {
            Vec3d v = points.get(i);
            float f = (float) i / (n - 1); // 0 = oldest, 1 = newest
            int rgb = Theme.byMode(color.index(), f * 0.6f);
            int a = (int) (255 * opacity.get() * f);
            WorldDraw.vertex(b, m, cam, v.x, v.y + 0.05, v.z, rgb, a);
            WorldDraw.vertex(b, m, cam, v.x, v.y + 0.05 + height.get() * f, v.z, rgb, a / 2);
        }
        WorldDraw.end(b);
        WorldDraw.finish();
    }
}
