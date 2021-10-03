package me.hollow.sputnik.client.command.commands;

import me.hollow.sputnik.Main;
import me.hollow.sputnik.api.util.MessageUtil;
import me.hollow.sputnik.client.command.Command;
import me.hollow.sputnik.client.command.CommandManifest;
import me.hollow.sputnik.client.modules.Module;

@CommandManifest(label = "Drawn", aliases = {"Hide", "d"})
public class DrawnCommand extends Command {

    @Override
    public void execute(String[] args) {
        if (args.length < 2) return;
        final Module module = Main.INSTANCE.getModuleManager().getModuleByLabel(args[1]);
        if (module != null) {
            module.setDrawn(!module.isHidden());
            MessageUtil.sendClientMessage(module.getLabel() + " has been " + (module.isHidden() ? "hidden" : "unhidden"), false);
        }
    }

}
