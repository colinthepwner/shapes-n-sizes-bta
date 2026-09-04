package com.shapesnsizes.mixin.client;

import com.shapesnsizes.PlayerScale;
import net.minecraft.client.render.entity.MobRenderer;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = MobRenderer.class, remap = false)
public class MobRendererLimbMixin {
	@Inject(method = "getLimbYaw", at = @At("RETURN"), cancellable = true)
	private void shapesnsizes$fullArcForBigBodies(Mob entity, float partialTick, CallbackInfoReturnable<Float> cir) {
		if (!(entity instanceof Player)) return;
		Player player = (Player) entity;
		float scale = PlayerScale.get(player);
		if (scale == PlayerScale.DEFAULT) return;

		float pace = Math.max(0.05f, PlayerScale.speedFactor(player));
		float boost = Math.max(1.0f, scale / pace);
		if (boost == 1.0f) return;
		cir.setReturnValue(Math.min(1.0f, cir.getReturnValueF() * boost));
	}
}
