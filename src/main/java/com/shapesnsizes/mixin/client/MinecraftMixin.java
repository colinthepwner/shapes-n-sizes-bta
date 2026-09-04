package com.shapesnsizes.mixin.client;

import com.shapesnsizes.PlayerScale;
import com.shapesnsizes.ScaledPlayer;
import com.shapesnsizes.ShapesNSizes;
import com.shapesnsizes.StartingSize;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.PlayerLocal;
import net.minecraft.client.world.WorldClient;
import net.minecraft.core.entity.player.Player;
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

	@Inject(
		method = "changeWorld(Lnet/minecraft/client/world/WorldClient;Ljava/lang/String;Lnet/minecraft/core/entity/player/Player;)V",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/world/WorldClient;spawnPlayerWithLoadedChunks(Lnet/minecraft/core/entity/player/Player;Z)V",
			shift = At.Shift.AFTER
		)
	)
	private void shapesnsizes$startingSize(WorldClient world, String loadingTitle, Player carried, CallbackInfo ci) {
		PlayerLocal local = this.thePlayer;
		if (world == null || world.isClientSide || local == null) return;
		if (!(local instanceof ScaledPlayer) || ((ScaledPlayer) local).shapesnsizes$hasSavedScale()) return;
		StartingSize choice = PlayerScale.startingSize(world);
		float starting = choice.pick(world.rand);
		if (starting == PlayerScale.DEFAULT) return;
		PlayerScale.set(local, starting);
		PlayerScale.snapEase(local);
		ShapesNSizes.LOGGER.info("Starting this world at {}x, from {}.", PlayerScale.format(starting),
			choice == StartingSize.RANDOM ? "a random draw" : "the world's starting size");
	}
}
