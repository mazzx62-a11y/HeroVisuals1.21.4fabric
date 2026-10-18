package com.example.herovisuals.setting;

import com.google.gson.JsonObject;

public abstract class Setting {
    public final String name;
    /** GUI animation state. */
    public float anim;

    protected Setting(String name) {
        this.name = name;
    }

    public abstract void write(JsonObject o);

    public abstract void read(JsonObject o);
}
