package me.hollow.sputnik.client.modules.combat;

import me.hollow.sputnik.Main;
import me.hollow.sputnik.api.property.Bind;
import me.hollow.sputnik.api.property.Setting;
import me.hollow.sputnik.api.util.BlockUtil;
import me.hollow.sputnik.api.util.CombatUtil;
import me.hollow.sputnik.api.util.ItemUtil;
import me.hollow.sputnik.api.util.Timer;
import me.hollow.sputnik.client.events.KeyEvent;
import me.hollow.sputnik.client.modules.Module;
import me.hollow.sputnik.client.modules.ModuleManifest;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Keyboard;
import tcb.bces.listener.Subscribe;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

@ModuleManifest(label = "AutoArmor", category = Module.Category.COMBAT, color = 0xff40AE70)
public class AutoArmor extends Module {

    private final Setting<Integer> delay = register(new Setting("Delay", 50, 0, 500));
    private final Setting<Boolean> mendingTakeOff = register(new Setting("Auto Mend", false));
    private final Setting<Integer> crystalRange = register(new Setting<>("Crystal Range", 10, 0, 20));
    private final Setting<Integer> closestEnemy = register(new Setting("Enemy Range", 8, 1, 20, v -> mendingTakeOff.getValue()));
    private final Setting<Integer> helmetThreshold = register(new Setting("Helmet %", 80, 1, 100, v -> mendingTakeOff.getValue()));
    private final Setting<Integer> chestThreshold = register(new Setting("Chest %", 80, 1, 100, v -> mendingTakeOff.getValue()));
    private final Setting<Integer> legThreshold = register(new Setting("Legs %", 80, 1, 100, v -> mendingTakeOff.getValue()));
    private final Setting<Integer> bootsThreshold = register(new Setting("Boots %", 80, 1, 100, v -> mendingTakeOff.getValue()));
    private final Setting<Boolean> curse = register(new Setting("Curse Of Binding", false));
    private final Setting<Integer> actions = register(new Setting("Actions", 3, 1, 12));
    private final Setting<Bind> elytraBind = register(new Setting("Elytra", new Bind(-1)));
    private final Setting<Boolean> tps = register(new Setting("Tps Sync", true));


    private final Timer timer = new Timer();
    private final Timer elytraTimer = new Timer();
    private final Queue<Task> taskList = new ConcurrentLinkedQueue<>();
    private final List<Integer> doneSlots = new ArrayList<>();
    private boolean elytraOn = false;

    @Subscribe
    public void onKeyInput(KeyEvent event) {
        if (elytraBind.getValue().getKey() == Keyboard.getEventKey()) {
            elytraOn = !elytraOn;
        }
    }


    @Override
    public void onDisable() {
        taskList.clear();
        doneSlots.clear();
        elytraOn = false;
    }

    @Override
    public void onDisconnect() {
        taskList.clear();
        doneSlots.clear();
    }

