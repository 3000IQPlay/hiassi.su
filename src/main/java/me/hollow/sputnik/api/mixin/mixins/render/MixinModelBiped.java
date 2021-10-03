package me.hollow.sputnik.api.mixin.mixins.render;

import me.hollow.sputnik.client.modules.visual.HeadRotations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelBiped.class)
public class MixinModelBiped {

    @Shadow
    public ModelRenderer bipedHead;

    @Inject(method = "setRotationAngles", at = @At(value = "FIELD", target = "Lnet/minecraft/client/model/ModelBiped;swingProgress:F"))
    private void setAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn, CallbackInfo ci) {
        if (HeadRotations.INSTANCE.isEnabled() && entityIn == Minecraft.getMinecraft().player) {
            this.bipedHead.rotateAngleY = HeadRotations.INSTANCE.fakeYaw.getValue() / (180 / (float) Math.PI);
            this.bipedHead.rotateAngleX = HeadRotations.INSTANCE.fakePitch.getValue() / (180F / (float) Math.PI);
        }
    }

}
