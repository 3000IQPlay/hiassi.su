package me.hollow.sputnik.client.modules.visual;

import me.hollow.sputnik.api.mixin.mixins.render.AccessorRenderManager;
import me.hollow.sputnik.api.property.Setting;
import me.hollow.sputnik.api.util.render.RenderUtil;
import me.hollow.sputnik.client.modules.Module;
import me.hollow.sputnik.client.modules.ModuleManifest;
import me.hollow.sputnik.client.modules.client.Colours;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

import java.awt.*;

@ModuleManifest(label = "EntityESP", listen = false, category = Module.Category.VISUAL, color = 0xff00ff)
public final class EntityESP extends Module {

    private final Setting<Boolean> bottles = register(new Setting<>("XPBottles", true));
    private final Setting<Boolean> items = register(new Setting<>("Items", true));

    private final Setting<Boolean> sync = register(new Setting<>("Sync", true));
    private final Setting<Integer> red = register(new Setting<>("Red", 255, 0, 255, v -> !sync.getValue()));
    private final Setting<Integer> green = register(new Setting<>("Green", 255, 0, 255, v -> !sync.getValue()));
    private final Setting<Integer> blue = register(new Setting<>("Blue", 255, 0, 255, v -> !sync.getValue()));

    private final AccessorRenderManager renderManager = (AccessorRenderManager) mc.getRenderManager();

    public static EntityESP INSTANCE;

    public EntityESP() {
        INSTANCE = this;
    }

    public final int getColor() {
        return sync.getValue() ? Colours.INSTANCE.getColor() : new Color(red.getValue(), green.getValue(), blue.getValue()).getRGB();
    }

    @Override
    public final void onRender3D() {
        final int size = mc.world.loadedEntityList.size();
        for (int i = 0; i < size; i++) {
            final Entity entity = mc.world.loadedEntityList.get(i);
            if (entity instanceof EntityExpBottle && bottles.getValue() || entity instanceof EntityItem && items.getValue()) {
                if (entity instanceof EntityItem && mc.player.getDistanceSq(entity) > 2500)
                    continue;

                final Vec3d vec = interpolateEntity(entity, mc.getRenderPartialTicks());
                RenderUtil.drawBoundingBox(new AxisAlignedBB(0.0, 0.0, 0.0, entity.width, entity.height, entity.width).offset(vec.x - renderManager.getRenderPosX() - entity.width / 2, vec.y - renderManager.getRenderPosY(), vec.z - renderManager.getRenderPosZ() - entity.width / 2).grow(0.125f), sync.getValue() ? new Color(Colours.INSTANCE.getColor()) : new Color(red.getValue(), green.getValue(), blue.getValue()), 1);
            }
        }
    }

    private Vec3d interpolateEntity(Entity entity, float time) {
        return new Vec3d(entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * time,
                entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * time,
                entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * time
        );
    }

}
