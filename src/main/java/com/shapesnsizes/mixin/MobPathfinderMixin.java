package com.shapesnsizes.mixin;

import com.shapesnsizes.PlayerScale;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.MobPathfinder;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pathfinder.Path;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = MobPathfinder.class, remap = false)
public class MobPathfinderMixin {
	@Redirect(
		method = "updateAI",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/core/world/World;getPathToEntity(Lnet/minecraft/core/entity/Entity;Lnet/minecraft/core/entity/Entity;F)Lnet/minecraft/core/world/pathfinder/Path;"
		)
	)
	private Path shapesnsizes$reachAsFarAsYouLook(World world, Entity walker, Entity target, float distance) {
		if (target instanceof Player) {
			double presence = PlayerScale.presence((Player) target);

			if (presence > 1.0) {
				distance = (float) Math.min(distance * presence, MAX_PATH);
			}
		}
		return world.getPathToEntity(walker, target, distance);
	}

	@Unique
	private static final double MAX_PATH = 48.0;
}
