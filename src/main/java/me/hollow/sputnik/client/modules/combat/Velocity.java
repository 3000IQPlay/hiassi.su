package me.hollow.sputnik.client.modules.combat;

import me.hollow.sputnik.client.events.PacketEvent;
import me.hollow.sputnik.client.modules.Module;
import me.hollow.sputnik.client.modules.ModuleManifest;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;
import tcb.bces.listener.Subscribe;

@ModuleManifest(label = "Velocity", category = Module.Category.MOVEMENT, color = 0xFF0030FF)
public class Velocity extends Module {

    @Override
    public void onEnable() {
        setSuffix("H0.0%:V0.0%");
    }

    @Subscribe
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketEntityVelocity && ((SPacketEntityVelocity) event.getPacket()).getEntityID() == mc.player.getEntityId() || event.getPacket() instanceof SPacketExplosion) {
            event.setCancelled();
        }
    }

}
