package com.example.herovisuals.gui;

/** White-violet palette + animated shimmer color function. */
public final class Theme {
    public static final int DEEP = 0x4C1D95;
    public static final int MID = 0x8B5CF6;
    public static final int LIGHT = 0xE4DAFF;
    public static final int LILAC = 0xC4B5FD;
    public static final int WHITE = 0xFFFFFF;

    private Theme() {}

    public static float time() {
        return (System.currentTimeMillis() % 1_000_000L) / 1000f;
    }

    public static int argb(int a, int rgb) {
        return ((a & 255) << 24) | (rgb & 0xFFFFFF);
    }

    public static int mix(int c1, int c2, float t) {
        t = Math.max(0f, Math.min(1f, t));
        int r = (int) (((c1 >> 16) & 255) * (1 - t) + ((c2 >> 16) & 255) * t);
        int g = (int) (((c1 >> 8) & 255) * (1 - t) + ((c2 >> 8) & 255) * t);
        int b = (int) ((c1 & 255) * (1 - t) + (c2 & 255) * t);
        return (r << 16) | (g << 8) | b;
    }

    /** Flowing violet -> white shimmer, depends on position and time. */
    public static int shimmer(float x, float y, float t) {
        float v = ((float) Math.sin(x * 0.018f + t * 0.9f)
                + (float) Math.sin(y * 0.025f - t * 0.7f)
                + (float) Math.sin((x + y) * 0.012f + t * 0.5f)) / 3f;
        float n = v * 0.5f + 0.5f;
        return n < 0.5f ? mix(DEEP, MID, n * 2f) : mix(MID, LIGHT, (n - 0.5f) * 2f);
    }

    public static int hsv(float h, float sat, float v) {
        h = h - (float) Math.floor(h);
        int i = (int) (h * 6);
        float f = h * 6 - i;
        float p = v * (1 - sat), q = v * (1 - f * sat), t = v * (1 - (1 - f) * sat);
        float r, g, b;
        switch (i % 6) {
            case 0 -> { r = v; g = t; b = p; }
            case 1 -> { r = q; g = v; b = p; }
            case 2 -> { r = p; g = v; b = t; }
            case 3 -> { r = p; g = q; b = v; }
            case 4 -> { r = t; g = p; b = v; }
            default -> { r = v; g = p; b = q; }
        }
        return ((int) (r * 255) << 16) | ((int) (g * 255) << 8) | (int) (b * 255);
    }

    /** Shared color modes for visual modules: Rainbow / Hero / Pink / White. */
    public static final String[] COLOR_MODES = {"Rainbow", "Hero", "Pink", "White"};

    public static int byMode(int mode, float phase) {
        float t = time();
        return switch (mode) {
            case 0 -> hsv(t * 0.25f + phase, 0.75f, 1f);
            case 1 -> mix(MID, LIGHT, 0.5f + 0.5f * (float) Math.sin(t * 2f + phase * 6.2832f));
            case 2 -> mix(0xEC4899, 0xF9A8D4, 0.5f + 0.5f * (float) Math.sin(t * 2f + phase * 6.2832f));
            default -> 0xF5F3FF;
        };
    }
}
