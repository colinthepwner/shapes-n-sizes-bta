package com.shapesnsizes.mixin;

import com.shapesnsizes.PlayerScale;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.monster.MobCreeper;
import net.minecraft.core.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = MobCreeper.class, remap = false)
public class MobCreeperMixin {

	@ModifyVariable(method = "attackEntity", at = @At("HEAD"), argsOnly = true, ordinal = 0)
	private float shapesnsizes$standOffBySize(float value, Entity target, float distance) {
		if (!(target instanceof Player)) return value;
		if (PlayerScale.get((Player) target) == PlayerScale.DEFAULT) return value;

		double halfWidth = (target.bb.maxX - target.bb.minX) * 0.5;
		double slack = Math.min(halfWidth, MAX_STAND_OFF);
		return (float) Math.max(0.0, value - slack);
	}

	@Unique
	private static final double MAX_STAND_OFF = 3.0;
}
