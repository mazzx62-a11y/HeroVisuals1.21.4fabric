package com.example.herovisuals.module;

import com.example.herovisuals.module.impl.*;

import java.util.ArrayList;
import java.util.List;

public final class ModuleManager {
    private static final List<Module> MODULES = new ArrayList<>();

    private ModuleManager() {}

    public static void init() {
        // Visuals
        MODULES.add(new ChinaHat());
        MODULES.add(new JumpCircles());
        MODULES.add(new Trails());
        // HUD
        MODULES.add(new Watermark());
        MODULES.add(new ModuleList());
        // Optimization
        MODULES.add(new Performance());
    }

    public static List<Module> all() { return MODULES; }

    public static List<Module> byCategory(Category c) {
        List<Module> out = new ArrayList<>();
        for (Module m : MODULES) if (m.category == c) out.add(m);
        return out;
    }
}
