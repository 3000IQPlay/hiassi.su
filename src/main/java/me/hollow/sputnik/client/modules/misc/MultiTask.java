package me.hollow.sputnik.client.modules.misc;

import me.hollow.sputnik.api.property.Setting;
import me.hollow.sputnik.client.modules.Module;
import me.hollow.sputnik.client.modules.ModuleManifest;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;

@ModuleManifest(label = "MultiTask", listen = false, category = Module.Category.MISC, color = 0xff0355ff)
public class MultiTask extends Module {

    private final Setting<Boolean> entityHit = register(new Setting<>("Entities", true));

    private static MultiTask INSTANCE;

    public MultiTask() {
        INSTANCE = this;
    }

    public static MultiTask getInstance() {
        return INSTANCE;
    }

    public void onMouse(int button) {
        if (button == 0 && entityHit.getValue() && mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == RayTraceResult.Type.ENTITY && !(mc.objectMouseOver.entityHit instanceof EntityItem) && !(mc.objectMouseOver.entityHit instanceof EntityExpBottle) && !(mc.objectMouseOver.entityHit instanceof EntityArrow) && mc.gameSettings.keyBindUseItem.isKeyDown() && mc.player.isHandActive() && mc.gameSettings.keyBindAttack.isKeyDown()) {
            mc.getConnection().sendPacket(new CPacketUseEntity(mc.objectMouseOver.entityHit));
            mc.player.swingArm(EnumHand.MAIN_HAND);
        }
    }

}
