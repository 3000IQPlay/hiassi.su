package me.hollow.Main.client.command.commands;

import me.hollow.sputnik.Main;
import me.hollow.sputnik.client.command.Command;
import me.hollow.sputnik.client.command.CommandManifest;

@CommandManifest(label = "Friend", aliases = {"friends", "friend, f"})
public class FriendCommand extends Command {

    @Override
    public void execute(String[] args) {
        if (args.length < 2) {
            return;
        }

        try {
            String name = args[2];
            switch (args[1].toUpperCase()) {
                case ("ADD"):
                    Main.INSTANCE.getFriendManager().addFriend(name);
                    break;
                case ("DEL"):
                    Main.INSTANCE.getFriendManager().removeFriend(name);
                    break;
                case ("DELETE"):
                    Main.INSTANCE.getFriendManager().removeFriend(name);
                    break;
                case ("CLEAR"):
                    Main.INSTANCE.getFriendManager().clearFriends();
                    break;
                case ("INSIDE"):
                    Main.INSTANCE.getFriendManager().clearFriends();
                    break;
            }
        } catch (Exception e) {
        }
    }

}
