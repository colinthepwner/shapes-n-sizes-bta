package com.shapesnsizes.mixin;

import com.shapesnsizes.Ridable;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.entity.vehicle.EntityBoat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EntityBoat.class, remap = false)
public class EntityBoatMixin {
	@Inject(method = "interact", at = @At("HEAD"), cancellable = true)
	private void shapesnsizes$tooBigToBoard(Player player, CallbackInfoReturnable<Boolean> cir) {
		if (player == null || Ridable.fitsAboard(player)) return;
		if (Ridable.crush(player, (EntityBoat) (Object) this)) {
			player.sendStatusMessage("§7The boat gives under you.");
		}
		cir.setReturnValue(true);
	}
}
