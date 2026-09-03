package com.shapesnsizes.mixin;

import com.shapesnsizes.PlayerScale;
import net.minecraft.core.block.BlockLogicSoulCatcher;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BlockLogicSoulCatcher.class, remap = false)
public class BlockLogicSoulCatcherMixin {
	@Inject(method = "onEntityCollision", at = @At("TAIL"))
	private void shapesnsizes$softenForBigPlayers(World world, TilePosc tilePos, Entity entity, CallbackInfo ci) {
		double correction = PlayerScale.relieveDrag(entity, 0.4) / 0.4;
		if (correction != 1.0) {
			entity.xd *= correction;
			entity.zd *= correction;
		}
	}
}
