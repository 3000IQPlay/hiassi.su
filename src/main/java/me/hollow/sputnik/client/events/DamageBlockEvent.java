package me.hollow.sputnik.client.events;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import tcb.bces.event.EventCancellable;

public class DamageBlockEvent extends EventCancellable {

    private final BlockPos blockPos;
    private final EnumFacing enumFacing;

    public DamageBlockEvent(BlockPos blockPos, EnumFacing enumFacing) {
        this.blockPos = blockPos;
        this.enumFacing = enumFacing;
    }

    public BlockPos getPos() {
        return this.blockPos;
    }

    public EnumFacing getFacing() {
        return this.enumFacing;
    }

}
