package me.hollow.sputnik.client.modules.combat;

import me.hollow.sputnik.Main;
import me.hollow.sputnik.api.property.Bind;
import me.hollow.sputnik.api.property.Setting;
import me.hollow.sputnik.api.util.ItemUtil;
import me.hollow.sputnik.client.events.KeyEvent;
import me.hollow.sputnik.client.modules.Module;
import me.hollow.sputnik.client.modules.ModuleManifest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import tcb.bces.listener.Subscribe;

@ModuleManifest(label = "Offhand", category = Module.Category.COMBAT, color = 0x1F85DE)
public class Offhand extends Module {

    private final Setting<Float> health = register(new Setting<>("C-T-Health", 15F, 1F, 37F));
    private final Setting<Float> gappleHealth = register(new Setting<>("G-T-Health", 8F, 1F, 37F));
    private final Setting<Bind> gappleBind = register(new Setting<>("Gapple Bind", new Bind(-1)));

    private boolean gappling;

    @Subscribe
    public void onKey(KeyEvent event) {
        if (event.getKey() == gappleBind.getValue().getKey()) {
            gappling = !gappling;
        }
    }

    @Override
    public void onUpdate() {
        if (mc.currentScreen instanceof GuiContainer) {
            return;
        }

        final Item item = shouldTotem() ? Items.TOTEM_OF_UNDYING : gappling ? Items.GOLDEN_APPLE : Items.END_CRYSTAL;
        final int getSlot = ItemUtil.getItemSlot(item);

        if (mc.player.getHeldItemOffhand().getItem() != item) {
            if (getSlot != -1) {
                switchItem(getSlot < 9 ? getSlot + 36 : getSlot);
                mc.playerController.updateController(); //sync held item in mainhand
            }
        }

        if (item == Items.END_CRYSTAL) {
            setSuffix("Crystal");
        } else if (item == Items.TOTEM_OF_UNDYING) {
            setSuffix("Totem");
        } else {
            setSuffix("Gapple");
        }
    }

    private void switchItem(int slot) {
        mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
        mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, mc.player);
        mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
    }

    private boolean shouldTotem() {
        if (mc.player.fallDistance > 10) {
            return true;
        }

        if (ItemUtil.getItemCount(Items.TOTEM_OF_UNDYING) == 0) {
            return false;
        }

        if (ItemUtil.getItemCount(gappling ? Items.GOLDEN_APPLE : Items.END_CRYSTAL) == 0) {
            return true;
        }

        return mc.player.getHealth() + mc.player.getAbsorptionAmount() <= getHealth() || !Main.INSTANCE.getSafeManager().isSafe() || mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() == Items.ELYTRA;
    }

    private float getHealth() {
        return gappling ? gappleHealth.getValue() : health.getValue();
    }

}
