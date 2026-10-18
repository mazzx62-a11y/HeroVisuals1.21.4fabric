package com.example.herovisuals.setting;

import com.google.gson.JsonObject;

public class ModeSetting extends Setting {
    public final String[] modes;
    private int index;

    public ModeSetting(String name, String... modes) {
        super(name);
        this.modes = modes;
    }

    public int index() { return index; }
    public String get() { return modes[index]; }
    public void next() { index = (index + 1) % modes.length; }

    @Override public void write(JsonObject o) { o.addProperty(name, get()); }
    @Override public void read(JsonObject o) {
        if (!o.has(name)) return;
        String v = o.get(name).getAsString();
        for (int i = 0; i < modes.length; i++) if (modes[i].equals(v)) index = i;
    }
}
