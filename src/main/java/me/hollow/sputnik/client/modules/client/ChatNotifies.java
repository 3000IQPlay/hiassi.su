package me.hollow.sputnik.client.modules.client;

import me.hollow.sputnik.client.modules.Module;
import me.hollow.sputnik.client.modules.ModuleManifest;

//fart


@ModuleManifest(label = "ChatNotify", category = Module.Category.CLIENT, persistent = true)
public class ChatNotifies extends Module {

    private static ChatNotifies INSTANCE;

    public ChatNotifies() {
        INSTANCE = this;
    }

    public static ChatNotifies getInstance() {
        return INSTANCE;
    }
}
