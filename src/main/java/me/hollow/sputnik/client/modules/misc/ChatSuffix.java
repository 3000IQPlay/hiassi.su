package me.hollow.sputnik.client.modules.misc;

import me.hollow.sputnik.api.mixin.mixins.network.AccessorCPacketChatMessage;
import me.hollow.sputnik.client.events.PacketEvent;
import me.hollow.sputnik.client.modules.Module;
import me.hollow.sputnik.client.modules.ModuleManifest;
import net.minecraft.network.play.client.CPacketChatMessage;
import tcb.bces.listener.Subscribe;

@ModuleManifest(label = "ChatSuffix", category = Module.Category.MISC)
public class ChatSuffix extends Module {

    @Subscribe
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketChatMessage) {
            AccessorCPacketChatMessage packet = (AccessorCPacketChatMessage) event.getPacket();
            packet.setMessage(((CPacketChatMessage) event.getPacket()).getMessage() + " | hiassi.su");
        }
    }

}