    @Override
    public void onUpdate() {
        if (isNull() || (mc.currentScreen instanceof GuiContainer && !(mc.currentScreen instanceof GuiInventory))) {
            return;
        }

        if(taskList.isEmpty()) {
            if (mendingTakeOff.getValue() && mc.player.getHeldItemMainhand().getItem() == Items.EXPERIENCE_BOTTLE && mc.gameSettings.keyBindUseItem.isKeyDown() && (isSafe() || BlockUtil.isSafe(mc.player, 1))) {
                final ItemStack helm = mc.player.inventoryContainer.getSlot(5).getStack();
                if (!helm.isEmpty()) {
                    int helmDamage = ItemUtil.getRoundedDamage(helm);
                    if (helmDamage >= helmetThreshold.getValue()) {
                        takeOffSlot(5);
                    }
                }

                final ItemStack chest = mc.player.inventoryContainer.getSlot(6).getStack();
                if (!chest.isEmpty()) {
                    int chestDamage = ItemUtil.getRoundedDamage(chest);
                    if (chestDamage >= chestThreshold.getValue()) {
                        takeOffSlot(6);
                    }
                }

                final ItemStack legging = mc.player.inventoryContainer.getSlot(7).getStack();
                if (!legging.isEmpty()) {
                    int leggingDamage = ItemUtil.getRoundedDamage(legging);
                    if (leggingDamage >= legThreshold.getValue()) {
                        takeOffSlot(7);
                    }
                }

                final ItemStack feet = mc.player.inventoryContainer.getSlot(8).getStack();
                if (!feet.isEmpty()) {
                    int bootDamage = ItemUtil.getRoundedDamage(feet);
                    if (bootDamage >= bootsThreshold.getValue()) {
                        takeOffSlot(8);
                    }
                }
                return;
            }

            final ItemStack helm = mc.player.inventoryContainer.getSlot(5).getStack();
            if (helm.getItem() == Items.AIR) {
                final int slot = findArmorSlot(EntityEquipmentSlot.HEAD, curse.getValue(), false);
                if (slot != -1) {
                    getSlotOn(5, slot);
                }
            }

            final ItemStack chest = mc.player.inventoryContainer.getSlot(6).getStack();
            if (chest.getItem() == Items.AIR) {
                if (taskList.isEmpty()) {
                    if (elytraOn && elytraTimer.hasReached(500)) {
                        int elytraSlot = findItemInventorySlot(Items.ELYTRA, false);
                        if (elytraSlot != -1) {
                            taskList.add(new Task(elytraSlot, true));
                            taskList.add(new Task());
                            elytraTimer.reset();
                        }
                    } else if (!elytraOn) {
                        final int slot = findArmorSlot(EntityEquipmentSlot.CHEST, curse.getValue(), true);
                        if (slot != -1) {
                            getSlotOn(6, slot);
                        }
                    }
                }
            } else {
                if (elytraOn && chest.getItem() != Items.ELYTRA && elytraTimer.hasReached(500)) {
                    if (taskList.isEmpty()) {
                        final int slot = findItemInventorySlot(Items.ELYTRA, false);
                        if(slot != -1) {
                            taskList.add(new Task(slot));
                            taskList.add(new Task(6));
                            taskList.add(new Task(slot));
                            taskList.add(new Task());
                        }
                        elytraTimer.reset();
                    }
                } else if (!elytraOn && chest.getItem() == Items.ELYTRA && elytraTimer.hasReached(500) && taskList.isEmpty()) {
                    //TODO: WTF IS THIS
                    int slot = findItemInventorySlot(Items.DIAMOND_CHESTPLATE, false);

                    if (slot != -1) {
                        taskList.add(new Task(slot));
                        taskList.add(new Task(6));
                        taskList.add(new Task(slot));
                        taskList.add(new Task());
                    }
                    elytraTimer.reset();
                }
            }

            final ItemStack legging = mc.player.inventoryContainer.getSlot(7).getStack();
            if (legging.getItem() == Items.AIR) {
                final int slot = findArmorSlot(EntityEquipmentSlot.LEGS, curse.getValue(), true);
                if (slot != -1) {
                    getSlotOn(7, slot);
                }
            }

            final ItemStack feet = mc.player.inventoryContainer.getSlot(8).getStack();
            if (feet.getItem() == Items.AIR) {
                final int slot = findArmorSlot(EntityEquipmentSlot.FEET, curse.getValue(), true);
                if (slot != -1) {
                    getSlotOn(8, slot);
                }
            }
        }

        if(timer.hasReached((int)(delay.getValue() * (tps.getValue() ? Main.INSTANCE.getTpsManager().getTpsFactor() : 1)))) {
            if (!taskList.isEmpty()) {
                for (int i = 0; i < actions.getValue(); i++) {
                    Task task = taskList.poll();
                    if (task != null) {
                        task.run();
                    }
                }
            }
            timer.reset();
        }
    }

    private void takeOffSlot(int slot) {
        if(taskList.isEmpty()) {
            int target = -1;
            for(int i : findEmptySlots(true)) {
                if (!doneSlots.contains(target)) {
                    target = i;
                    doneSlots.add(i);
                }
            }

            if (target != -1) {
                taskList.add(new Task(slot, true));
                taskList.add(new Task());
            }
        }
    }

    public int findItemInventorySlot(Item item, boolean offHand) {
        int slot = -1;
        for(Map.Entry<Integer, ItemStack> entry : getInventoryAndHotbarSlots().entrySet()) {
            if(entry.getValue().getItem() == item) {
                if(entry.getKey() == 45 && !offHand) {
                    continue;
                }
                slot = entry.getKey();
                return slot;
            }
        }
        return slot;
    }

