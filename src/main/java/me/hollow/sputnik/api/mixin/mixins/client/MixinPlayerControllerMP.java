package me.hollow.sputnik.api.mixin.mixins.client;

import me.hollow.sputnik.Main;
import me.hollow.sputnik.api.mixin.accessors.IPlayerControllerMP;
import me.hollow.sputnik.client.events.ClickBlockEvent;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerControllerMP.class)
public abstract class MixinPlayerControllerMP implements IPlayerControllerMP {

    @Override
    @Accessor("isHittingBlock")
    public abstract void setIsHittingBlock(boolean b);

    @Override
    @Accessor("blockHitDelay")
    public abstract void setBlockHitDelay(int delay);

    @Override
    @Accessor("curBlockDamageMP")
    public abstract float getCurBlockDamageMP();

    @Inject(method = "clickBlock", at = @At("HEAD"), cancellable = true)
    public void clickBlock(BlockPos loc, EnumFacing face, CallbackInfoReturnable<Boolean> cir) {
        final ClickBlockEvent event = new ClickBlockEvent(0, loc, face);
        Main.INSTANCE.getBus().post(event);
        if (event.isCancelled()) {
            cir.cancel();
        }
    }

    @Inject(method = "onPlayerDamageBlock(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;)Z", at = @At("HEAD"), cancellable = true)
    public void onPlayerDamageBlock(BlockPos posBlock, EnumFacing directionFacing, CallbackInfoReturnable<Boolean> cir) {
        ClickBlockEvent event = new ClickBlockEvent(1, posBlock, directionFacing);
        Main.INSTANCE.getBus().post(event);
        if (event.isCancelled())
            cir.cancel();
    }

}
