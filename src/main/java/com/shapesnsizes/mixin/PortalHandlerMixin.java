package com.shapesnsizes.mixin;

import com.shapesnsizes.PortalSizes;
import com.shapesnsizes.ShapesNSizes;
import net.minecraft.core.block.BlockLogicPortal;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.util.helper.DyeColor;
import net.minecraft.core.world.Dimension;
import net.minecraft.core.world.PortalHandler;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = PortalHandler.class, remap = false)
public class PortalHandlerMixin {
	@Redirect(
		method = "generatePortal",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/core/block/BlockLogicPortal;tryToCreatePortal(Lnet/minecraft/core/world/World;Lnet/minecraft/core/world/pos/TilePosc;Lnet/minecraft/core/util/helper/DyeColor;)Z"
		)
	)
	private boolean shapesnsizes$matchSourceSize(BlockLogicPortal logic, World portalWorld, TilePosc corner,
												 DyeColor color, World world, Entity entity,
												 DyeColor portalColor, Dimension oldDim, Dimension newDim) {
		PortalSizes.Shape wanted = PortalSizes.remembered(entity);
		if (wanted != null && wanted.isBiggerThanDefault()) {
			try {
				PortalSizes.enlargeFrame(portalWorld, corner, logic.portalFrame, wanted);
			} catch (RuntimeException e) {

				ShapesNSizes.LOGGER.warn("Could not widen the destination portal frame", e);
			}
		}
		return logic.tryToCreatePortal(portalWorld, corner, color);
	}
}
