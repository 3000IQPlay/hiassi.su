package me.hollow.sputnik.api.mixin.mixins.gui;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.hollow.sputnik.Main;
import me.hollow.sputnik.client.modules.client.Manage;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.scoreboard.ScorePlayerTeam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GuiPlayerTabOverlay.class)
public class MixinGuiPlayerTab {

    @Inject(method = "getPlayerName", at = @At("HEAD"), cancellable = true)
    public void getPlayerName(NetworkPlayerInfo networkPlayerInfoIn, CallbackInfoReturnable returnable) {
        if (Manage.INSTANCE.tabTweaks.getValue()) {
            returnable.cancel();
            returnable.setReturnValue(getPlayerName(networkPlayerInfoIn));
        }
    }

    public String getPlayerName(NetworkPlayerInfo networkPlayerInfoIn) {
        final String dname = networkPlayerInfoIn.getDisplayName() != null ? networkPlayerInfoIn.getDisplayName().getFormattedText() : ScorePlayerTeam.formatPlayerName(networkPlayerInfoIn.getPlayerTeam(), networkPlayerInfoIn.getGameProfile().getName());
        if (Main.INSTANCE.getFriendManager().isFriend(dname)) {
            String tweaks = "";

            if (Manage.INSTANCE.highlightFriends.getValue()) {
                tweaks += ChatFormatting.DARK_PURPLE + "" + ChatFormatting.BOLD;
            }

            return tweaks + dname;
        }
        return dname;
    }

}
