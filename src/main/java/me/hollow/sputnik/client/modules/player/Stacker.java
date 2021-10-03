package me.hollow.sputnik.client.modules.player;

import me.hollow.sputnik.api.property.Setting;
import me.hollow.sputnik.client.events.UpdateEvent;
import me.hollow.sputnik.client.modules.Module;
import me.hollow.sputnik.client.modules.ModuleManifest;
import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import tcb.bces.listener.Subscribe;

import java.util.HashMap;
import java.util.Map;

@ModuleManifest(label = "Stacker", category = Module.Category.PLAYER, color = 0xffffEA10)
public class Stacker extends Module {

    private final Setting<Integer> tickDelay = register(new Setting<>("Delay", 1, 0, 5));
    private final Setting<Integer> threshold = register(new Setting<>("Threshold", 30, 1, 63));

    private int delayStep = 0;

    private Map<Integer, ItemStack> getInventory() {
        return getInventorySlots(9, 35);
    }

    private Map<Integer, ItemStack> getHotbar() {
        return getInventorySlots(36, 44);
    }

    private Map<Integer, ItemStack> getInventorySlots(int current, int last) {
        final Map<Integer, ItemStack> fullInventorySlots = new HashMap<>();
        while (current <= last) {
            fullInventorySlots.put(current, mc.player.inventoryContainer.getInventory().get(current));
            current++;
        }
        return fullInventorySlots;
    }

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        if (isNull()) {
            return;
        }
        if (mc.currentScreen instanceof GuiContainer) {
            return;
        }
        if (delayStep < tickDelay.getValue()) {
            delayStep++;
            return;
        } else {
            delayStep = 0;
        }
        final Pair<Integer, Integer> slots = findReplenishableHotbarSlot();
        if (slots == null) {
            return;
        }
        final int inventorySlot = slots.getKey();
        final int hotbarSlot = slots.getValue();
        mc.playerController.windowClick(0, inventorySlot, 0, ClickType.PICKUP, mc.player);
        mc.playerController.windowClick(0, hotbarSlot, 0, ClickType.PICKUP, mc.player);
        mc.playerController.windowClick(0, inventorySlot, 0, ClickType.PICKUP, mc.player);
    }

    private Pair<Integer, Integer> findReplenishableHotbarSlot() {
        Pair<Integer, Integer> returnPair = null;
        for (Map.Entry<Integer, ItemStack> hotbarSlot : getHotbar().entrySet()) {

            ItemStack stack = hotbarSlot.getValue();

            if (stack.isEmpty() || stack.getItem() == Items.AIR) {
                continue;
            }

            if (!stack.isStackable()) {
                continue;
            }

            if (stack.getCount() >= stack.getMaxStackSize()) {
                continue;
            }

            if (stack.getCount() > threshold.getValue()) {
                continue;
            }

            int inventorySlot = findCompatibleInventorySlot(stack);

            if (inventorySlot == -1) {
                continue;
            }

            returnPair = new Pair<>(inventorySlot, hotbarSlot.getKey());

        }
        return returnPair;

    }

    private int findCompatibleInventorySlot(ItemStack hotbarStack) {

        int inventorySlot = -1;
        int smallestStackSize = 999;

        for (Map.Entry<Integer, ItemStack> entry : getInventory().entrySet()) {

            ItemStack inventoryStack = entry.getValue();

            if (inventoryStack.isEmpty() || inventoryStack.getItem() == Items.AIR) {
                continue;
            }

            if (!isCompatibleStacks(hotbarStack, inventoryStack)) {
                continue;
            }

            int currentStackSize = mc.player.inventoryContainer.getInventory().get(entry.getKey()).getCount();

            if (smallestStackSize > currentStackSize) {
                smallestStackSize = currentStackSize;
                inventorySlot = entry.getKey();
            }
        }
        return inventorySlot;
    }

    private boolean isCompatibleStacks(ItemStack stack1, ItemStack stack2) {
        if (!stack1.getItem().equals(stack2.getItem())) {
            return false;
        }

        if ((stack1.getItem() instanceof ItemBlock) && (stack2.getItem() instanceof ItemBlock)) {
            final Block block1 = ((ItemBlock) stack1.getItem()).getBlock();
            final Block block2 = ((ItemBlock) stack2.getItem()).getBlock();
            if (!block1.getMaterial(block1.getBlockState().getBaseState()).equals(block2.getMaterial(block2.getBlockState().getBaseState()))) {
                return false;
            }
        }
        if (!stackEqualExact(stack1, stack2)) {
            return false;
        }
        return stack1.getItemDamage() == stack2.getItemDamage();
    }

    private boolean stackEqualExact(ItemStack stack1, ItemStack stack2) {
        return stack1.getItem() == stack2.getItem() && (!stack1.getHasSubtypes() || stack1.getMetadata() == stack2.getMetadata()) && ItemStack.areItemStackTagsEqual(stack1, stack2);
    }

    private static class Pair<K, V> {
        final K key;
        final V value;

        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public V getValue() {
            return value;
        }

        public K getKey() {
            return key;
        }
    }

}
