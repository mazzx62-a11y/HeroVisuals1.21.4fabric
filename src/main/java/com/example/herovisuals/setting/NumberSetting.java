package com.example.herovisuals.setting;

import com.google.gson.JsonObject;

public class NumberSetting extends Setting {
    public final double min, max, step;
    private double value;

    public NumberSetting(String name, double def, double min, double max, double step) {
        super(name);
        this.min = min;
        this.max = max;
        this.step = step;
        set(def);
    }

    public double get() { return value; }
    public float getFloat() { return (float) value; }
    public int getInt() { return (int) Math.round(value); }

    public void set(double v) {
        v = Math.max(min, Math.min(max, v));
        if (step > 0) v = Math.round(v / step) * step;
        value = Math.max(min, Math.min(max, v));
    }

    public float fraction() { return (float) ((value - min) / (max - min)); }
    public void setFraction(float f) { set(min + (max - min) * Math.max(0f, Math.min(1f, f))); }

    @Override public void write(JsonObject o) { o.addProperty(name, value); }
    @Override public void read(JsonObject o) { if (o.has(name)) set(o.get(name).getAsDouble()); }
}
