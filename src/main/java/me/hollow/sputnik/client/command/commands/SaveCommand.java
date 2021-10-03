package me.hollow.sputnik.client.command.commands;

import me.hollow.sputnik.Main;
import me.hollow.sputnik.client.command.Command;
import me.hollow.sputnik.client.command.CommandManifest;

@CommandManifest(label = "Save", aliases = "s")
public class SaveCommand extends Command {

    @Override
    public void execute(String[] args) {
        Main.INSTANCE.getConfigManager().saveConfig(Main.INSTANCE.getConfigManager().config.replaceFirst("TrollGod/", ""));
    }
}
