package com.shapesnsizes.mixin;

import com.shapesnsizes.PlayerScale;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import net.minecraft.core.item.ItemFood;
import net.minecraft.core.item.ItemStack;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Mob.class, remap = false)
public abstract class MobMixin {

	@Redirect(
		method = "moveEntityWithHeading",
		at = @At(value = "FIELD", target = "Lnet/minecraft/core/entity/Mob;fallDistance:F", opcode = Opcodes.PUTFIELD)
	)
	private void shapesnsizes$keepFallOnWalls(Mob self, float value) {
		if (self instanceof Player && PlayerScale.isWallClimbing((Player) self)) return;
		self.fallDistance = value;
	}

	@Redirect(
		method = "eatFood",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/core/item/ItemFood;getHealAmount(Lnet/minecraft/core/item/ItemStack;)I")
	)
	private int shapesnsizes$instantFood(ItemFood food, ItemStack stack) {
		return PlayerScale.nourishment((Mob) (Object) this, food.getHealAmount(stack));
	}

	@Redirect(
		method = "trySuffocate",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/core/entity/Mob;isUnderAcidOrWater()Z")
	)
	private boolean shapesnsizes$rainCountsAsWater(Mob self) {
		if (self.isUnderAcidOrWater()) return true;
		return self instanceof Player && PlayerScale.drowningInRain((Player) self);
	}

	@Redirect(
		method = "moveEntityWithHeading",
		at = @At(value = "FIELD", target = "Lnet/minecraft/core/block/Block;friction:F", opcode = Opcodes.GETFIELD)
	)
	private float shapesnsizes$slickSurfaceTension(Block<?> block) {
		Mob self = (Mob) (Object) this;
		if (self instanceof Player && block.hasTag(BlockTags.IS_WATER) && PlayerScale.canWaterWalk((Player) self)) {
			return Blocks.ICE.friction;
		}
		return block.friction;
	}

	@ModifyConstant(method = "moveEntityWithHeading", constant = @Constant(doubleValue = 0.25))
	private double shapesnsizes$climbUpSpeed(double speed) {
		Mob self = (Mob) (Object) this;
		if (!(self instanceof Player)) return speed;
		return PlayerScale.climbPush((Player) self);
	}

	@ModifyConstant(method = "moveEntityWithHeading", constant = @Constant(doubleValue = -0.25))
	private double shapesnsizes$climbSlideSpeed(double speed) {
		return speed * shapesnsizes$climbScale();
	}

	@Unique
	private float shapesnsizes$climbScale() {
		Mob self = (Mob) (Object) this;
		return self instanceof Player ? PlayerScale.climbFactor((Player) self) : 1.0f;
	}

	@Inject(method = "knockBack", at = @At("HEAD"), cancellable = true)
	private void shapesnsizes$sizedKnockback(Entity striker, int damage, double dx, double dz, CallbackInfo ci) {
		Mob struck = (Mob) (Object) this;
		float scale = PlayerScale.knockbackBetween(striker, struck);
		if (scale == 1.0f) return;

		float strength = 0.4f * scale;
		float distance = MathHelper.sqrt(dx * dx + dz * dz);
		if (distance == 0.0f) return;

		struck.xd /= 2.0;
		struck.yd /= 2.0;
		struck.zd /= 2.0;
		struck.xd -= dx / (double) distance * (double) strength;
		struck.yd += strength;
		struck.zd -= dz / (double) distance * (double) strength;
		if (struck.yd > (double) strength) {
			struck.yd = strength;
		}
		ci.cancel();
	}

	@Inject(method = "moveEntityWithHeading", at = @At("TAIL"))
	private void shapesnsizes$gaitInBodyLengths(float moveStrafing, float moveForward, CallbackInfo ci) {
		Mob self = (Mob) (Object) this;
		if (!(self instanceof Player)) return;
		float scale = PlayerScale.get((Player) self);
		if (scale == PlayerScale.DEFAULT) return;

		double dx = self.x - self.xo;
		double dz = self.z - self.zo;
		float raw = (float) Math.sqrt(dx * dx + dz * dz) * 4.0f;
		float vanillaTarget = Math.min(raw, 1.0f);
		float wantedTarget = Math.min(raw / scale, 1.0f);

		float previous = self.walkAnimSpeedO;
		float vanillaSpeed = previous + (vanillaTarget - previous) * 0.4f;
		float wantedSpeed = previous + (wantedTarget - previous) * 0.4f;

		self.walkAnimPos += wantedSpeed - vanillaSpeed;
		self.walkAnimSpeed = wantedSpeed;
	}
}
