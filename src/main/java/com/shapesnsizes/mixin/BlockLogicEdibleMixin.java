package com.shapesnsizes.mixin;

import com.shapesnsizes.PlayerScale;
import net.minecraft.core.block.BlockLogicEdible;
import net.minecraft.core.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = BlockLogicEdible.class, remap = false)
public class BlockLogicEdibleMixin {
	@Redirect(
		method = "eatSlice",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/core/entity/player/Player;heal(I)V")
	)
	private void shapesnsizes$sliceIsSized(Player player, int amount) {
		player.heal(PlayerScale.nourishment(player, amount));
	}
}
