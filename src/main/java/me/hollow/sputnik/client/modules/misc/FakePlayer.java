package me.hollow.sputnik.client.modules.misc;

import com.mojang.authlib.GameProfile;
import me.hollow.sputnik.client.modules.Module;
import me.hollow.sputnik.client.modules.ModuleManifest;
import net.minecraft.client.entity.EntityOtherPlayerMP;

import java.util.UUID;

@ModuleManifest(label = "FakePlayer", listen = false, category = Module.Category.MISC, color = 0xff00ff00)
public class FakePlayer extends Module {

    private EntityOtherPlayerMP entityOtherPlayerMP = null;

    @Override
    public void onEnable() {
        if (mc.player == null) return;

        entityOtherPlayerMP = new EntityOtherPlayerMP(mc.world, new GameProfile(UUID.fromString("cc72ff00-a113-48f4-be18-2dda8db52355"), "RUBA4ER"));
        entityOtherPlayerMP.copyLocationAndAnglesFrom(mc.player);
        entityOtherPlayerMP.inventory = mc.player.inventory;
        mc.world.spawnEntity(entityOtherPlayerMP);
    }

    @Override
    public void onDisable() {
        if (entityOtherPlayerMP != null)
        mc.world.removeEntity(entityOtherPlayerMP);
    }
}
