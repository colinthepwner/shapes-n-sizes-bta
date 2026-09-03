package com.shapesnsizes.mixin.client;

import com.shapesnsizes.client.PreviewRender;
import net.minecraft.client.render.EntityRendererDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.tessellator.TessellatorGeneral;
import net.minecraft.core.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = EntityRendererDispatcher.class, remap = false)
public class EntityRendererDispatcherMixin {
	@SuppressWarnings({"unchecked", "rawtypes"})
	@Redirect(
		method = "renderEntityPreviewWithPosYaw",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/render/entity/EntityRenderer;renderPreview(Lnet/minecraft/client/render/tessellator/TessellatorGeneral;Lnet/minecraft/core/entity/Entity;DDDFF)V"
		)
	)
	private void shapesnsizes$markPreview(EntityRenderer renderer, TessellatorGeneral tessellator, Entity entity,
										  double x, double y, double z, float yaw, float partialTick) {
		PreviewRender.active = true;
		try {
			renderer.renderPreview(tessellator, entity, x, y, z, yaw, partialTick);
		} finally {
			PreviewRender.active = false;
		}
	}
}
