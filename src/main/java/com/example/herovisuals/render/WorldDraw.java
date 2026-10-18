package com.example.herovisuals.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

/**
 * All in-world drawing goes through here, so if a Minecraft update changes how
 * the world matrix works, it only needs fixing in this one file.
 */
public final class WorldDraw {
    private WorldDraw() {}

    public static Matrix4f matrix(WorldRenderContext ctx) {
        MatrixStack ms = ctx.matrixStack();
        return ms == null ? null : ms.peek().getPositionMatrix();
    }

    public static void setup() {
        RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.enableDepthTest();
    }

    public static void finish() {
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

    public static BufferBuilder begin(VertexFormat.DrawMode mode) {
        return Tessellator.getInstance().begin(mode, VertexFormats.POSITION_COLOR);
    }

    public static void end(BufferBuilder b) {
        BufferRenderer.drawWithGlobalProgram(b.end());
    }

    /** Add a vertex given in WORLD coordinates (camera offset is applied here). */
    public static void vertex(BufferBuilder b, Matrix4f m, Vec3d cam, double x, double y, double z, int rgb, int alpha) {
        b.vertex(m, (float) (x - cam.x), (float) (y - cam.y), (float) (z - cam.z))
                .color((rgb >> 16) & 255, (rgb >> 8) & 255, rgb & 255, alpha);
    }
}
