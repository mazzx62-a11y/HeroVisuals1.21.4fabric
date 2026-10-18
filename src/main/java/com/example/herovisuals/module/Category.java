package com.example.herovisuals.module;

public enum Category {
    VISUALS("Visuals"),
    HUD("HUD"),
    OPTIMIZATION("Optimization");

    public final String label;

    Category(String label) { this.label = label; }
}
