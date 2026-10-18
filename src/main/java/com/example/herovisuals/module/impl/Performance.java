package com.example.herovisuals.module.impl;

import com.example.herovisuals.module.Category;
import com.example.herovisuals.module.Module;
import com.example.herovisuals.setting.BoolSetting;
import com.example.herovisuals.setting.NumberSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.ParticlesMode;
import net.minecraft.client.option.SimpleOption;

/** Simple FPS boosts using vanilla options. The original values are restored when disabled. */
public class Performance extends Module {
    private final BoolSetting particles = add(new BoolSetting("Minimal particles", true));
    private final BoolSetting shadows = add(new BoolSetting("No entity shadows", true));
    private final BoolSetting clouds = add(new BoolSetting("No clouds", true));
    private final NumberSetting entityDist = add(new NumberSetting("Entity distance", 0.75, 0.5, 1.0, 0.05));

    private boolean captured;
    private ParticlesMode origParticles;
    private boolean origShadows;
    private CloudRenderMode origClouds;
    private double origDist;

    public Performance() {
        super("Performance", "Vanilla settings tuned for FPS", Category.OPTIMIZATION);
    }

    @Override
    public void onTick(MinecraftClient mc) {
        GameOptions o = mc.options;
        if (o == null) return;
        if (!captured) {
            origParticles = o.getParticles().getValue();
            origShadows = o.getEntityShadows().getValue();
            origClouds = o.getCloudRenderMode().getValue();
            origDist = o.getEntityDistanceScaling().getValue();
            captured = true;
        }
        set(o.getParticles(), particles.get() ? ParticlesMode.MINIMAL : origParticles);
        set(o.getEntityShadows(), shadows.get() ? Boolean.FALSE : origShadows);
        set(o.getCloudRenderMode(), clouds.get() ? CloudRenderMode.OFF : origClouds);
        set(o.getEntityDistanceScaling(), Math.min(origDist, entityDist.get()));
    }

    private static <T> void set(SimpleOption<T> opt, T value) {
        if (!opt.getValue().equals(value)) opt.setValue(value);
    }

    private void restore() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (!captured || mc.options == null) return;
        GameOptions o = mc.options;
        set(o.getParticles(), origParticles);
        set(o.getEntityShadows(), origShadows);
        set(o.getCloudRenderMode(), origClouds);
        set(o.getEntityDistanceScaling(), origDist);
        captured = false;
    }

    @Override
    public void onDisable() {
        restore();
    }

    @Override
    public void onShutdown() {
        restore();
    }
}
