package me.hollow.sputnik.client.modules.client;

import me.hollow.sputnik.client.modules.Module;
import me.hollow.sputnik.client.modules.ModuleManifest;
import me.hollow.sputnik.RPC;

@ModuleManifest(label = "DiscordRPC", category = Module.Category.CLIENT)
public class DiscordRpc extends Module {

    public void onEnable(){RPC.startRPC();}

    public void onDisable(){RPC.stopRPC();}
}
