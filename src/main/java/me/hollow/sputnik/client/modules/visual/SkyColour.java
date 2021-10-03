package me.hollow.sputnik.client.modules.visual;

import me.hollow.sputnik.api.property.Setting;
import me.hollow.sputnik.client.modules.Module;
import me.hollow.sputnik.client.modules.ModuleManifest;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleManifest(label = "SkyColour", listen = false, category = Module.Category.VISUAL, color = 0x006666)
public class SkyColour extends Module {

    private final Setting<Float> red = register(new Setting<>("Red", 255F, 0F, 255F));
    private final Setting<Float> green = register(new Setting<>("Green", 255F, 0F, 255F));
    private final Setting<Float> blue = register(new Setting<>("Blue", 255F, 0F, 255F));

    @SubscribeEvent
    public void setFogColors(EntityViewRenderEvent.RenderFogEvent.FogColors event) {
        event.setRed(red.getValue() / 255);
        event.setGreen(green.getValue() / 255);
        event.setBlue(blue.getValue() / 255);
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
