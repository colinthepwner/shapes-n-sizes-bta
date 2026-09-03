package com.shapesnsizes.mixin.server;

import com.shapesnsizes.PlayerScale;
import com.shapesnsizes.ScaledPlayer;
import com.shapesnsizes.ShapesConfig;
import com.shapesnsizes.ShapesNSizes;
import net.minecraft.core.util.helper.DyeColor;
import net.minecraft.server.entity.player.PlayerServer;
import net.minecraft.server.net.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PlayerList.class, remap = false)
public class PlayerListMixin {
	@Inject(method = "playerLoggedIn", at = @At("TAIL"))
	private void shapesnsizes$onLogin(PlayerServer player, CallbackInfo ci) {

		if (player instanceof ScaledPlayer && !((ScaledPlayer) player).shapesnsizes$hasSavedScale()) {
			float starting = ShapesConfig.startingScale(player.username);
			if (starting != PlayerScale.DEFAULT) {
				PlayerScale.set(player, starting);
				ShapesNSizes.LOGGER.info("{} starts at {}x, from the config.", player.username, PlayerScale.format(starting));
			}
		}
		PlayerScale.snapEase(player);
		PlayerScale.forceResync(player);
	}

	@Inject(method = "recreatePlayerEntity", at = @At("RETURN"))
	private void shapesnsizes$onRespawn(PlayerServer old, int dimension, CallbackInfoReturnable<PlayerServer> cir) {
		PlayerServer fresh = cir.getReturnValue();
		if (fresh == null) return;

		PlayerScale.clearBonus(fresh);
		if (fresh != old) {
			PlayerScale.set(fresh, PlayerScale.getBase(old));
		}
		PlayerScale.snapEase(fresh);
		PlayerScale.forceResync(fresh);
	}

	@Inject(method = "sendPlayerToOtherDimension", at = @At("TAIL"))
	private void shapesnsizes$onDimensionChange(PlayerServer player, int dimension, DyeColor color, boolean flag, CallbackInfo ci) {
		PlayerScale.snapEase(player);
		PlayerScale.forceResync(player);
	}
}
