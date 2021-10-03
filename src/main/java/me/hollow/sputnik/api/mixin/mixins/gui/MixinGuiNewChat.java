package me.hollow.sputnik.api.mixin.mixins.gui;

import me.hollow.sputnik.client.modules.client.Manage;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiNewChat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GuiNewChat.class)
public class MixinGuiNewChat {

    @Redirect(method = "drawChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiNewChat;drawRect(IIIII)V"))
    public void drawChatHook1(int left, int top, int right, int bottom, int color) {
        if (Manage.INSTANCE.chatTweaks.getValue() && Manage.INSTANCE.giantBeetleSoundsLikeAJackhammer.getValue()) {
            return;
        }
        Gui.drawRect(left, top, right, bottom, color);
    }

}
