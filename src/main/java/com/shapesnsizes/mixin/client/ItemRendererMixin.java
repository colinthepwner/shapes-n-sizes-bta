package com.shapesnsizes.mixin.client;

import com.shapesnsizes.PlayerScale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.ItemRenderer;
import net.minecraft.client.render.renderer.GLRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ItemRenderer.class, remap = false)
public class ItemRendererMixin {
	@Inject(
		method = "renderItemInFirstPerson",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/render/entity/MobRendererPlayer;drawHeldObject(Lnet/minecraft/core/entity/player/Player;F)V"
		)
	)
	private void shapesnsizes$worldSizedCarried(float partialTick, CallbackInfo ci) {

		float s = PlayerScale.get(Minecraft.getMinecraft().thePlayer);
		if (s != PlayerScale.DEFAULT) {
			float inv = 1.0f / s;
			GLRenderer.modelM4f().scale(inv, inv, inv);
		}
	}
}
