package com.example.herovisuals.gui;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

/** Small drawing helpers: rounded rects, shimmer fills, glow buttons, gradient text. */
public final class Draw {
    private Draw() {}

    public static void rect(DrawContext c, int x, int y, int w, int h, int argb) {
        c.fill(x + 2, y, x + w - 2, y + h, argb);
        c.fill(x, y + 2, x + 2, y + h - 2, argb);
        c.fill(x + w - 2, y + 2, x + w, y + h - 2, argb);
        c.fill(x + 1, y + 1, x + 2, y + 2, argb);
        c.fill(x + w - 2, y + 1, x + w - 1, y + 2, argb);
        c.fill(x + 1, y + h - 2, x + 2, y + h - 1, argb);
        c.fill(x + w - 2, y + h - 2, x + w - 1, y + h - 1, argb);
    }

    public static void outline(DrawContext c, int x, int y, int w, int h, int argb) {
        c.fill(x + 2, y, x + w - 2, y + 1, argb);
        c.fill(x + 2, y + h - 1, x + w - 2, y + h, argb);
        c.fill(x, y + 2, x + 1, y + h - 2, argb);
        c.fill(x + w - 1, y + 2, x + w, y + h - 2, argb);
    }

    /** Animated white-violet shimmering fill made of small colored cells. */
    public static void shimmer(DrawContext c, int x, int y, int w, int h, int cell, int alpha, float t, float phase) {
        float tt = t + phase;
        for (int yy = 0; yy < h; yy += cell) {
            int ch = Math.min(cell, h - yy);
            for (int xx = 0; xx < w; xx += cell) {
                int cw = Math.min(cell, w - xx);
                c.fill(x + xx, y + yy, x + xx + cw, y + yy + ch,
                        Theme.argb(alpha, Theme.shimmer(x + xx, y + yy, tt)));
            }
        }
    }

    /**
     * Button / tab with hover glow and a light "shimmer" sweep running across it.
     * hover, active are 0..1 animation values.
     */
    public static void glowButton(DrawContext c, int x, int y, int w, int h,
                                  float hover, float active, float t, float phase) {
        if (hover > 0.03f) {
            for (int i = 4; i >= 1; i--) {
                rect(c, x - i, y - i, w + 2 * i, h + 2 * i,
                        Theme.argb((int) (hover * (34 - i * 6)), Theme.MID));
            }
        }
        int base = Theme.mix(0x24104A, 0x4B2A96, hover);
        base = Theme.mix(base, 0x7C3AED, active);
        rect(c, x, y, w, h, Theme.argb(0xD0, base));
        c.fill(x + 2, y + 1, x + w - 2, y + 2, Theme.argb((int) (40 + 60 * hover), Theme.WHITE));

        if (hover > 0.05f) {
            float cycle = ((t * 0.7f + phase) % 1.6f) - 0.3f;
            float bw = Math.max(14f, w * 0.2f);
            float cx = x + cycle * w;
            for (int r = 1; r < h - 1; r += 2) {
                float c0 = cx + r * 0.6f; // slanted streak
                int from = (int) Math.max(x + 1, c0 - bw);
                int to = (int) Math.min(x + w - 1, c0 + bw);
                for (int px = from; px < to; px++) {
                    float d = 1f - Math.abs(px - c0) / bw;
                    int al = (int) (d * d * 120 * hover);
                    if (al > 2) {
                        c.fill(px, y + r, px + 1, Math.min(y + r + 2, y + h - 1), Theme.argb(al, 0xF3EEFF));
                    }
                }
            }
        }
        outline(c, x, y, w, h, Theme.argb((int) (0x50 + 0xAF * Math.max(hover, active * 0.6f)), Theme.LILAC));
    }

    public static void text(DrawContext c, TextRenderer tr, String s, int x, int y, float scale, int rgb, boolean shadow) {
        c.getMatrices().push();
        c.getMatrices().translate(x, y, 0);
        c.getMatrices().scale(scale, scale, 1f);
        c.drawText(tr, s, 0, 0, 0xFF000000 | rgb, shadow);
        c.getMatrices().pop();
    }

    /** Text whose letters flow between lilac and white. */
    public static void gradientText(DrawContext c, TextRenderer tr, String s, int x, int y, float scale, float t) {
        c.getMatrices().push();
        c.getMatrices().translate(x, y, 0);
        c.getMatrices().scale(scale, scale, 1f);
        int cx = 0;
        for (int i = 0; i < s.length(); i++) {
            String ch = String.valueOf(s.charAt(i));
            float k = 0.5f + 0.5f * (float) Math.sin(t * 2.2f - i * 0.45f);
            int col = Theme.mix(Theme.LILAC, Theme.WHITE, k);
            c.drawText(tr, ch, cx, 0, 0xFF000000 | col, true);
            cx += tr.getWidth(ch);
        }
        c.getMatrices().pop();
    }

    /** Toggle switch (anim 0..1). */
    public static void pill(DrawContext c, int x, int y, float anim) {
        if (anim > 0.05f) {
            rect(c, x - 2, y - 2, 30, 16, Theme.argb((int) (60 * anim), Theme.MID));
        }
        rect(c, x, y, 26, 12, Theme.argb(0xFF, Theme.mix(0x3A2370, 0xA78BFA, anim)));
        rect(c, x + 2 + (int) (14 * anim), y + 2, 8, 8, Theme.argb(0xFF, Theme.WHITE));
    }
}
