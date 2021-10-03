package me.hollow.sputnik.client.managers;

import me.hollow.sputnik.Main;
import me.hollow.sputnik.api.util.Timer;
import me.hollow.sputnik.client.events.PacketEvent;
import me.hollow.sputnik.client.modules.Module;
import me.hollow.sputnik.client.modules.combat.AutoCrystal;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import tcb.bces.listener.IListener;
import tcb.bces.listener.Subscribe;

public class EventManager implements IListener {

    public void init() {
        MinecraftForge.EVENT_BUS.register(this);
        Main.INSTANCE.getBus().register(this);
        size = Main.INSTANCE.getModuleManager().getModules().size();
    }

    private int size = -1;
    private final Timer timer = new Timer();

    @SubscribeEvent
    public void onUpdate(final LivingEvent.LivingUpdateEvent event) {
        if (event.getEntityLiving() == Minecraft.getMinecraft().player) {
            for (int i = 0; i < size; i++) {
                final Module module = Main.INSTANCE.getModuleManager().getModules().get(i);
                if (module.isEnabled()) {
                    module.onUpdate();
                }
            }
            Main.INSTANCE.getSafeManager().update();
            Main.INSTANCE.getPopManager().update();
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onTickHighest(TickEvent.ClientTickEvent event) {
        if (AutoCrystal.INSTANCE.isEnabled())
            AutoCrystal.INSTANCE.onTick();
    }

    @Subscribe
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketHeldItemChange) {
            timer.reset();
        }
    }

    public boolean switchTimerPassed(long time) {
        return timer.hasReached(time);
    }

    @Override
    public boolean isListening() {
        return true;
    }
}
