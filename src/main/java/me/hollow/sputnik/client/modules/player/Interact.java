package me.hollow.sputnik.client.modules.player;

import me.hollow.sputnik.api.mixin.accessors.IMinecraft;
import me.hollow.sputnik.api.property.Setting;
import me.hollow.sputnik.client.events.UpdateEvent;
import me.hollow.sputnik.client.modules.Module;
import me.hollow.sputnik.client.modules.ModuleManifest;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import tcb.bces.listener.Subscribe;

@ModuleManifest(label = "FastPlace", category = Module.Category.PLAYER, color = 0xA5AEAD)
public class Interact extends Module {

    private final Setting<Boolean> xp = register(new Setting<>("XP", true));
    private final Setting<Boolean> blocks = register(new Setting<>("Blocks", true));

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        if (mc.player.getHeldItemMainhand().getItem() == Items.EXPERIENCE_BOTTLE && xp.getValue() || mc.player.getHeldItemMainhand().getItem() instanceof ItemBlock && blocks.getValue()) {
            ((IMinecraft) mc).setRightClickDelayTimer(0);
        }
    }

}
