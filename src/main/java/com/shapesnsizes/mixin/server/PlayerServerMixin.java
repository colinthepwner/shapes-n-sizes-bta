package com.shapesnsizes.mixin.server;

import com.shapesnsizes.SizeTicker;
import net.minecraft.server.entity.player.PlayerServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PlayerServer.class, remap = false)
public class PlayerServerMixin {
	@Inject(method = "tick", at = @At("TAIL"))
	private void shapesnsizes$sizeTickOnServer(CallbackInfo ci) {
		((SizeTicker) this).shapesnsizes$sizeTick();
	}
}
