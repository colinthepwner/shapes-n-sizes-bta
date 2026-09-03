package com.shapesnsizes.mixin;

import com.shapesnsizes.PlayerScale;
import net.minecraft.core.block.BlockLogicPressurePlate;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = BlockLogicPressurePlate.class, remap = false)
public abstract class BlockLogicPressurePlateMixin {
	@Redirect(
		method = "updateState",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/core/entity/Entity;canInteract()Z")
	)
	private boolean shapesnsizes$heavyEnough(Entity entity) {
		if (!entity.canInteract()) return false;
		if (entity instanceof Player) {
			BlockLogicPressurePlate<?> self = (BlockLogicPressurePlate<?>) (Object) this;
			return !PlayerScale.tooLightForPlate((Player) entity, self.getMaterial());
		}
		return true;
	}
}
