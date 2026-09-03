package com.shapesnsizes.mixin.client;

import com.shapesnsizes.PlayerScale;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = EntityRenderer.class, remap = false)
public class EntityRendererMixin {

	private static final float GAP = 0.8f;

	@Redirect(
		method = "renderFire",
		at = @At(value = "FIELD", target = "Lnet/minecraft/core/entity/Entity;bbHeight:F", opcode = org.objectweb.asm.Opcodes.GETFIELD)
	)
	private float shapesnsizes$fireHeightInFrameUnits(Entity entity) {
		if (!(entity instanceof Player)) return entity.bbHeight;
		float scale = PlayerScale.get((Player) entity);
		return scale == PlayerScale.DEFAULT ? entity.bbHeight : entity.bbHeight / scale;
	}

	@Redirect(
		method = "renderLivingLabel",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/core/entity/Entity;getHeadHeight()F")
	)
	private float shapesnsizes$liftLabel(Entity entity) {
		float head = entity.getHeadHeight();
		if (!(entity instanceof Player)) return head;
		float scale = PlayerScale.get((Player) entity);
		if (scale == PlayerScale.DEFAULT) return head;

		return head + GAP * (scale - 1.0f);
	}
}
