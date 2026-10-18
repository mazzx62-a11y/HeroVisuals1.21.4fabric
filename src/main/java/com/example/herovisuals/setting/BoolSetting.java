package com.example.herovisuals.setting;

import com.google.gson.JsonObject;

public class BoolSetting extends Setting {
    private boolean value;

    public BoolSetting(String name, boolean def) {
        super(name);
        this.value = def;
        this.anim = def ? 1f : 0f;
    }

    public boolean get() { return value; }
    public void set(boolean v) { value = v; }
    public void toggle() { value = !value; }

    @Override public void write(JsonObject o) { o.addProperty(name, value); }
    @Override public void read(JsonObject o) { if (o.has(name)) { value = o.get(name).getAsBoolean(); anim = value ? 1f : 0f; } }
}
