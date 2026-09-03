package com.shapesnsizes.mixin.client;

import com.shapesnsizes.PlayerScale;
import net.minecraft.client.render.camera.EntityCamera;
import net.minecraft.client.render.camera.EntityCameraThirdPersonRear;
import net.minecraft.core.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = EntityCameraThirdPersonRear.class, remap = false)
public class EntityCameraThirdPersonRearMixin {
	private float shapesnsizes$factor() {
		Object mob = ((EntityCamera) (Object) this).mob;
		if (!(mob instanceof Player)) return 1.0f;
		return Math.max(1.0f, PlayerScale.get((Player) mob));
	}

	@ModifyConstant(method = "getCameraDistance", constant = @Constant(intValue = 5))
	private int shapesnsizes$steps(int steps) {
		return Math.round(steps * shapesnsizes$factor());
	}

	@ModifyConstant(method = "getCameraDistance", constant = @Constant(doubleValue = 0.5))
	private double shapesnsizes$clearance(double offset) {
		return offset * shapesnsizes$factor();
	}
}
