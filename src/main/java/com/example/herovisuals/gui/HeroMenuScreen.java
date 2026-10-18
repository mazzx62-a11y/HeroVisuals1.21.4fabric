package com.example.herovisuals.gui;

import com.example.herovisuals.config.ConfigManager;
import com.example.herovisuals.module.Category;
import com.example.herovisuals.module.Module;
import com.example.herovisuals.module.ModuleManager;
import com.example.herovisuals.setting.BoolSetting;
import com.example.herovisuals.setting.ModeSetting;
import com.example.herovisuals.setting.NumberSetting;
import com.example.herovisuals.setting.Setting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class HeroMenuScreen extends Screen {
    private static final int T_TAB = 0, T_MODULE = 1, T_EXPAND = 2, T_BOOL = 3, T_NUM = 4, T_MODE = 5;

    private record Hit(int x, int y, int w, int h, int type, Object target) {}

    private static Category savedCat = Category.VISUALS;
    private static float savedScroll = 0f;

    private final Category[] cats = Category.values();
    private final float[] tabHover = new float[cats.length];
    private final float[] tabActive = new float[cats.length];
    private final List<Hit> hits = new ArrayList<>();

    private float scroll = savedScroll, scrollTarget = savedScroll;
    private int maxScroll;
    private int x0, y0, W, H;
    private int vx, vy, vw, vh; // content viewport

    private NumberSetting dragNum;
    private int dragX, dragW;

    public HeroMenuScreen() {
        super(Text.literal("Hero Visuals"));
        for (int i = 0; i < cats.length; i++) tabActive[i] = cats[i] == savedCat ? 1f : 0f;
    }

    @Override
    protected void init() {
        W = Math.min(440, width - 16);
        H = Math.min(280, height - 16);
        x0 = (width - W) / 2;
        y0 = (height - H) / 2;
        vx = x0 + 118;
        vy = y0 + 46;
        vw = W - 126;
        vh = H - 56;
    }

    @Override public boolean shouldPause() { return false; }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        // we draw our own background (no vanilla blur / black)
    }

    @Override
    public void removed() {
        savedScroll = scrollTarget;
        ConfigManager.save();
    }

    private static boolean inside(double mx, double my, int x, int y, int w, int h) {
        return mx >= x && mx < x + w && my >= y && my < y + h;
    }

    private static int settingH(Setting s) {
        return s instanceof NumberSetting ? 28 : 22;
    }

    // ------------------------------------------------------------------ render

    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        hits.clear();
        float t = Theme.time();

        // Fullscreen shimmering background
        Draw.shimmer(ctx, 0, 0, width, height, 10, 0xC8, t, 0f);
        ctx.fillGradient(0, 0, width, height / 3, 0x55200A4D, 0x00000000);
        ctx.fillGradient(0, height * 2 / 3, width, height, 0x00000000, 0x55200A4D);

        // Window
        for (int i = 6; i >= 1; i--) {
            Draw.rect(ctx, x0 - i, y0 - i, W + 2 * i, H + 2 * i, Theme.argb(8, 0x1B0A3C));
        }
        Draw.shimmer(ctx, x0, y0, W, H, 5, 0xF0, t, 1.7f);
        Draw.outline(ctx, x0, y0, W, H, Theme.argb(0xAA, Theme.LILAC));

        // Header
        Draw.gradientText(ctx, textRenderer, "HERO VISUALS", x0 + 14, y0 + 11, 1.7f, t);
        String hint = "LMB: on/off   RMB or +: settings";
        Draw.text(ctx, textRenderer, hint, x0 + W - 10 - (int) (textRenderer.getWidth(hint) * 0.7f), y0 + 15, 0.7f, Theme.LIGHT, true);

        // Panels
        Draw.rect(ctx, x0 + 8, y0 + 44, 104, H - 52, Theme.argb(0x55, 0x12062B));
        Draw.rect(ctx, vx, vy - 2, vw, vh + 2, Theme.argb(0x44, 0x12062B));

        // Tabs
        for (int i = 0; i < cats.length; i++) {
            int tx = x0 + 12, ty = y0 + 50 + i * 28, tw = 96, th = 24;
            boolean hov = inside(mx, my, tx, ty, tw, th);
            tabHover[i] += ((hov ? 1f : 0f) - tabHover[i]) * 0.25f;
            tabActive[i] += ((cats[i] == savedCat ? 1f : 0f) - tabActive[i]) * 0.25f;
            Draw.glowButton(ctx, tx, ty, tw, th, tabHover[i], tabActive[i], t, i * 0.31f);
            int tc = Theme.mix(Theme.LILAC, Theme.WHITE, Math.max(tabHover[i], tabActive[i]));
            Draw.text(ctx, textRenderer, cats[i].label, tx + 10, ty + 8, 1f, tc, true);
            hits.add(new Hit(tx, ty, tw, th, T_TAB, cats[i]));
        }

        // Module cards (scrollable)
        scroll += (scrollTarget - scroll) * 0.3f;
        boolean inView = inside(mx, my, vx, vy, vw, vh);
        int cardX = vx + 6, cardW = vw - 18;
        int y = vy + 6 - (int) scroll;
        int idx = 0;

        ctx.enableScissor(vx, vy, vx + vw, vy + vh);
        for (Module m : ModuleManager.byCategory(savedCat)) {
            int hh = 28;
            boolean hov = inView && inside(mx, my, cardX, y, cardW, hh);
            m.guiHover += ((hov ? 1f : 0f) - m.guiHover) * 0.25f;
            m.guiOn += ((m.isEnabled() ? 1f : 0f) - m.guiOn) * 0.25f;
            m.guiExpand += ((m.expanded ? 1f : 0f) - m.guiExpand) * 0.25f;

            Draw.glowButton(ctx, cardX, y, cardW, hh, m.guiHover, m.guiOn * 0.4f, t, idx * 0.2f);
            Draw.text(ctx, textRenderer, m.name, cardX + 10, y + 6, 1f, Theme.WHITE, true);
            Draw.text(ctx, textRenderer, m.description, cardX + 10, y + 18, 0.7f, Theme.LILAC, false);
            Draw.pill(ctx, cardX + cardW - 38, y + 9, m.guiOn);
            hits.add(new Hit(cardX, y, cardW, hh, T_MODULE, m));

            if (!m.settings.isEmpty()) {
                Draw.text(ctx, textRenderer, m.expanded ? "-" : "+", cardX + cardW - 54, y + 10, 1f, Theme.LIGHT, true);
                hits.add(new Hit(cardX + cardW - 60, y, 18, hh, T_EXPAND, m));
            }

            int setH = 8;
            for (Setting s : m.settings) setH += settingH(s);
            int bodyH = m.settings.isEmpty() ? 0 : (int) (m.guiExpand * setH);
            y += hh;
            if (bodyH > 1) {
                Draw.rect(ctx, cardX + 3, y + 2, cardW - 6, bodyH, Theme.argb(0x66, 0x12062B));
                ctx.enableScissor(cardX, y + 2, cardX + cardW, y + 2 + bodyH);
                int sy = y + 6;
                for (Setting s : m.settings) {
                    sy += drawSetting(ctx, s, cardX + 6, sy, cardW - 12, mx, my, t, inView && m.expanded);
                }
                ctx.disableScissor();
                y += bodyH + 4;
            }
            y += 6;
            idx++;
        }
        ctx.disableScissor();

        int total = y + (int) scroll - vy;
        maxScroll = Math.max(0, total - vh);
        scrollTarget = MathHelper.clamp(scrollTarget, 0, maxScroll);

        // Scrollbar
        if (maxScroll > 0) {
            int trackX = vx + vw - 6;
            ctx.fill(trackX, vy, trackX + 3, vy + vh, Theme.argb(0x55, 0x12062B));
            int thumbH = Math.max(16, vh * vh / (vh + maxScroll));
            int thumbY = vy + (int) ((vh - thumbH) * (scroll / maxScroll));
            Draw.rect(ctx, trackX, thumbY, 3, thumbH, Theme.argb(0xDD, Theme.LILAC));
        }

        Draw.gradientText(ctx, textRenderer, "by @scedm", x0 + 14, y0 + H - 12, 0.7f, t);
    }

    private int drawSetting(DrawContext ctx, Setting s, int x, int y, int w, int mx, int my, float t, boolean active) {
        int h = settingH(s);
        if (s instanceof BoolSetting b) {
            b.anim += ((b.get() ? 1f : 0f) - b.anim) * 0.25f;
            Draw.text(ctx, textRenderer, b.name, x + 8, y + 7, 1f, Theme.WHITE, true);
            Draw.pill(ctx, x + w - 34, y + 5, b.anim);
            if (active) hits.add(new Hit(x, y, w, h, T_BOOL, b));
        } else if (s instanceof ModeSetting ms) {
            int bx = x + w - 84, by = y + 3;
            boolean hov = active && inside(mx, my, bx, by, 76, 16);
            ms.anim += ((hov ? 1f : 0f) - ms.anim) * 0.25f;
            Draw.text(ctx, textRenderer, ms.name, x + 8, y + 7, 1f, Theme.WHITE, true);
            Draw.glowButton(ctx, bx, by, 76, 16, ms.anim, 0.2f, t, 0.5f);
            String v = ms.get();
            Draw.text(ctx, textRenderer, v, bx + (76 - textRenderer.getWidth(v)) / 2, by + 4, 1f, Theme.WHITE, true);
            if (active) hits.add(new Hit(bx, by, 76, 16, T_MODE, ms));
        } else if (s instanceof NumberSetting n) {
            Draw.text(ctx, textRenderer, n.name, x + 8, y + 5, 1f, Theme.WHITE, true);
            String vs = n.step >= 1 ? String.valueOf(n.getInt()) : String.format("%.2f", n.get());
            Draw.text(ctx, textRenderer, vs, x + w - 8 - textRenderer.getWidth(vs), y + 5, 1f, Theme.LIGHT, true);
            int tx = x + 8, tw = w - 16, ty = y + h - 8;
            float p = n.fraction();
            ctx.fill(tx, ty, tx + tw, ty + 3, Theme.argb(0xAA, 0x12062B));
            ctx.fill(tx, ty, tx + (int) (tw * p), ty + 3, Theme.argb(0xFF, Theme.MID));
            Draw.rect(ctx, tx + (int) (tw * p) - 3, ty - 2, 7, 7, Theme.argb(0xFF, Theme.WHITE));
            if (active) hits.add(new Hit(x, y, w, h, T_NUM, n));
        }
        return h;
    }

    // ------------------------------------------------------------------ input

    private void updateNum(double mx) {
        if (dragNum == null) return;
        dragNum.setFraction((float) MathHelper.clamp((mx - dragX) / (double) dragW, 0.0, 1.0));
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        boolean inView = inside(mx, my, vx, vy, vw, vh);
        for (int i = hits.size() - 1; i >= 0; i--) {
            Hit h = hits.get(i);
            if (!inside(mx, my, h.x(), h.y(), h.w(), h.h())) continue;
            if (h.type() != T_TAB && !inView) continue;

            switch (h.type()) {
                case T_TAB -> {
                    if (button == 0 && savedCat != h.target()) {
                        savedCat = (Category) h.target();
                        scroll = scrollTarget = 0f;
                    }
                }
                case T_MODULE -> {
                    Module m = (Module) h.target();
                    if (button == 0) m.toggle();
                    else if (button == 1 && !m.settings.isEmpty()) m.expanded = !m.expanded;
                }
                case T_EXPAND -> {
                    Module m = (Module) h.target();
                    if (button == 0) m.expanded = !m.expanded;
                }
                case T_BOOL -> {
                    if (button == 0) ((BoolSetting) h.target()).toggle();
                }
                case T_MODE -> {
                    if (button == 0) ((ModeSetting) h.target()).next();
                }
                case T_NUM -> {
                    if (button == 0) {
                        dragNum = (NumberSetting) h.target();
                        dragX = h.x() + 8;
                        dragW = h.w() - 16;
                        updateNum(mx);
                    }
                }
                default -> {}
            }
            return true;
        }
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean mouseDragged(double mx, double my, int button, double dx, double dy) {
        if (dragNum != null) {
            updateNum(mx);
            return true;
        }
        return super.mouseDragged(mx, my, button, dx, dy);
    }

    @Override
    public boolean mouseReleased(double mx, double my, int button) {
        dragNum = null;
        return super.mouseReleased(mx, my, button);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double horizontal, double vertical) {
        if (inside(mx, my, vx, vy, vw, vh)) {
            scrollTarget = MathHelper.clamp(scrollTarget - (float) vertical * 24f, 0, maxScroll);
            return true;
        }
        return super.mouseScrolled(mx, my, horizontal, vertical);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT) {
            close();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
