package com.shapesnsizes.mixin;

import com.shapesnsizes.Ridable;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.entity.vehicle.EntityMinecart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EntityMinecart.class, remap = false)
public class EntityMinecartMixin {
	@Inject(method = "interact", at = @At("HEAD"), cancellable = true)
	private void shapesnsizes$tooBigForRails(Player player, CallbackInfoReturnable<Boolean> cir) {
		if (player == null || Ridable.fitsAboard(player)) return;
		if (Ridable.crush(player, (EntityMinecart) (Object) this)) {
			player.sendStatusMessage("§7The cart buckles under you.");
		}
		cir.setReturnValue(true);
	}
}
