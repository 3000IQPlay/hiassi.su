package me.hollow.sputnik.client.managers;

import me.hollow.sputnik.Main;
import me.hollow.sputnik.api.util.MessageUtil;
import me.hollow.sputnik.client.command.Command;
import me.hollow.sputnik.client.command.commands.*;
import me.hollow.sputnik.client.events.PacketEvent;
import me.hollow.sputnik.client.modules.client.ClickGui;
import net.minecraft.network.play.client.CPacketChatMessage;
import tcb.bces.listener.IListener;
import tcb.bces.listener.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommandManager implements IListener {

    private final List<Command> commands = new ArrayList<>();

    public CommandManager(){}

    @Subscribe
    public void onSendPacket(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketChatMessage) {
            String message = ((CPacketChatMessage) event.getPacket()).getMessage();
            if (message.startsWith(ClickGui.getInstance().prefix.getValue())) {
                String[] args = message.split(" ");
                String input = message.split(" ")[0].substring(1);
                for (Command command : commands) {
                    if (input.equalsIgnoreCase(command.getLabel()) || checkAliases(input, command)) {
                        event.setCancelled();
                        command.execute(args);
                    }
                }
                if (!event.isCancelled()) {
                    MessageUtil.sendClientMessage("Command " + message + " was not found!", true);
                    event.setCancelled();
                }
                event.setCancelled();
            }
        }
    }

    private boolean checkAliases(String input, Command command) {
        for (String str : command.getAliases()) {
            if (input.equalsIgnoreCase(str)) {
                return true;
            }
        }
        return false;
    }

    public void init() {
        register(new ToggleCommand(), new BindCommand(), new DrawnCommand(), new me.hollow.Main.client.command.commands.FriendCommand(), new SaveCommand(), new TutorialCommand());
        Main.INSTANCE.getBus().register(this);
    }

    public void register(Command... command) {
        Collections.addAll(commands, command);
    }

    @Override
    public boolean isListening() {
        return true;
    }
}
