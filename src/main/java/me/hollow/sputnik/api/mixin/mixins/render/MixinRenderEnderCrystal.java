package me.hollow.sputnik.api.mixin.mixins.render;

import me.hollow.sputnik.client.modules.visual.Chams;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderEnderCrystal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RenderEnderCrystal.class)
public class MixinRenderEnderCrystal {

    @Redirect(method = "doRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelBase;render(Lnet/minecraft/entity/Entity;FFFFFF)V"))
    public void renderModelBaseHook(ModelBase model, Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (Chams.INSTANCE.isEnabled()) {
            GlStateManager.scale(Chams.INSTANCE.scale.getValue(), Chams.INSTANCE.scale.getValue(), Chams.INSTANCE.scale.getValue());
            if (Chams.INSTANCE.wireframe.getValue() || Chams.INSTANCE.chams.getValue()) {
                if (Chams.INSTANCE.wireframe.getValue()) {
                    Chams.INSTANCE.onRenderModel(model, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                }

                if (Chams.INSTANCE.chams.getValue()) {
                    GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
                    GL11.glDisable(3008)    ;
                    GL11.glDisable(3553);
                    GL11.glDisable(2896);
                    GL11.glEnable(3042);
                    GL11.glBlendFunc(770, 771);
                    GL11.glLineWidth(1.5f);
                    GL11.glEnable(2960);
                    GL11.glEnable(10754);
                    GL11.glDepthMask(false);
                    GL11.glColor4d(((float) Chams.INSTANCE.invisibleRed.getValue() / 255), ((float) Chams.INSTANCE.invisibleGreen.getValue() / 255), ((float) Chams.INSTANCE.invisibleBlue.getValue() / 255), ((float) Chams.INSTANCE.alpha.getValue() / 255));
                    model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                    GL11.glEnable(GL11.GL_DEPTH_TEST);
                    GL11.glDepthMask(true);
                    GL11.glColor4d(((float) Chams.INSTANCE.visibleRed.getValue() / 255), ((float) Chams.INSTANCE.visibleGreen.getValue() / 255), ((float) Chams.INSTANCE.visibleBlue.getValue() / 255), ((float) Chams.INSTANCE.alpha.getValue() / 255));
                    model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                    GL11.glEnable(3042);
                    GL11.glEnable(2896);
                    GL11.glEnable(3553);
                    GL11.glEnable(3008);
                    GL11.glPopAttrib();
                }
            } else {
                model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            }
            GlStateManager.scale(1.0f / Chams.INSTANCE.scale.getValue(), 1.0f / Chams.INSTANCE.scale.getValue(), 1.0f / Chams.INSTANCE.scale.getValue());
        } else {
            model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        }
    }

    /**
     * @author hello
     * pig
     */
    @Overwrite
    public ResourceLocation getEntityTexture(EntityEnderCrystal crystal) {
        return new ResourceLocation("textures/entity/pig/pig.png");
    }

}
