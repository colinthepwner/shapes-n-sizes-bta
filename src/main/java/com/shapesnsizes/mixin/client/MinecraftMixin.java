package com.shapesnsizes.mixin.client;

import com.shapesnsizes.PlayerScale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.PlayerLocal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Minecraft.class, remap = false)
public class MinecraftMixin {
	@Shadow public PlayerLocal thePlayer;

	@Unique private float shapesnsizes$scaleBeforeRespawn = PlayerScale.DEFAULT;

	@Inject(method = "respawn", at = @At("HEAD"))
	private void shapesnsizes$rememberScale(boolean multiplayer, int targetDimension, CallbackInfo ci) {

		this.shapesnsizes$scaleBeforeRespawn = PlayerScale.getBase(this.thePlayer);
	}

	@Inject(method = "respawn", at = @At("TAIL"))
	private void shapesnsizes$restoreScale(boolean multiplayer, int targetDimension, CallbackInfo ci) {
		if (this.thePlayer != null && this.shapesnsizes$scaleBeforeRespawn != PlayerScale.DEFAULT) {
			PlayerScale.set(this.thePlayer, this.shapesnsizes$scaleBeforeRespawn);
		}
	}
}
