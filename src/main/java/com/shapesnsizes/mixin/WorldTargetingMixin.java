package com.shapesnsizes.mixin;

import com.shapesnsizes.PlayerScale;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = World.class, remap = false)
public class WorldTargetingMixin {

	private static final double FLOOR = 0.6;
	private static final double CEILING = 3.0;

	@Redirect(
		method = "getClosestPlayer",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/core/entity/player/Player;distanceToSqr(DDD)D")
	)
	private double shapesnsizes$noticeTheBigOnes(Player player, double x, double y, double z) {
		double distanceSqr = player.distanceToSqr(x, y, z);
		float weight = PlayerScale.abilityFactor(player);
		if (weight == 1.0f) return distanceSqr;
		double presence = Math.max(FLOOR, Math.min(CEILING, Math.sqrt(weight)));
		return distanceSqr / (presence * presence);
	}
}
