package com.example.herovisuals.module.impl;

import com.example.herovisuals.gui.Theme;
import com.example.herovisuals.module.Category;
import com.example.herovisuals.module.Module;
import com.example.herovisuals.render.WorldDraw;
import com.example.herovisuals.setting.BoolSetting;
import com.example.herovisuals.setting.ModeSetting;
import com.example.herovisuals.setting.NumberSetting;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

public class ChinaHat extends Module {
    private static final int SEGMENTS = 48;

    private final ModeSetting color = add(new ModeSetting("Color", Theme.COLOR_MODES));
    private final NumberSetting size = add(new NumberSetting("Size", 1.0, 0.5, 2.0, 0.05));
    private final NumberSetting opacity = add(new NumberSetting("Opacity", 0.6, 0.1, 1.0, 0.05));
    private final BoolSetting firstPerson = add(new BoolSetting("First person", false));

    public ChinaHat() {
        super("China Hat", "Cone above your head", Category.VISUALS);
    }

    @Override
    public void onWorldRender(WorldRenderContext ctx, MinecraftClient mc, float td) {
        PlayerEntity p = mc.player;
        Matrix4f m = WorldDraw.matrix(ctx);
        if (p == null || m == null) return;
        if (!firstPerson.get() && mc.options.getPerspective().isFirstPerson()) return;

        double x = MathHelper.lerp(td, p.prevX, p.getX());
        double y = MathHelper.lerp(td, p.prevY, p.getY()) + p.getHeight() - 0.05;
        double z = MathHelper.lerp(td, p.prevZ, p.getZ());
        Vec3d cam = ctx.camera().getPos();
        float r = 0.55f * size.getFloat();
        float h = 0.28f * size.getFloat();
        int a = (int) (255 * opacity.get());

        WorldDraw.setup();
        BufferBuilder b = WorldDraw.begin(VertexFormat.DrawMode.TRIANGLE_FAN);
        WorldDraw.vertex(b, m, cam, x, y + h, z, 0xFFFFFF, Math.min(255, a + 40));
        for (int i = 0; i <= SEGMENTS; i++) {
            float ang = (float) (i * 2 * Math.PI / SEGMENTS);
            int rgb = Theme.byMode(color.index(), (float) i / SEGMENTS);
            WorldDraw.vertex(b, m, cam, x + MathHelper.cos(ang) * r, y, z + MathHelper.sin(ang) * r, rgb, a);
        }
        WorldDraw.end(b);
        WorldDraw.finish();
    }
}
