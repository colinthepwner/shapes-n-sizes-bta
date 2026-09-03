package com.shapesnsizes.mixin;

import com.shapesnsizes.PlayerScale;
import net.minecraft.core.block.piston.BlockLogicPistonBase;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = BlockLogicPistonBase.class, remap = false)
public class BlockLogicPistonBaseMixin {
	private static final float FLOOR = 0.4f;
	private static final float CEILING = 2.5f;

	@Redirect(
		method = "flingEntity",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/core/entity/Entity;fling(DDDF)V")
	)
	private void shapesnsizes$flingBySize(Entity entity, double vx, double vy, double vz, float strength) {
		float boost = 1.0f;
		if (entity instanceof Player) {
			float weight = PlayerScale.abilityFactor((Player) entity);
			if (weight != 1.0f) {
				boost = Math.max(FLOOR, Math.min(CEILING, (float) (1.0 / Math.sqrt(weight))));
			}
		}
		entity.fling(vx * boost, vy * boost, vz * boost, strength);
	}
}
