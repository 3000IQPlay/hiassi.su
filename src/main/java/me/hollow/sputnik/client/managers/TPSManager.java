package me.hollow.sputnik.client.managers;

import com.google.common.base.Strings;
import me.hollow.sputnik.Main;
import me.hollow.sputnik.api.interfaces.Minecraftable;
import me.hollow.sputnik.client.events.ConnectionEvent;
import me.hollow.sputnik.client.events.PacketEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import tcb.bces.listener.IListener;
import tcb.bces.listener.Subscribe;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.UUID;

public class TPSManager implements Minecraftable, IListener {

    private float TPS = 20.0f;
    private long lastUpdate = -1L;
    private final float[] tpsCounts = new float[10];
    private final DecimalFormat format = new DecimalFormat("##.00#");

    public TPSManager() {

    }

    public void update() {
        if (mc.world == null || mc.player == null)
            return;
        float tps;
        long currentTime = System.currentTimeMillis();
        if (this.lastUpdate == -1L) {
            this.lastUpdate = currentTime;
            return;
        }
        long timeDiff = currentTime - this.lastUpdate;
        float tickTime = (float)timeDiff / 20.0f;
        if (tickTime == 0.0f) {
            tickTime = 50.0f;
        }
        if ((tps = 1000.0f / tickTime) > 20.0f) {
            tps = 20.0f;
        }
        System.arraycopy(this.tpsCounts, 0, this.tpsCounts, 1, this.tpsCounts.length - 1);
        this.tpsCounts[0] = tps;
        double total = 0.0;
        for (float f : tpsCounts) {
            total += f;
        }
        if ((total /= tpsCounts.length) > 20.0) {
            total = 20.0;
        }
        this.TPS = Float.parseFloat(this.format.format(total));
        this.lastUpdate = currentTime;
    }

    public void init() {
        Main.INSTANCE.getBus().register(this);
    }

    @Subscribe
    public void onPacketReceive(PacketEvent.Receive event) {

        if (event.getPacket() instanceof SPacketTimeUpdate) {
            update();
        }

        if (event.getPacket() instanceof SPacketPlayerListItem) {
            if (mc.world == null || mc.player == null) return;

            final SPacketPlayerListItem packet = (SPacketPlayerListItem) event.getPacket();

            if (!SPacketPlayerListItem.Action.ADD_PLAYER.equals(packet.getAction()) && !SPacketPlayerListItem.Action.REMOVE_PLAYER.equals(packet.getAction())) {
                return;
            }

            for (final SPacketPlayerListItem.AddPlayerData data : packet.getEntries()) {
                if (data != null) {
                    if (!Strings.isNullOrEmpty(data.getProfile().getName()) || data.getProfile().getId() != null) {
                        final UUID id = data.getProfile().getId();
                        switch (packet.getAction()) {
                            case ADD_PLAYER:
                                String name = data.getProfile().getName();
                                Main.INSTANCE.getBus().post(new ConnectionEvent(0, id, name));
                                break;
                            case REMOVE_PLAYER:
                                EntityPlayer entity = mc.world.getPlayerEntityByUUID(id);
                                if (entity != null) {
                                    String logoutName = entity.getName();
                                    Main.INSTANCE.getBus().post(new ConnectionEvent(1, entity, id, logoutName));
                                } else {
                                    Main.INSTANCE.getBus().post( new ConnectionEvent(2, id, null));
                                }
                                break;
                        }
                    }
                }
            }
        }
    }

    public void reset() {
        Arrays.fill(this.tpsCounts, 20.0f);
        this.TPS = 20.0f;
    }

    public final float getTpsFactor() {
        return 20.0f / this.TPS;
    }

    public final float getTPS() {
        return this.TPS;
    }

    public int getPing() {
        if (mc.player == null || mc.world == null || mc.getConnection() == null || mc.getConnection().getPlayerInfo(mc.getConnection().getGameProfile().getId()) == null) {
            return -1;
        }
        return mc.getConnection().getPlayerInfo(mc.getConnection().getGameProfile().getId()).getResponseTime();
    }

    @Override
    public boolean isListening() {
        return true;
    }
}
