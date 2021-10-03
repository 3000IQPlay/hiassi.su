package me.hollow.sputnik.client.modules.client;

import me.hollow.sputnik.api.property.Setting;
import me.hollow.sputnik.client.events.ClientEvent;
import me.hollow.sputnik.client.modules.Module;
import me.hollow.sputnik.client.modules.ModuleManifest;
import tcb.bces.listener.Subscribe;

import java.awt.*;

@ModuleManifest(label = "Colours", category = Module.Category.CLIENT, persistent = true)
public class Colours extends Module {

    private final Setting<Integer> red = register(new Setting<>("Red", 255, 0, 255));
    private final Setting<Integer> green = register(new Setting<>("Green", 255, 0, 255));
    private final Setting<Integer> blue = register(new Setting<>("Blue", 255, 0, 255));

    public static Colours INSTANCE;

    private int color;

    public Colours() {
        INSTANCE = this;
    }

    @Subscribe
    public void onSetting(ClientEvent event) {
        color = new Color(red.getValue(), green.getValue(), blue.getValue()).getRGB();
    }

    @Override
    public void onLoad() {
        color = new Color(red.getValue(), green.getValue(), blue.getValue()).getRGB();
    }

    //caching mmmm
    public final int getColor() {
        return color;
    }

}
