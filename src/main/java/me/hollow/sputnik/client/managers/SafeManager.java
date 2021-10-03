package me.hollow.sputnik.client.managers;

import me.hollow.sputnik.api.interfaces.Minecraftable;
import me.hollow.sputnik.api.util.EntityUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;

public class SafeManager implements Minecraftable {

    private boolean safe;

    public SafeManager(){}

    public void update() {
        safe = true;
        float maxDamage = 0.5F;
        final int size = mc.world.loadedEntityList.size();
        for (int i = 0; i < size; ++i) {
            final Entity entity = mc.world.loadedEntityList.get(i);
            if (entity instanceof EntityEnderCrystal) {
                if (mc.player.getDistanceSq(entity) > 100) continue;

                final float damage = EntityUtil.calculate(entity.posX, entity.posY, entity.posZ, mc.player);

                if (damage < maxDamage)
                    continue;
                maxDamage = damage;

                if (damage + 2 < EntityUtil.getHealth(mc.player)) {
                    continue;
                }
                safe = false;
            }
        }
    }

    public boolean isSafe() {
        return safe;
    }

}
