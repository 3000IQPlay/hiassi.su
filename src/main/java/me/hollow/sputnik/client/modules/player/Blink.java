package me.hollow.sputnik.client.modules.player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.mojang.authlib.GameProfile;

import me.hollow.sputnik.api.util.CommandUtil;
import me.hollow.sputnik.client.events.PacketSendEvent;
import me.hollow.sputnik.client.modules.Module;
import me.hollow.sputnik.client.modules.ModuleManifest;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

//pasted from moneymod+2 

@ModuleManifest(label = "Blink", category = Module.Category.PLAYER, color = 0xff0030ff)
public class Blink extends Module {

    private static List<Packet> packets = new ArrayList();
    private double oldX;
    private double oldY;
    private double oldZ;
    private EntityOtherPlayerMP fakePlayer;

    @SubscribeEvent
    public void packetrec(PacketSendEvent event) {
        if (!this.isEnabled())
            return;

        packets.add(event.getPacket());
        event.setCanceled(true);
    }

    @Override
    public void onEnable() {
        if (mc.player != null) {
            this.oldX = mc.player.posX;
            this.oldY = mc.player.posY;
            this.oldZ = mc.player.posZ;
            fakePlayer = new EntityOtherPlayerMP(Minecraft.getMinecraft().world, new GameProfile(
                    UUID.fromString("403e6cb7-a6ca-440a-8041-7fb1e579b5a5"), mc.player.getName() + "fake"));

            fakePlayer.copyLocationAndAnglesFrom(Minecraft.getMinecraft().player);
            fakePlayer.rotationYawHead = Minecraft.getMinecraft().player.rotationYawHead;
            Minecraft.getMinecraft().world.addEntityToWorld(-101, fakePlayer);
        }
        // CommandUtil.sendChatMessage("fakeplayer spawned");
        // super.onEnable();
    }

    @Override
    public void onDisable() {
        if (mc.player != null) {
            // copy.addAll(packets.stream().map(s->s.clone()).collect(Collectors.toList()));
            try {
                for (Packet packet : packets) {
                    mc.player.connection.sendPacket(packet);
                    packets.remove(packet);
                }
            } catch (Exception e) {
                CommandUtil.sendChatMessage("Exception");
                e.printStackTrace();
            }
            // packets.clear();
            Minecraft.getMinecraft().world.removeEntityFromWorld(-101);
            fakePlayer = null;
        }
        // super.onDisable();
    }
}


