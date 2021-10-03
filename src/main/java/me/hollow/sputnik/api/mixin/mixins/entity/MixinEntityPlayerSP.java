package me.hollow.sputnik.api.mixin.mixins.entity;

import me.hollow.sputnik.Main;
import me.hollow.sputnik.api.util.Timer;
import me.hollow.sputnik.client.events.UpdateEvent;
import me.hollow.sputnik.client.modules.exploit.Burrow;
import me.hollow.sputnik.client.modules.misc.AntiPush;
import me.hollow.sputnik.client.modules.player.AntiVoid;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityPlayerSP.class)
public abstract class MixinEntityPlayerSP {

    @Shadow
    protected Minecraft mc;

    @Inject(method = "onUpdate", at = @At("RETURN"))
    public void onUpdatePost(CallbackInfo ci) {
        Main.INSTANCE.getBus().post(new UpdateEvent());
    }


    private final Timer timer = new Timer();

    //run antivoid here so we can cancel all movement packets so we dont kicked for too many packets
    @Inject(method = "onUpdateWalkingPlayer", at = @At("HEAD"), cancellable = true)
    public void onUpdateWalkingPlayerHead(CallbackInfo c) {
        if (AntiVoid.INSTANCE.isEnabled() && mc.world != null) {
            if (mc.player.noClip || mc.player.posY > AntiVoid.INSTANCE.height.getValue()) {
                return;
            }
            final RayTraceResult trace = mc.world.rayTraceBlocks(mc.player.getPositionVector(), new Vec3d(mc.player.posX, 0.0, mc.player.posZ), false, false, false);
            if (trace != null && trace.typeOfHit == RayTraceResult.Type.BLOCK) {
                return;
            }
            if (AntiVoid.INSTANCE.flag.getValue()) {
                c.cancel(); // do not let the Dumb Nigga minecraft Send packets
                mc.getConnection().sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.05 /* need to experiment with this value */, mc.player.posY, true));
                mc.getConnection().sendPacket(new CPacketPlayer.Position(mc.player.posX, -1337 /*cool leet number do u get the joke ?*/, mc.player.posZ, true));
            } else {
                mc.player.motionY += (float) 0.0553525;
                if (timer.hasReached(100)) {
                    mc.getConnection().sendPacket(new CPacketPlayer(true));
                    timer.reset();
                }
            }
        }
    }

    @Inject(method = "pushOutOfBlocks", at = @At("HEAD"), cancellable = true)
    public void push(double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
       if (new BlockPos(mc.player.getPositionVector()).equals(Burrow.getInstance().startPos) || AntiPush.INSTANCE.isEnabled()) {
            cir.cancel();
            cir.setReturnValue(false);
        }
    }

}
