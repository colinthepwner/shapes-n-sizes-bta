package com.shapesnsizes.mixin;

import com.shapesnsizes.PlayerScale;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = World.class, remap = false)
public class WorldTargetingMixin {

	@Redirect(
		method = "getClosestPlayer",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/core/entity/player/Player;distanceToSqr(DDD)D")
	)
	private double shapesnsizes$noticeTheBigOnes(Player player, double x, double y, double z) {
		double distanceSqr = player.distanceToSqr(x, y, z);
		double presence = PlayerScale.presence(player);
		if (presence == 1.0) return distanceSqr;

		return distanceSqr / (presence * presence);
	}
}
