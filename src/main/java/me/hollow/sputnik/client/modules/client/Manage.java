package me.hollow.sputnik.client.modules.client;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.hollow.sputnik.Main;
import me.hollow.sputnik.api.mixin.mixins.network.AccessorSPacketChat;
import me.hollow.sputnik.api.property.Setting;
import me.hollow.sputnik.client.events.PacketEvent;
import me.hollow.sputnik.client.modules.Module;
import me.hollow.sputnik.client.modules.ModuleManifest;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.util.text.TextComponentString;
import tcb.bces.listener.Subscribe;

import java.util.Date;

@ModuleManifest(label = "Manage", category = Module.Category.CLIENT, persistent = true, listen = false, drawn = false)
public final class Manage extends Module {

    //PREFIX
    //public final Setting<String> prefixName = register(new Setting<>("Prefix Name", "TrollGod.CC"));

    //UNFOCUSED CPU
    public final Setting<Boolean> unfocusedLimit = register(new Setting<>("Limit Unfocused", true));
    public final Setting<Integer> unfocusedFPS = register(new Setting<>("Unfocused FPS", 60, 1, 240, v -> unfocusedLimit.getValue()));

    //TAB TWEAKS
    public final Setting<Boolean> tabTweaks = register(new Setting<>("Tab Tweaks", true));
    public final Setting<Boolean> highlightFriends = register(new Setting<>("Highlight Friends", true, v -> tabTweaks.getValue()));

    //CHAT TWEAKS
    public final Setting<Boolean> chatTweaks = register(new Setting<>("Chat Tweaks", true));
    private final Setting<Boolean> timestamps = register(new Setting<>("Timestamps", true, v -> chatTweaks.getValue()));
    public final Setting<Boolean> giantBeetleSoundsLikeAJackhammer = register(new Setting<>("No Rect", true, v -> chatTweaks.getValue()));

    public static Manage INSTANCE;

    public Manage() {
        INSTANCE = this;
    }

    @Override
    public void onLoad() {
        Main.INSTANCE.getBus().register(this);
    }

    @Subscribe
    public final void onPacketReceive(PacketEvent.Receive event) {
        if (timestamps.getValue() && event.getPacket() instanceof SPacketChat) {
            final SPacketChat packet = (SPacketChat) event.getPacket();

            final Date date = new Date();
            final AccessorSPacketChat chatPacket = (AccessorSPacketChat) event.getPacket();

            boolean add = false;
            if (date.getMinutes() <= 9)
                add = true;

            final String time = "<" + ChatFormatting.LIGHT_PURPLE + date.getHours() + ":" + (add ? "0" : "") + date.getMinutes() + ChatFormatting.RESET + "> ";
            chatPacket.setChatComponent(new TextComponentString(time + packet.getChatComponent().getFormattedText()));
        }
    }

}
