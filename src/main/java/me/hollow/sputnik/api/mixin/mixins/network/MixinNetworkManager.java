package me.hollow.sputnik.api.mixin.mixins.network;

import io.netty.channel.ChannelHandlerContext;
import me.hollow.sputnik.Main;
import me.hollow.sputnik.client.events.PacketEvent;
import me.hollow.sputnik.client.modules.Module;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetworkManager.class)
public abstract class MixinNetworkManager {

    @Inject(method = "sendPacket(Lnet/minecraft/network/Packet;)V", at = @At("HEAD"), cancellable = true)
    public void onSendPacket(Packet<?> packetIn, CallbackInfo ci) {
        final PacketEvent.Send event = new PacketEvent.Send(packetIn);
        Main.INSTANCE.getBus().post(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method = "channelRead0", at = @At("HEAD"), cancellable = true)
    public void onReceivePacket(ChannelHandlerContext p_channelRead0_1_, Packet<?> packet, CallbackInfo ci) {
        final PacketEvent.Receive event = new PacketEvent.Receive(packet);
        Main.INSTANCE.getBus().post(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method = "handleDisconnection", at = @At("HEAD"))
    public void onDisconnect(CallbackInfo ci) {
        Main.INSTANCE.getModuleManager().getModules().forEach(Module::onDisconnect);
    }

}
