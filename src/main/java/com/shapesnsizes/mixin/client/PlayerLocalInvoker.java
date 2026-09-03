package com.shapesnsizes.mixin.client;

import net.minecraft.client.entity.player.PlayerLocal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = PlayerLocal.class, remap = false)
public interface PlayerLocalInvoker {
	@Invoker("checkAndPushInTile")
	boolean shapesnsizes$checkAndPushInTile(double x, double y, double z);
}
