package me.hollow.sputnik.client.modules.visual;

import me.hollow.sputnik.client.modules.Module;
import me.hollow.sputnik.client.modules.ModuleManifest;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@ModuleManifest(label = "NoWeather", listen = false, category = Module.Category.VISUAL, color = 0x006600)
public class NoWeather extends Module {

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (mc.world == null)
            return;
        if (mc.world.isRaining()) {
            mc.world.setRainStrength(0);
        }
    }
}