package me.hollow.sputnik.client.modules.movement;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.hollow.sputnik.api.util.MessageUtil;
import me.hollow.sputnik.client.modules.Module;
import me.hollow.sputnik.client.modules.ModuleManifest;

@ModuleManifest(label = "ElytraFlight", listen = false, category = Module.Category.MOVEMENT)
public class ElytraFlight extends Module {
    @Override
    public void onEnable() {
        MessageUtil.sendClientMessage(ChatFormatting.RED + "Not done yet", -551);
    }
}
