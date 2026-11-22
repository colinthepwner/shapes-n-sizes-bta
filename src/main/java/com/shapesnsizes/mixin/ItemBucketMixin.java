package com.shapesnsizes.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.shapesnsizes.PlayerScale;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemBucket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ItemBucket.class, remap = false)
public class ItemBucketMixin {
	@ModifyExpressionValue(
		method = "onUse",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/core/player/gamemode/Gamemode;getBlockReachDistance()F")
	)
	private float shapesnsizes$scaledReach(float reach, @Local(argsOnly = true) Player player) {
		return reach * PlayerScale.abilityFactor(player);
	}
}
