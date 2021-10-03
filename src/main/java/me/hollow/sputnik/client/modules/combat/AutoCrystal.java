package me.hollow.sputnik.client.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.hollow.sputnik.Main;
import me.hollow.sputnik.api.mixin.mixins.network.AccessorCPacketUseEntity;
import me.hollow.sputnik.api.property.Setting;
import me.hollow.sputnik.api.util.Timer;
import me.hollow.sputnik.api.util.*;
import me.hollow.sputnik.api.util.render.RenderUtil;
import me.hollow.sputnik.client.events.PacketEvent;
import me.hollow.sputnik.client.modules.Module;
import me.hollow.sputnik.client.modules.ModuleManifest;
import me.hollow.sputnik.client.modules.client.Colours;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import tcb.bces.listener.Subscribe;

import java.awt.*;
import java.util.*;

@ModuleManifest(label = "AutoCrystal", category = Module.Category.COMBAT, color = 0x880000 /* red */)
public final class AutoCrystal extends Module {

    private final Setting<Page> page = register(new Setting<>("Page", Page.BREAK));

    //break page
    private final Setting<Float> breakRange = register(new Setting<>("Break Range", 5F, 1F, 6F, v -> page.getValue() == Page.BREAK));
    private final Setting<Float> breakWallRange = register(new Setting<>("Wall Range", 5F, 1F, 6F, v -> page.getValue() == Page.BREAK));
    private final Setting<Boolean> instant = register(new Setting<>("Instant", true, v -> page.getValue() == Page.BREAK));
    private final Setting<Integer> breakDelay = register(new Setting<>("Break Delay", 0, 0, 100, v -> page.getValue() == Page.BREAK));
    private final Setting<Boolean> antiWeakness = register(new Setting<>("Anti Weakness", true, v -> page.getValue() == Page.BREAK));
    private final Setting<Boolean> antiWeaknessSilent = register(new Setting<>("Anti Weakness Silent", true, v -> page.getValue() == Page.BREAK && antiWeakness.getValue()));
    private final Setting<Boolean> switchBack = register(new Setting<>("Switch Back", false, v -> page.getValue() == Page.PLACE && antiWeakness.getValue()));

    //place page
    private final Setting<Boolean> place = register(new Setting<>("Place", true, v -> page.getValue() == Page.PLACE));
    private final Setting<Float> placeRange = register(new Setting<>("Place Range", 5F, 1F, 6F, v -> page.getValue() == Page.PLACE && place.getValue()));
    private final Setting<Integer> facePlaceHp = register(new Setting<>("Faceplace HP", 8, 0, 36, v -> page.getValue() == Page.PLACE && place.getValue()));
    private final Setting<Float> minDamage = register(new Setting<>("MinDamage", 4F, 1F, 36F, v -> page.getValue() == Page.PLACE && place.getValue()));
    private final Setting<Integer> maxSelfDamage = register(new Setting<>("Max SelfDamage", 8, 1, 36, v -> page.getValue() == Page.PLACE && place.getValue()));
    private final Setting<Integer> armorScale = register(new Setting<>("Armor Scale", 10, 0, 100, v -> page.getValue() == Page.PLACE));
    private final Setting<Float> range = register(new Setting<>("Range", 9F, 1F, 15F, v -> page.getValue() == Page.PLACE));
    private final Setting<Boolean> secondCheck = register(new Setting<>("Second Check", true, v -> page.getValue() == Page.PLACE));
    private final Setting<Integer> cooldown = register(new Setting<>("Switch Delay", 0, 0, 1000, v -> page.getValue() != Page.RENDER));
    private final Setting<Boolean> autoSwitch = register(new Setting<>("Auto Switch", false, v -> page.getValue() == Page.PLACE));
    private final Setting<Boolean> silentSwitch =  register(new Setting<>("Silent Switch", false, v -> page.getValue() == Page.PLACE));

    //render page
    private final Setting<Boolean> sync = register(new Setting<>("Sync", true, v -> page.getValue() == Page.RENDER));
    private final Setting<Integer> red = register(new Setting<>("Red", 255, 0, 255, v -> !sync.getValue() && page.getValue() == Page.RENDER));
    private final Setting<Integer> green = register(new Setting<>("Green", 255, 0, 255, v -> !sync.getValue() && page.getValue() == Page.RENDER));
    private final Setting<Integer> blue = register(new Setting<>("Blue", 255, 0, 255, v -> !sync.getValue() && page.getValue() == Page.RENDER));
    private final Setting<Integer> alpha = register(new Setting<>("Alpha", 40, 0, 255, v -> page.getValue() == Page.RENDER));

