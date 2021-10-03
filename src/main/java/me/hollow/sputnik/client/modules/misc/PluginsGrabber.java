package me.hollow.sputnik.client.modules.misc;

import joptsimple.internal.Strings;
import me.hollow.sputnik.api.util.CommandUtil;
import me.hollow.sputnik.client.events.PacketReceiveEvent;
import me.hollow.sputnik.client.modules.Module;
import me.hollow.sputnik.client.modules.ModuleManifest;
import net.minecraft.network.play.client.CPacketTabComplete;
import net.minecraft.network.play.server.SPacketTabComplete;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//thx cattyn

@ModuleManifest(label = "PluginsGrabber", category = Module.Category.MISC, color = 0xff4AE0AE)
public class PluginsGrabber extends Module {

    @Override
    public void onEnable()
    {
        super.onEnable();
        CommandUtil.sendChatMessage("Obtaining plugins");
        CPacketTabComplete packet = new CPacketTabComplete("/", null, false);
        mc.player.connection.sendPacket(packet);
    }

    @SubscribeEvent
    public void onReceivePacket(PacketReceiveEvent event) {
        if (event.getPacket() instanceof SPacketTabComplete) {
            SPacketTabComplete s3APacketTabComplete = (SPacketTabComplete) event.getPacket();

            List<String> plugins = new ArrayList<String>();
            String[] commands = s3APacketTabComplete.getMatches();

            for (int i = 0; i < commands.length; i++) {
                String[] command = commands[i].split(":");

                if (command.length > 1) {
                    String pluginName = command[0].replace("/", "");

                    if (!plugins.contains(pluginName)) {
                        plugins.add(pluginName);
                    }
                }
            }

            Collections.sort(plugins);

            if (!plugins.isEmpty()) {
                CommandUtil.sendChatMessage("Plugins \u00a77(\u00a78" + plugins.size() + "\u00a77): \u00a79"
                        + Strings.join(plugins.toArray(new String[0]), "\u00a77, \u00a79"));
            } else {
                CommandUtil.sendChatMessage("No plugins found");
            }

            this.disable();
        }
    }

}
