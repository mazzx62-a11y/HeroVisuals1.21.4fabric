# Hero Visuals (Fabric 1.21.4) — by @scedm

Visual-only client mod. Menu key: RIGHT SHIFT (changeable in Controls).

## Build the jar
Option A (local): install JDK 21, then in this folder run `gradle build`
(or generate a Gradle wrapper first: `gradle wrapper --gradle-version 8.12`).
Jar: build/libs/herovisuals-1.0.0.jar  (not the -sources one).

Option B (GitHub, no setup): push this folder to a GitHub repo -> Actions tab ->
"build" -> download artifact "herovisuals-jar".

Put the jar into .minecraft/mods together with Fabric API.

## Adding a new module
1. Create a class in module/impl extending Module (name, description, Category).
2. Add settings with add(new BoolSetting/NumberSetting/ModeSetting(...)).
3. Override onTick / onWorldRender / onHudRender.
4. Register it in ModuleManager.init(). It shows up in the menu automatically
   and its settings are saved to config/herovisuals.json.
