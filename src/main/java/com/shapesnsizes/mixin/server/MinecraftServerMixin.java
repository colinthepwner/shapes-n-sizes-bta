package com.shapesnsizes.mixin.server;

import com.shapesnsizes.ShapesConfig;
import net.minecraft.core.net.PropertyManager;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = MinecraftServer.class, remap = false)
public class MinecraftServerMixin {
	@Shadow public PropertyManager propertyManager;

	@Inject(method = "startServer", at = @At("RETURN"))
	private void shapesnsizes$readStartingSize(CallbackInfoReturnable<Boolean> cir) {
		if (this.propertyManager != null) {
			ShapesConfig.loadServerProperty(this.propertyManager);
		}
	}
}
