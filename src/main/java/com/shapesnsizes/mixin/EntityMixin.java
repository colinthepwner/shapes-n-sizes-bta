package com.shapesnsizes.mixin;

import net.minecraft.core.block.Block;
import net.minecraft.core.enums.EnumBlockSoundEffectType;
import net.minecraft.core.world.World;
import com.shapesnsizes.PlayerScale;
import com.shapesnsizes.Reach;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Entity.class, remap = false)
public class EntityMixin {
	@ModifyConstant(method = "move", constant = @Constant(doubleValue = 0.25))
	private double shapesnsizes$stuckHorizontal(double drag) {
		return PlayerScale.relieveDrag((Entity) (Object) this, drag);
	}

	@ModifyConstant(method = "move", constant = @Constant(doubleValue = 0.05, ordinal = 0))
	private double shapesnsizes$cobwebVertical(double drag) {
		return PlayerScale.relieveDrag((Entity) (Object) this, drag);
	}

	@ModifyConstant(method = "move", constant = @Constant(doubleValue = 0.6))
	private double shapesnsizes$strideLength(double perBlock) {
		Entity self = (Entity) (Object) this;
		if (!(self instanceof Player)) return perBlock;
		float scale = PlayerScale.get((Player) self);
		return scale == PlayerScale.DEFAULT ? perBlock : perBlock / scale;
	}

	@Redirect(
		method = "move",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/core/entity/Entity;isSneaking()Z", ordinal = 0)
	)
	private boolean shapesnsizes$edgeGuardWantsCrouch(Entity self) {
		return self instanceof Player ? PlayerScale.sneakingOnPurpose((Player) self) : self.isSneaking();
	}

	@Redirect(
		method = "move",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/core/entity/Entity;isSneaking()Z", ordinal = 2)
	)
	private boolean shapesnsizes$stepDownWantsCrouch(Entity self) {
		return self instanceof Player ? PlayerScale.sneakingOnPurpose((Player) self) : self.isSneaking();
	}

	@ModifyConstant(method = "move", constant = @Constant(doubleValue = -1.0))
	private double shapesnsizes$edgeGuardReach(double drop) {
		Entity self = (Entity) (Object) this;
		if (!(self instanceof Player)) return drop;
		return -PlayerScale.edgeGuardDrop((Player) self);
	}

	@Inject(method = "distanceToSqr(DDD)D", at = @At("RETURN"), cancellable = true)
	private void shapesnsizes$containerReach(double x, double y, double z, CallbackInfoReturnable<Double> cir) {
		double allowance = Reach.allowanceFor((Entity) (Object) this);
		if (allowance != 1.0) cir.setReturnValue(cir.getReturnValueD() / allowance);
	}
}
