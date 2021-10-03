package me.hollow.sputnik.api.mixin.mixins.network;

import net.minecraft.network.play.server.SPacketPlayerPosLook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SPacketPlayerPosLook.class)
public interface AccessorSPacketPlayerPosLook {

    @Accessor("yaw")
    void setYaw(float yaw);

    @Accessor("pitch")
    void setPitch(float pitch);

}
