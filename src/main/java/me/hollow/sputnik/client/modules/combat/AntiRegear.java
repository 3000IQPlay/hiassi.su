package me.hollow.sputnik.client.modules.combat;

import me.hollow.sputnik.api.property.Setting;
import me.hollow.sputnik.api.util.BlockUtil;
import me.hollow.sputnik.api.util.ItemUtil;
import me.hollow.sputnik.client.events.PacketEvent;
import me.hollow.sputnik.client.modules.Module;
import me.hollow.sputnik.client.modules.ModuleManifest;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.init.Items;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import tcb.bces.listener.Subscribe;

import java.util.HashSet;
import java.util.Set;

//TODO antifriend
@ModuleManifest(label = "AntiRegear", category = Module.Category.COMBAT)
public class  AntiRegear extends Module {


    private final Set<BlockPos> shulkerBlackList = new HashSet<>(); // sets have better .add and .contains performance

    @Override
    public void onUpdate() {
        for (BlockPos pos : BlockUtil.getSphere(6, true)) {
            if (mc.world.getBlockState(pos).getBlock() instanceof BlockShulkerBox) {
                if (shulkerBlackList.contains(pos)) {
                    continue;
                }
                mc.player.swingArm(EnumHand.MAIN_HAND);
                int lastSlot = -1;
                if (mc.player.getHeldItemMainhand().getItem() != Items.DIAMOND_PICKAXE) {
                    lastSlot = mc.player.inventory.currentItem;
                    int pickSlot = ItemUtil.getItemFromHotbar(Items.DIAMOND_PICKAXE);
                    if (pickSlot != -1)
                        mc.getConnection().sendPacket(new CPacketHeldItemChange(ItemUtil.getItemFromHotbar(Items.DIAMOND_PICKAXE)));
                }
                mc.getConnection().sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, EnumFacing.UP));
                mc.getConnection().sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, EnumFacing.UP));
                if (lastSlot != -1) {
                    mc.getConnection().sendPacket(new CPacketHeldItemChange(lastSlot));
                }
            }
        }
    }

    @Subscribe
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock) {
            CPacketPlayerTryUseItemOnBlock packet = (CPacketPlayerTryUseItemOnBlock) event.getPacket();
            if (mc.player.getHeldItem(packet.getHand()).getItem() instanceof ItemShulkerBox) { //make sure ur placing a shulker
                shulkerBlackList.add(packet.getPos().offset(packet.getDirection()));
            }
        }
    }

}
