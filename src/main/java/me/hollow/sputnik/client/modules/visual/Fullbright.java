package me.hollow.sputnik.client.modules.visual;

import me.hollow.sputnik.client.modules.Module;
import me.hollow.sputnik.client.modules.ModuleManifest;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Objects;

@ModuleManifest(label = "Fullbright", listen = false, category = Module.Category.VISUAL, color = 0xff00ff)
public final class Fullbright extends Module {

    private boolean hasEffect = false;
    private final PotionEffect effect = new PotionEffect(Objects.requireNonNull(Potion.getPotionById(16)));


    @Override
    public void onEnable() {
        if (mc.player == null)
            return;

        mc.player.addPotionEffect(effect);
        hasEffect = true;
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (mc.player == null)
            return;

        if (!hasEffect) {
            mc.player.addPotionEffect(effect);
            hasEffect = true;
        }
    }

    @Override
    public void onDisable() {
        mc.player.removeActivePotionEffect(effect.getPotion());
        hasEffect = false;
    }

}