    private final Set<BlockPos> placeSet = new HashSet<>();
    private final Map<Integer, Integer> attackMap = new HashMap<>();
    private final Timer clearTimer = new Timer();
    private final Timer breakTimer = new Timer();

    private BlockPos renderPos;
    private EntityPlayer currentTarget;
    private int predictedId = -1, ticks;
    private boolean offhand;

    public static AutoCrystal INSTANCE;

    public AutoCrystal() {
        INSTANCE = this;
    }

    @Override
    public void onToggle() {
        currentTarget = null;
        attackMap.clear();
        placeSet.clear();
        predictedId = -1;
        renderPos = null;
    }

    public void onTick() {
        if (isNull())
            return;

        if (ticks < 40) {
            ticks++;
        } else {
            ticks = 0;
            attackMap.clear();
        }

        if (clearTimer.hasReached(500)) {
            currentTarget = null;
            placeSet.clear();
            predictedId = -1;
            renderPos = null;
            clearSuffix();
            clearTimer.reset();
        }

        offhand = mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL;
        if (!offhand && mc.player.getHeldItemMainhand().getItem() != Items.END_CRYSTAL && !autoSwitch.getValue())
            return;
        if (!Main.INSTANCE.getEventManager().switchTimerPassed(cooldown.getValue())) {
            return;
        }

        doBreak();
        doPlace();
    }


    public void doBreak() {
        final EntityPlayer target = CombatUtil.getTarget(range.getValue());
        if (target == null)
            return;

        float maxDamage = 0;

        Entity maxCrystal = null;
        final float minDmg = EntityUtil.getHealth(target) < facePlaceHp.getValue() ? 2 : minDamage.getValue();
        for (Entity crystal : mc.world.loadedEntityList) {

            if (crystal instanceof EntityEnderCrystal) {

                if (mc.player.getDistance(crystal) > (mc.player.canEntityBeSeen(crystal) ? breakRange.getValue() : breakWallRange.getValue())) {
                    continue;
                }

                if (crystal.isDead) continue;

                if (attackMap.containsKey(crystal.getEntityId()) && attackMap.get(crystal.getEntityId()) > 5)
                    continue;

                final float targetDamage = EntityUtil.calculate(crystal.posX, crystal.posY, crystal.posZ, target);
                if (targetDamage > minDmg) {
                    final float selfDamage = EntityUtil.calculate(crystal.posX, crystal.posY, crystal.posZ, mc.player);
                    if (selfDamage > maxSelfDamage.getValue() || selfDamage + 0.5F >= EntityUtil.getHealth(mc.player))
                        continue;

                    if (targetDamage > maxDamage) {
                        maxDamage = targetDamage;
                        maxCrystal = crystal;
                    }
                }
            }
        }

        if (maxCrystal != null && breakTimer.hasReached(breakDelay.getValue())) {
            int lastSlot = -1;
            if (antiWeakness.getValue() && mc.player.isPotionActive(MobEffects.WEAKNESS)) {
                boolean swtch = !mc.player.isPotionActive(MobEffects.STRENGTH) || Objects.requireNonNull(mc.player.getActivePotionEffect(MobEffects.STRENGTH)).getAmplifier() != 2;

                final int swordSlot = ItemUtil.getItemSlot(Items.DIAMOND_SWORD);
                if (swtch && swordSlot != -1) {
                    lastSlot = mc.player.inventory.currentItem;
                    if (antiWeaknessSilent.getValue())
                        mc.getConnection().sendPacket(new CPacketHeldItemChange(swordSlot));
                    else
                        mc.player.inventory.currentItem = swordSlot;
                }
            }
            mc.getConnection().sendPacket(new CPacketUseEntity(maxCrystal));
            attackMap.put(maxCrystal.getEntityId(), attackMap.containsKey(maxCrystal.getEntityId()) ? attackMap.get(maxCrystal.getEntityId()) + 1 : 1);
            mc.player.swingArm(EnumHand.OFF_HAND);
            if (lastSlot != -1 && switchBack.getValue()) {
                if (antiWeaknessSilent.getValue())
                    mc.getConnection().sendPacket(new CPacketHeldItemChange(lastSlot));
                else
                    mc.player.inventory.currentItem = lastSlot;
            }
            breakTimer.reset();
        }
    }