    public List<Integer> findEmptySlots(boolean withXCarry) {
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

    public Map<Integer, ItemStack> getInventoryAndHotbarSlots() {
        return getInventorySlots(9, 44);
    }

    public int findArmorSlot(EntityEquipmentSlot type, boolean binding) {
        int slot = -1;
        float damage = 0;
        for (int i = 9; i < 45; i++) {
            final ItemStack s = Minecraft.getMinecraft().player.inventoryContainer.getSlot(i).getStack();
            if (s.getItem() != Items.AIR && s.getItem() instanceof ItemArmor) {
                final ItemArmor armor = (ItemArmor) s.getItem();
                if (armor.armorType == type) {
                    final float currentDamage = (armor.damageReduceAmount + EnchantmentHelper.getEnchantmentLevel(Enchantments.PROTECTION, s));
                    final boolean cursed = binding && (EnchantmentHelper.hasBindingCurse(s));
                    if (currentDamage > damage && !cursed) {
                        damage = currentDamage;
                        slot = i;
                    }
                }
            }
        }
        return slot;
    }

    public int findArmorSlot(EntityEquipmentSlot type, boolean binding, boolean withXCarry) {
        int slot = findArmorSlot(type, binding);
        if(slot == -1 && withXCarry) {
            float damage = 0;
            for (int i = 1; i < 5; i++) {
                Slot craftingSlot = mc.player.inventoryContainer.inventorySlots.get(i);
                ItemStack craftingStack = craftingSlot.getStack();
                if(craftingStack.getItem() != Items.AIR && craftingStack.getItem() instanceof ItemArmor) {
                    final ItemArmor armor = (ItemArmor)craftingStack.getItem();
                    if (armor.armorType == type) {
                        final float currentDamage = (armor.damageReduceAmount + EnchantmentHelper.getEnchantmentLevel(Enchantments.PROTECTION, craftingStack));
                        final boolean cursed = binding && (EnchantmentHelper.hasBindingCurse(craftingStack));
                        if(currentDamage > damage && !cursed) {
                            damage = currentDamage;
                            slot = i;
                        }
                    }
                }
            }
        }
        return slot;
    }

    private Map<Integer, ItemStack> getInventorySlots(int currentI, int last) {
        int current = currentI;
        Map<Integer, ItemStack> fullInventorySlots = new HashMap<>();
        while (current <= last) {
            fullInventorySlots.put(current, mc.player.inventoryContainer.getInventory().get(current));
            current++;
        }
        return fullInventorySlots;
    }

    private void getSlotOn(int slot, int target) {
        if (taskList.isEmpty()) {
            doneSlots.remove((Object) target);
            taskList.add(new Task(target, true));
            taskList.add(new Task());
        }
    }

    private static class Task {
        private final int slot;
        private boolean update = false;
        private boolean quickClick = false;
        private Minecraft mc = Minecraft.getMinecraft();

        public Task(int slot, boolean quickClick) {
            this.slot = slot;
            this.quickClick = quickClick;
        }

        public Task(int slot) {
            this.slot = slot;
            this.quickClick = false;
        }

        public Task() {
            this.update = true;
            this.slot = -1;
        }

        public void run() {
            if (slot != -1) {
                mc.playerController.windowClick(0, this.slot, 0, this.quickClick ? ClickType.QUICK_MOVE : ClickType.PICKUP, mc.player);
            }

            if (this.update) {
                mc.playerController.updateController();
            }
        }

        public boolean isSwitching() {
            return !this.update;
        }
    }

    private boolean isSafe() {
        Entity crystal = mc.world.loadedEntityList
                .stream()
                .filter(e -> e instanceof EntityEnderCrystal)
                .filter(e -> mc.player.getDistance(e) > crystalRange.getValue())
                .findFirst()
                .orElse(null);
        if (crystal == null) {
            EntityPlayer closest = CombatUtil.getTarget(closestEnemy.getValue());
            if (closest == null) {
                return true;
            }
            return mc.player.getDistanceSq(closest) >= square(closestEnemy.getValue());
        }
        else {
            return false;
        }

    }

    private double square(double sq) {
        return sq*sq;
    }

}
