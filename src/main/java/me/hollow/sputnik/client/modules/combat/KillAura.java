package me.hollow.sputnik.client.modules.combat;

import me.hollow.sputnik.client.modules.Module;
import me.hollow.sputnik.client.modules.ModuleManifest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;

import java.util.ArrayList;
import java.util.List;

@ModuleManifest(label = "KillAura", color = 0x22, category = Module.Category.COMBAT)
public class KillAura extends Module {

    @Override
    public void onUpdate() {
        if (mc.world == null || mc.player == null || mc.world.playerEntities.isEmpty()) return;
        if (!(mc.player.getHeldItemMainhand().getItem() instanceof ItemSword)) return;
    }

    List<EntityPlayer> list = new ArrayList<>();

    {
        for (EntityPlayer player : mc.world.playerEntities) {

        }
    }
}

