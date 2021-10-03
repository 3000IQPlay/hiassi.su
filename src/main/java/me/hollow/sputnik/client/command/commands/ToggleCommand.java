package me.hollow.sputnik.client.command.commands;

import me.hollow.sputnik.Main;
import me.hollow.sputnik.api.util.MessageUtil;
import me.hollow.sputnik.client.command.Command;
import me.hollow.sputnik.client.command.CommandManifest;
import me.hollow.sputnik.client.modules.Module;

@CommandManifest(label = "Toggle", aliases = {"t"})
public class ToggleCommand extends Command {

    @Override
    public void execute(String[] args) {
        //fix crash
        if (args.length < 2) {
            return;
        }
        final Module module = Main.INSTANCE.getModuleManager().getModuleByLabel(args[1]);
        if (module != null) {
            module.toggle();
            MessageUtil.sendClientMessage(module.getLabel() + " has been toggled " + (module.isEnabled() ? "on" : "off"), false);
        }
    }
}
