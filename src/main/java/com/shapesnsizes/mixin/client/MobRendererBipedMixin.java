package com.shapesnsizes.mixin.client;

import com.shapesnsizes.PlayerScale;
import net.minecraft.client.render.entity.MobRendererBiped;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.client.render.tessellator.TessellatorGeneral;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MobRendererBiped.class, remap = false)
public class MobRendererBipedMixin {
	@Inject(
		method = "renderAdditional(Lnet/minecraft/client/render/tessellator/TessellatorGeneral;Lnet/minecraft/core/entity/Mob;F)V",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/model/ItemModelDispatcher;getInstance()Lnet/minecraft/client/render/item/model/ItemModelDispatcher;")
	)
	private void shapesnsizes$worldSizedHeldItem(TessellatorGeneral tessellator, Mob entity, float partialTick, CallbackInfo ci) {
		if (!(entity instanceof Player)) return;
		float s = PlayerScale.get((Player) entity);
		if (s != PlayerScale.DEFAULT) {
			float inv = 1.0f / s;
			GLRenderer.modelM4f().scale(inv, inv, inv);
		}
	}
}
