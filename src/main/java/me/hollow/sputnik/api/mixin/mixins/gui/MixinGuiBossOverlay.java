package me.hollow.sputnik.api.mixin.mixins.gui;

import me.hollow.sputnik.client.modules.visual.NoRender;
import net.minecraft.client.gui.GuiBossOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiBossOverlay.class)
public class MixinGuiBossOverlay {

    @Inject(method = "renderBossHealth", at = @At("HEAD"), cancellable = true)
    public void render(CallbackInfo ci) {
        if (NoRender.getInstance().isEnabled() && NoRender.getInstance().noBossOverlay.getValue()) {
            ci.cancel();
        }
    }

}
