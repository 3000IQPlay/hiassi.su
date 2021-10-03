package me.hollow.sputnik.client.events;

import me.hollow.sputnik.api.property.Setting;
import me.hollow.sputnik.client.modules.Module;
import tcb.bces.event.Event;

public class ClientEvent extends Event {

    private Module module;
    private Setting setting;
    private int stage;

    public ClientEvent(Setting setting) {
        this.setting = setting;
    }

    public ClientEvent(int stage) {
        this.stage = stage;
    }

    public Module getModule() {
        return module;
    }

    public Setting getSetting() {
        return setting;
    }

    public int getStage() {
        return stage;
    }

}
