package com.shapesnsizes.mixin;

import com.shapesnsizes.PlayerScale;
import net.minecraft.core.block.BlockLogicMagma;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BlockLogicMagma.class, remap = false)
public class BlockLogicMagmaMixin {
	@Inject(method = "onEntityCollision", at = @At("HEAD"), cancellable = true)
	private void shapesnsizes$sparesTheExtremes(World world, TilePosc tilePos, Entity entity, CallbackInfo ci) {
		if (entity instanceof Player && PlayerScale.emberProof((Player) entity)) {
			ci.cancel();
		}
	}
}
