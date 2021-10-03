package me.hollow.sputnik.api.util;

import me.hollow.sputnik.api.interfaces.Minecraftable;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemUtil implements Minecraftable {

    public static int getItemFromHotbar(Item item) {
        int slot = -1;

        for (int i = 0; i < 9; ++i) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == item) {
                slot = i;
            }
        }

        return slot;
    }


    public static List<Integer> getEmptySlots(boolean withXCarry) {
        List<Integer> outPut = new ArrayList<>();
        for(Map.Entry<Integer, ItemStack> entry : getInventoryAndHotbarSlots().entrySet()) {
            if(entry.getValue().isEmpty() || entry.getValue().getItem() == Items.AIR) {
                outPut.add(entry.getKey());
            }
        }

        if(withXCarry) {
            for (int i = 1; i < 5; i++) {
                Slot craftingSlot = mc.player.inventoryContainer.inventorySlots.get(i);
                ItemStack craftingStack = craftingSlot.getStack();
                if(craftingStack.isEmpty() || craftingStack.getItem() == Items.AIR) {
                    outPut.add(i);
                }
            }
        }
        return outPut;
    }

    public static Map<Integer, ItemStack> getInventoryAndHotbarSlots() {
        return getInventorySlots(9, 44);
    }

    private static Map<Integer, ItemStack> getInventorySlots(int currentI, int last) {
        int current = currentI;
        Map<Integer, ItemStack> fullInventorySlots = new HashMap<>();
        while (current <= last) {
            fullInventorySlots.put(current, mc.player.inventoryContainer.getInventory().get(current));
            current++;
        }
        return fullInventorySlots;
    }

    public static int getBlockFromHotbar(Block block) {
        int slot = -1;

        for (int i = 0; i < 9; ++i) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == Item.getItemFromBlock(block)) {
                slot = i;
            }
        }

        return slot;
    }

    public static int getItemSlot(Class clss) {
        int itemSlot = -1;

        for (int i = 45; i > 0; --i) {
            if (mc.player.inventory.getStackInSlot(i).getItem().getClass() == clss) {
                itemSlot = i;
                break;
            }
        }

        return itemSlot;
    }

    public static int getItemSlot(Item item) {
        int itemSlot = -1;

        for (int i = 45; i > 0; i--) {
            if (mc.player.inventory.getStackInSlot(i).getItem().equals(item)) {
                itemSlot = i;
                break;
            }
        }

        return itemSlot;
    }


    public static int getItemCount(Item item) {
        int count = 0;

        final int size = mc.player.inventory.mainInventory.size();
        for (int i = 0; i < size; i++) {
            final ItemStack itemStack = mc.player.inventory.mainInventory.get(i);
            if (itemStack.getItem() == item) {
                count += itemStack.getCount();
            }
        }

        final ItemStack offhandStack = mc.player.getHeldItemOffhand();
        if (offhandStack.getItem() == item) {
            count += offhandStack.getCount();
        }

        return count;
    }

    public static boolean isArmorLow(EntityPlayer player, int durability) {
        for (final ItemStack piece : player.inventory.armorInventory) {
            if (piece == null || getDamageInPercent(piece) < durability) {
                return true;
            }
        }
        return false;
    }

    public static int getItemDamage(ItemStack stack) {
        return stack.getMaxDamage() - stack.getItemDamage();
    }

    public static float getDamageInPercent(ItemStack stack) {
        float green = ((float)stack.getMaxDamage() - (float)stack.getItemDamage()) / (float)stack.getMaxDamage();
        float red = 1.0f - green;
        return 100 - (int)(red * 100.0f);
    }

    public static int getRoundedDamage(ItemStack stack) {
        return (int)getDamageInPercent(stack);
    }

    public static boolean hasDurability(ItemStack stack) {
        Item item = stack.getItem();
        return item instanceof ItemArmor || item instanceof ItemSword || item instanceof ItemTool || item instanceof ItemShield;
    }


}
