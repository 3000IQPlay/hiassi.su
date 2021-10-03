package me.hollow.sputnik.client.modules.client;

import me.hollow.sputnik.Main;
import me.hollow.sputnik.api.property.Setting;
import me.hollow.sputnik.client.modules.Module;
import me.hollow.sputnik.client.modules.ModuleManifest;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

@ModuleManifest(label = "MiddleClick", listen = false, category = Module.Category.CLIENT)
public class MiddleClick extends Module {

    private static MiddleClick INSTANCE;

    private final Setting<Boolean> friends = register(new Setting<>("Friends", true));

    public MiddleClick() {
        INSTANCE = this;
    }

    public static MiddleClick getInstance() {
        return INSTANCE;
    }

    public void run(int mouse) {
        if (mouse == 2 && friends.getValue()) {
            if (mc.objectMouseOver.entityHit != null) {
                final Entity entity = mc.objectMouseOver.entityHit;

                if (!(entity instanceof EntityPlayer)) {
                    return;
                }

                if (Main.INSTANCE.getFriendManager().isFriend(entity.getName())) {
                    Main.INSTANCE.getFriendManager().removeFriend(entity.getName());
                } else {
                    Main.INSTANCE.getFriendManager().addFriend(entity.getName());
                }
            }
        }
    }

}
