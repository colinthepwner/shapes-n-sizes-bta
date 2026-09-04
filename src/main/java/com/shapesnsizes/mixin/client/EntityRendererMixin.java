package com.shapesnsizes.mixin.client;

import com.shapesnsizes.PlayerScale;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import net.minecraft.client.render.tessellator.TessellatorGeneral;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = EntityRenderer.class, remap = false)
public class EntityRendererMixin {

	private static final float GAP = 0.8f;

	@ModifyConstant(method = "renderLivingLabel", constant = {
		@Constant(floatValue = 0.026666667f),
		@Constant(floatValue = -0.026666667f)
	})
	private float shapesnsizes$labelTextSize(float vanilla, TessellatorGeneral t, Entity entity,
			CharSequence text, double x, double y, double z, int maxDistance, boolean depthTest) {
		if (!(entity instanceof Player)) return vanilla;
		return vanilla * PlayerScale.labelScale((Player) entity);
	}

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
