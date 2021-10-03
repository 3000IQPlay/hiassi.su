package me.hollow.sputnik.api.mixin.mixins.render;

import me.hollow.sputnik.client.modules.visual.NoRender;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockModelRenderer.class)
public class MixinBlockRender {

    @Inject(method = "renderModel(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/client/renderer/block/model/IBakedModel;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/client/renderer/BufferBuilder;ZJ)Z", at = @At("INVOKE"), cancellable = true)
    public void render(IBlockAccess worldIn, IBakedModel modelIn, IBlockState stateIn, BlockPos posIn, BufferBuilder buffer, boolean checkSides, long rand, CallbackInfoReturnable<Boolean> cir) {
        if (NoRender.getInstance().isEnabled() && NoRender.getInstance().boxedVines.getValue() && stateIn.getBlock() == Blocks.VINE) {
            cir.cancel();
        }
    }

}
