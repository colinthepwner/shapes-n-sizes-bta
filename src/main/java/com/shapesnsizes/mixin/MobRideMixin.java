package com.shapesnsizes.mixin;

import com.shapesnsizes.Piggyback;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Mob.class, remap = false)
public class MobRideMixin {
	@Inject(method = "interact", at = @At("RETURN"), cancellable = true)
	private void shapesnsizes$stringBridle(Player player, CallbackInfoReturnable<Boolean> cir) {
		if (cir.getReturnValueZ()) return;
		Mob self = (Mob) (Object) this;
		if (self instanceof Player) return;
		if (self.passenger != null || player.vehicle != null) return;
		if (!Piggyback.canMount(player, self)) return;

		if (!self.world.isClientSide) {
			Piggyback.ride(player, self);
		}
		cir.setReturnValue(true);
	}
}
