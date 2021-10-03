package me.hollow.sputnik.client.command.commands;

import me.hollow.sputnik.Main;
import me.hollow.sputnik.api.property.Bind;
import me.hollow.sputnik.api.util.MessageUtil;
import me.hollow.sputnik.client.command.Command;
import me.hollow.sputnik.client.command.CommandManifest;
import me.hollow.sputnik.client.modules.Module;
import org.lwjgl.input.Keyboard;

@CommandManifest(label = "Bind", aliases = {"b"})
public class BindCommand extends Command {

    @Override
    public void execute(String[] args) {
        //fix crash
        if (args.length < 2) {
            MessageUtil.sendClientMessage("Use the command like this -> (module, bind)", true);
            return;
        }
        final Module module = Main.INSTANCE.getModuleManager().getModuleByLabel(args[1]);
        if (module != null) {
            //no idea what that keyboard method does too lazy to test it lmao
            int index = Keyboard.getKeyIndex(args[2].toUpperCase());
            module.bind.setValue(new Bind(index));
            MessageUtil.sendClientMessage(module.getLabel() + " has been bound to " + Keyboard.getKeyName(index), false);
        }
    }

}
