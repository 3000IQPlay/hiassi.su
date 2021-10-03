package me.hollow.sputnik.client.modules.movement;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.hollow.sputnik.api.util.MessageUtil;
import me.hollow.sputnik.client.modules.Module;
import me.hollow.sputnik.client.modules.ModuleManifest;

@ModuleManifest(label = "Speed", listen = false, category = Module.Category.MOVEMENT, color = 0xAE85DE)
public class Speed extends Module {

    @Override
    public void onEnable() {
        MessageUtil.sendClientMessage(ChatFormatting.RED + "Not done yet", -551);
    }
// I be off the durgs
}
