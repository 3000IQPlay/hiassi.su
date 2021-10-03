package me.hollow.sputnik.client.modules.combat;

import me.hollow.sputnik.api.property.Setting;
import me.hollow.sputnik.client.events.PacketEvent;
import me.hollow.sputnik.client.modules.Module;
import me.hollow.sputnik.client.modules.ModuleManifest;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import tcb.bces.listener.Subscribe;

@ModuleManifest(label = "Criticals", category = Module.Category.COMBAT, color = 0xff400070)
public class Criticals extends Module {

    private final Setting<Boolean> smallPacket = register(new Setting<>("small packet", false));
    private final Setting<Integer> packets = register(new Setting<>("Packets", 2, 1, 5, v -> !smallPacket.getValue()));

    private final double[] packetArray = {
            0.11,
            0.11,
            0.1100013579,
            0.1100013579,
            0.1100013579,
            0.1100013579
    };

    @Subscribe
    public void onPacket(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketUseEntity) {
            if (!mc.player.onGround)
                return;

            final CPacketUseEntity packet = (CPacketUseEntity) event.getPacket();
            if (packet.getAction() == CPacketUseEntity.Action.ATTACK && packet.getEntityFromWorld(mc.world) instanceof EntityLivingBase) {
                if (smallPacket.getValue()) {
                    mc.getConnection().sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.01D, mc.player.posZ, false));
                } else {
                    for (int i = 0; i < packets.getValue(); i++)
                        mc.getConnection().sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + packetArray[i], mc.player.posZ, false));
                }

                mc.getConnection().sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
            }
        }
    }
}
