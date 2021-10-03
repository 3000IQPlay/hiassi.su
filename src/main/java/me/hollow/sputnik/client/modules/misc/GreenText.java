package me.hollow.sputnik.client.modules.misc;

import me.hollow.sputnik.client.events.PacketSendEvent;
import me.hollow.sputnik.client.modules.Module;
import me.hollow.sputnik.client.modules.ModuleManifest;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;


@ModuleManifest(label = "GreenText", category = Module.Category.MISC, color = 0x00FF00)
public class GreenText extends Module {

    private final String suffix = ">";

    @SubscribeEvent
    public void onPacket(final PacketSendEvent event) {
        if (event.getPacket() instanceof CPacketChatMessage) {
            String s = ((CPacketChatMessage) event.getPacket()).getMessage();
            if (s.startsWith("/") || s.startsWith(">")) {
                return;
            }
            if (s.length() >= 256) {
                s = s.substring(0, 256);
            }

            event.setCanceled(true);
            CPacketChatMessage newpacket = new CPacketChatMessage(suffix + s);
            mc.player.connection.sendPacket(newpacket);

        }
    }
}