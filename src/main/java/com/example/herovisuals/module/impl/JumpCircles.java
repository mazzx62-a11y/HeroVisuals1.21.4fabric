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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** Expanding glowing ring on the ground when you jump (and optionally land). */
public class JumpCircles extends Module {
    private static final int SEGMENTS = 40;
    private static final int MAX_AGE = 26;

    private static final class Circle {
        final double x, y, z;
        int age;
        Circle(double x, double y, double z) { this.x = x; this.y = y; this.z = z; }
    }

    private final ModeSetting color = add(new ModeSetting("Color", Theme.COLOR_MODES));
    private final NumberSetting size = add(new NumberSetting("Size", 1.0, 0.5, 2.5, 0.1));
    private final NumberSetting opacity = add(new NumberSetting("Opacity", 0.75, 0.1, 1.0, 0.05));
    private final BoolSetting onLand = add(new BoolSetting("On landing", true));

    private final List<Circle> circles = new ArrayList<>();
    private boolean wasOnGround = true;

    public JumpCircles() {
        super("Jump Circles", "Glowing ring when you jump", Category.VISUALS);
    }

    @Override
    public void onDisable() {
        circles.clear();
    }

    @Override
    public void onTick(MinecraftClient mc) {
        PlayerEntity p = mc.player;
        if (p == null) return;
        boolean g = p.isOnGround();
        if (wasOnGround && !g && p.getVelocity().y > 0.1) {
            circles.add(new Circle(p.getX(), p.getY(), p.getZ()));
        } else if (onLand.get() && !wasOnGround && g) {
            circles.add(new Circle(p.getX(), p.getY(), p.getZ()));
        }
        wasOnGround = g;
        Iterator<Circle> it = circles.iterator();
        while (it.hasNext()) {
            Circle c = it.next();
            if (++c.age >= MAX_AGE) it.remove();
        }
    }

    @Override
    public void onWorldRender(WorldRenderContext ctx, MinecraftClient mc, float td) {
        Matrix4f m = WorldDraw.matrix(ctx);
        if (m == null || circles.isEmpty()) return;
        Vec3d cam = ctx.camera().getPos();

        WorldDraw.setup();
        for (Circle c : circles) {
            float p = Math.min(1f, (c.age + td) / MAX_AGE);
            float outer = (0.3f + p * 1.9f) * size.getFloat();
            float inner = Math.max(0f, outer - 0.14f * size.getFloat());
            int a = (int) (255 * opacity.get() * (1f - p));
            if (a < 3) continue;

            BufferBuilder b = WorldDraw.begin(VertexFormat.DrawMode.TRIANGLE_STRIP);
            for (int i = 0; i <= SEGMENTS; i++) {
                float ang = (float) (i * 2 * Math.PI / SEGMENTS);
                float cs = MathHelper.cos(ang), sn = MathHelper.sin(ang);
                int rgb = Theme.byMode(color.index(), (float) i / SEGMENTS);
                WorldDraw.vertex(b, m, cam, c.x + cs * outer, c.y + 0.02, c.z + sn * outer, rgb, a);
                WorldDraw.vertex(b, m, cam, c.x + cs * inner, c.y + 0.02, c.z + sn * inner, rgb, a / 3);
            }
            WorldDraw.end(b);
        }
        WorldDraw.finish();
    }
}