    public void doPlace() {
        final EntityPlayer target = CombatUtil.getTarget(range.getValue());
        if (target == null)
            return;
        float maxDamage = 0;
        float minDmg = EntityUtil.getHealth(target) < facePlaceHp.getValue() ? 2 : minDamage.getValue();
        BlockPos placePos = null;
        for (BlockPos pos : BlockUtil.getSphere(placeRange.getValue(), true)) {
            if (!BlockUtil.canPlaceCrystal(pos, secondCheck.getValue()))
                continue;

            final float targetDamage = EntityUtil.calculate(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, target);

            if (targetDamage < minDmg) continue;

            final float selfDamage = EntityUtil.calculate(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, mc.player);

            final int crystalSlot = ItemUtil.getItemSlot(Items.END_CRYSTAL);

            if (selfDamage > maxSelfDamage.getValue() || selfDamage + 0.5F >= EntityUtil.getHealth(mc.player))
                continue;

            if (targetDamage > maxDamage) {
                maxDamage = targetDamage;
                placePos = pos;
                currentTarget = target;
            }
        }

        if (placePos != null) {
            if (autoSwitch.getValue() && !offhand) {
                int crystalSlot = ItemUtil.getItemFromHotbar(Items.END_CRYSTAL);
                if (crystalSlot == -1 || mc.player.getHeldItemMainhand().getItem() == Items.GOLDEN_APPLE && mc.player.isHandActive()) {
                    return;
                }
                mc.player.inventory.currentItem = crystalSlot;
                mc.playerController.updateController();
            } else {
            }
            mc.getConnection().sendPacket(new CPacketPlayerTryUseItemOnBlock(placePos, EnumFacing.UP, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.5F, 1F, 0.5F));
            placeSet.add(placePos);
            renderPos = placePos;
            setSuffix(currentTarget.getName());
        } else {
            renderPos = null;
            clearSuffix();
        }
    }

    @Override
    public void onRender3D() {
        if (renderPos != null) {
            RenderUtil.drawBoxESP(renderPos, sync.getValue() ? new Color(Colours.INSTANCE.getColor()) : new Color(red.getValue(), green.getValue(), blue.getValue()), 1, true, true, alpha.getValue(), 1);
        }
    }

    @Subscribe
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketSpawnObject && instant.getValue()) {
            final SPacketSpawnObject packet = (SPacketSpawnObject) event.getPacket();
            if (packet.getType() == 51 && placeSet.contains(new BlockPos(packet.getX(), packet.getY(), packet.getZ()).down())) {
                final AccessorCPacketUseEntity hitPacket = (AccessorCPacketUseEntity) new CPacketUseEntity();
                hitPacket.setEntityId(packet.getEntityID());
                hitPacket.setAction(CPacketUseEntity.Action.ATTACK);
                mc.getConnection().sendPacket((Packet<?>) hitPacket);
                predictedId = packet.getEntityID();
                attackMap.put(packet.getEntityID(), attackMap.containsKey(packet.getEntityID()) ? attackMap.get(packet.getEntityID()) + 1 : 1);
                mc.player.swingArm(EnumHand.OFF_HAND);
            }
        }

        if (event.getPacket() instanceof SPacketSoundEffect) {
            final SPacketSoundEffect packet = (SPacketSoundEffect) event.getPacket();
            if (packet.getCategory() == SoundCategory.BLOCKS && packet.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
                for (final Entity entity : new ArrayList<>(mc.world.loadedEntityList)) {
                    if (entity instanceof EntityEnderCrystal) {
                        if (entity.getDistanceSq(packet.getX(), packet.getY(), packet.getZ()) < 36) {
                            entity.setDead();
                            mc.world.removeEntity(entity);
                        }
                    }
                }
            }
        }
    }

    public enum Page {
        PLACE,
        BREAK,
        RENDER
    }

}
