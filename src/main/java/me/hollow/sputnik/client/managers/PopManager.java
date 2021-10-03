package me.hollow.sputnik.client.managers;

import me.hollow.sputnik.Main;
import me.hollow.sputnik.api.interfaces.Minecraftable;
import me.hollow.sputnik.api.util.MessageUtil;
import me.hollow.sputnik.client.events.PacketEvent;
import me.hollow.sputnik.client.modules.client.PopCounter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketEntityStatus;
import tcb.bces.listener.IListener;
import tcb.bces.listener.Subscribe;

import java.util.HashMap;
import java.util.Map;

public class PopManager implements Minecraftable, IListener {

    private final Map<String, Integer> popMap = new HashMap<>();

    public void update() {
        for (EntityPlayer player : mc.world.playerEntities) {
            if (player.getHealth() <= 0) {
                if (popMap.containsKey(player.getName())) {
                    if (PopCounter.getInstance().isEnabled()) {
                        MessageUtil.sendClientMessage(player.getName() + " died after popping their " + popMap.get(player.getName()) + getNumberStringThing(popMap.get(player.getName())) + " totem.", player.getEntityId());
                    }
                    popMap.remove(player.getName(), popMap.get(player.getName()));
                }
            }
        }
    }

    public String getNumberStringThing(int number) {
        if (number > 3) {
            return "th";
        }
        switch (number) {
            case 2:
                return "nd";
            case 3:
                return "rd";
        }
        return "";
    }

    @Subscribe
    public void onPacket(PacketEvent.Receive event) {
        if (mc.player == null || mc.world == null) {
            return;
        }
        if (event.getPacket() instanceof SPacketEntityStatus) {
            final SPacketEntityStatus packet = (SPacketEntityStatus) event.getPacket();
            if (packet.getOpCode() == 35) {
                Entity entity = packet.getEntity(mc.world);

                if (popMap.get(entity.getName()) == null) {
                    popMap.put(entity.getName(), 1);
                    if (PopCounter.getInstance().isEnabled()) {
                        MessageUtil.sendClientMessage(entity.getName() + " popped " + "a totem.", entity.getEntityId());
                    }
                } else if (popMap.get(entity.getName()) != null) {
                    final int popCounter = popMap.get(entity.getName());
                    final int newPopCounter = popCounter + 1;
                    popMap.put(entity.getName(), newPopCounter);
                    if (PopCounter.getInstance().isEnabled()) {
                        MessageUtil.sendClientMessage(entity.getName() + " popped their " + newPopCounter + getNumberStringThing(newPopCounter) + " totem.", entity.getEntityId());
                    }
                }
            }
        }
    }

    public final Map<String, Integer> getPopMap() {
        return this.popMap;
    }

    public void init() {
        Main.INSTANCE.getBus().register(this);
    }

    @Override
    public boolean isListening() {
        return true;
    }
}
