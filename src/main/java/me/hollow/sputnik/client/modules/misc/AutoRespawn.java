package me.hollow.sputnik.client.modules.misc;

import me.hollow.sputnik.api.property.Setting;
import me.hollow.sputnik.api.util.MessageUtil;
import me.hollow.sputnik.client.modules.Module;
import me.hollow.sputnik.client.modules.ModuleManifest;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

@ModuleManifest(label = "AutoRespawn", listen = false, category = Module.Category.MISC, color = 0xfff33f00)
public class AutoRespawn extends Module {

    private final Setting<Boolean> copyToClipboard = register(new Setting<>("Clipboard", true));

    @SubscribeEvent
    public void onGui(GuiOpenEvent event) {
        if (event.getGui() instanceof GuiGameOver) {
            mc.player.respawnPlayer();
            String deathCoords = "XYZ : " + (int) mc.player.posX + " " + (int) mc.player.posY + " " + (int) mc.player.posZ;
            MessageUtil.sendClientMessage(deathCoords, 0);
            if (copyToClipboard.getValue()) {
                try {
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    clipboard.setContents(new StringSelection(deathCoords), null);
                } catch (Exception e) {
                    MessageUtil.sendClientMessage(e.getMessage(), 0);
                }
            }
        }
    }

    @Override
    public void onEnable() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void onDisable() {
        MinecraftForge.EVENT_BUS.unregister(this);
    }
}
