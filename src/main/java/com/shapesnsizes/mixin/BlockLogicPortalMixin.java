package com.shapesnsizes.mixin;

import com.shapesnsizes.PortalSizes;
import net.minecraft.core.block.BlockLogicPortal;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BlockLogicPortal.class, remap = false)
public class BlockLogicPortalMixin {
	@Inject(method = "onEntityCollision", at = @At("HEAD"))
	private void shapesnsizes$rememberPortalShape(World world, TilePosc tilePos, Entity entity, CallbackInfo ci) {
		if (!PortalSizes.carries(entity)) return;
		PortalSizes.Shape shape = PortalSizes.measure(world, tilePos);
		if (shape != null) {
			PortalSizes.remember(entity, shape);
		}
	}
}
